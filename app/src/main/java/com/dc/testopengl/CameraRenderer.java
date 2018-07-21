package com.dc.testopengl;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.provider.Settings;
import android.util.Log;
import android.util.Size;

import com.dc.testopengl.camera.CameraManager;
import com.dc.testopengl.filter.FilterChain;
import com.dc.testopengl.filter.TestFilterChain;
import com.dc.testopengl.test.Triangle;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by duancong on 2017/12/4.
 */

public class CameraRenderer implements GLSurfaceView.Renderer {

    public interface SurfaceTextureInitCallback {
        void onInit(SurfaceTexture surfaceTexture);
        void onFpsUpdate(float fps);
    }

    private static final String TAG = "congduan";

    Context mContext;

    private int width;
    private int height;

    private GLSurfaceView mGLSurfaceView;

    private int mOESTextureId;
    private SurfaceTexture mSurfaceTexture;
    private SurfaceTextureInitCallback mCallback;
    private float[] mTransformMatrix = new float[16];

    private FilterChain mFilterChain;

    private float mFps;
    private int mFrameCount = 0;
    private static final int FPS_FRAME_INTERVAL = 10;
    private long lastTime = 0L;
    private Triangle mTriangle;
    private float[] matrix = {
            1,0,0,0,
            0,1,0,0,
            0,0,1,0,
            0,0,0,1,
    };

    public CameraRenderer(Context context, GLSurfaceView glView, SurfaceTextureInitCallback callback) {
        mContext = context;
        this.mGLSurfaceView = glView;
        this.mCallback = callback;
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        if (mGLSurfaceView == null) {
            Log.i(TAG, "onSurfaceCreated: glView is null");
        }

        mFilterChain = new TestFilterChain();
        mOESTextureId = createOESTextureObject();
        mFilterChain.glInit();
        initSurfaceTexture();
        mTriangle = new Triangle();
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        this.width = width;
        this.height = height;
        Log.i(TAG, "onSurfaceChanged: w=" + width + ", h=" + height);
        GLES20.glViewport(0,0,width,height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        long timeMillis = System.currentTimeMillis();

        if (mSurfaceTexture != null) {
            //更新纹理图像
            mSurfaceTexture.updateTexImage();
            //获取外部纹理的矩阵，用来确定纹理的采样位置，没有此矩阵可能导致图像翻转等问题
            mSurfaceTexture.getTransformMatrix(mTransformMatrix);
        }

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        ((TestFilterChain) mFilterChain).setTransfromMatrix(mTransformMatrix);
        ((TestFilterChain) mFilterChain).setOESTexture(mOESTextureId);
        mFilterChain.onDraw(width, height);


        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE);
        mTriangle.draw(matrix);

        GLES20.glDisable(GLES20.GL_BLEND);

        if(lastTime == 0){
            lastTime = timeMillis;
            return;
        }
        mFrameCount++;
        mFps += 1000.0f / (timeMillis - lastTime);
        if(mFrameCount == FPS_FRAME_INTERVAL){
            mFps /= FPS_FRAME_INTERVAL;
            if(mCallback != null){
                mCallback.onFpsUpdate(mFps);
            }
            mFps = 0;
            mFrameCount = 0;
        }
        lastTime = timeMillis;
    }

    public static int createOESTextureObject() {
        int[] tex = new int[1];
        //生成一个纹理
        GLES20.glGenTextures(1, tex, 0);
        //将此纹理绑定到外部纹理上
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, tex[0]);
        //设置纹理过滤参数
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
        //解除纹理绑定
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
        return tex[0];
    }

    //在onDrawFrame方法中调用此方法
    public boolean initSurfaceTexture() {
        //根据外部纹理ID创建SurfaceTexture
        mSurfaceTexture = new SurfaceTexture(mOESTextureId);
        mSurfaceTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
            @Override
            public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                //每获取到一帧数据时请求OpenGL ES进行渲染
                mGLSurfaceView.requestRender();
            }
        });
        if (mCallback != null) {
            mCallback.onInit(mSurfaceTexture);
        }
        return true;
    }
}
