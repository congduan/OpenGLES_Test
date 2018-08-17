package com.dc.testopengl.filter;

import android.graphics.Bitmap;
import android.media.FaceDetector;
import android.opengl.GLES20;

import com.dc.testopengl.Utils;
import com.dc.testopengl.camera.CameraManager;

/**
 * 滤镜链
 * Created by duancong on 21/07/2018.
 */

public class FilterChain {

    private int[] mTempTexture = new int[2];
    private int OESTexture;

    private FaceDetector mFaceDetector;
    FaceDetector.Face[] faces = new FaceDetector.Face[1];

    OES2RGBAFilter oes2RGBAFilter = new OES2RGBAFilter();
    CropFilter cropFilter = new CropFilter();
    PreviewFilter previewFilter = new PreviewFilter();

    public FilterChain() {
    }

    public void glInit() {
        GLES20.glGenTextures(mTempTexture.length, mTempTexture, 0);

        oes2RGBAFilter.init();
        cropFilter.init();
        previewFilter.init();
    }

    public void glDestroy() {
        GLES20.glDeleteTextures(mTempTexture.length, mTempTexture, 0);
        oes2RGBAFilter.destroy();
        cropFilter.destroy();
        previewFilter.destroy();
    }

    int cameraWidth, cameraHeight;
    void updateSize(){
        if(cameraWidth == 0 && cameraHeight == 0) {
            cameraWidth = CameraManager.getInstance().getCameraSize().getWidth();
            cameraHeight = CameraManager.getInstance().getCameraSize().getHeight();
        }
    }

    public void onDraw(int width, int height) {
        updateSize();
        initFaceDetector(width, height);

        int tempInTex = mTempTexture[0];
        int tempOutTex = mTempTexture[1];
        oes2RGBAFilter.process(OESTexture, tempInTex, this.cameraHeight, this.cameraWidth, this.cameraHeight, this.cameraWidth);
        cropFilter.process(tempInTex, tempOutTex,this.cameraHeight, this.cameraWidth, width, height);

//        Bitmap bitmap = Utils.saveTexture(tempOutTex, width, height);
//        mFaceDetector.findFaces(bitmap, faces);
//        bitmap.recycle();

        previewFilter.process(tempOutTex, tempInTex, width, height, width, height);
    }

    public FaceDetector.Face[] getFaces(){
        return faces;
    }

    private void initFaceDetector(int width, int height) {
        if(mFaceDetector == null) {
            mFaceDetector = new FaceDetector(width, height, 1);
        }
    }

    /**
     * 拷贝texture
     * 先render到fbo，再从fbo拷贝出来
     *
     * @param srcTex
     * @param dstTex
     */
    private void copyTexture(int srcTex, int dstTex, int width, int height) {
        // 创建fbo
        int[] fbos = new int[1];
        GLES20.glGenFramebuffers(1, fbos, 0);
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, fbos[0]);
        // 绑定源texture
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER,
                GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D,
                srcTex, 0);

        // 绑定目标texture
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, dstTex);
        GLES20.glCopyTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, 0, 0, width, height, 0);

        // 还原屏幕fbo绑定
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        GLES20.glDeleteFramebuffers(1, fbos, 0);
    }

    public void setOESTexture(int OESTexture) {
        this.OESTexture = OESTexture;
    }

    public void setTransfromMatrix(float[] transfromMatrix) {
        oes2RGBAFilter.setTransformMatrix(transfromMatrix);
    }
}
