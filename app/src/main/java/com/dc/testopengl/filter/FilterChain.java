package com.dc.testopengl.filter;

import android.opengl.GLES20;

import java.util.LinkedList;

/**
 * 滤镜链
 * Created by duancong on 21/07/2018.
 */

public class FilterChain {

    private int[] mTempTexture = new int[2];

    private LinkedList<Filter> mList;
    private int OESTexture;

    public FilterChain() {
        mList = new LinkedList<>();
    }

    public void add(Filter filter) {
        mList.add(filter);
    }

    public void remove(Filter filter) {
        mList.remove(filter);
    }

    public void glInit() {
        GLES20.glGenTextures(mTempTexture.length, mTempTexture, 0);
        for (Filter filter : mList) {
            filter.init();
        }
    }

    public void glDestroy() {
        for (Filter filter : mList) {
            filter.destroy();
        }
        GLES20.glDeleteTextures(mTempTexture.length, mTempTexture, 0);
    }

    public void onDraw(int width, int height) {
        int tempInTex = mTempTexture[0];
        int tempOutTex = mTempTexture[1];
        Filter head = mList.getFirst();
        if(head instanceof OES2RGBAFilter){
            head.process(OESTexture, tempInTex);
        }

        Filter currentFilter;
        if (head != null) {
            for (int i = 1; i < mList.size(); i++) {
                currentFilter = mList.get(i);
                currentFilter.process(tempInTex, tempOutTex);
                tempInTex = tempOutTex;
            }
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
}
