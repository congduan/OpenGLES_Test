package com.dc.testopengl.filter;

import android.opengl.GLES11Ext;
import android.opengl.GLES20;

import com.dc.testopengl.shader.BaseShader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by duancong on 21/07/2018.
 */

class CropFilter extends Filter {

    int[] mFrameBuffers = new int[1];
    //每行前两个值为顶点坐标，后两个为纹理坐标
    private float[] vertexData = {
            1f, 1f, 1f, 1f,
            -1f, 1f, 0f, 1f,
            -1f, -1f, 0f, 0f,
            1f, 1f, 1f, 1f,
            -1f, -1f, 0f, 0f,
            1f, -1f, 1f, 0f
    };

    private boolean isCropped = false;

    private FloatBuffer mDataBuffer;

    public CropFilter() {
        super(new BaseShader());
    }

    @Override
    public void init() {
        super.init();
        GLES20.glGenFramebuffers(1, mFrameBuffers, 0);

        mDataBuffer = ByteBuffer.allocateDirect(vertexData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mDataBuffer.put(vertexData, 0, vertexData.length).position(0);
    }

    @Override
    public void destroy() {
        super.destroy();
        GLES20.glDeleteFramebuffers(1, mFrameBuffers, 0);
    }

    @Override
    public void process(int inTex, int outTex, int inputWidth, int inputHeight, int outputWidth, int outputHeight) {

        if (!isCropped) {
            crop(inputWidth, inputHeight, outputWidth, outputHeight);
        }

        process(inTex, outTex, outputWidth, outputHeight);
    }

    private void crop(int inputWidth, int inputHeight, int outputWidth, int outputHeight) {
        float k = ((float) outputHeight / outputWidth) * ( (float) inputWidth / inputHeight);
        vertexData[4 * 0 + 3] = (1+k)/2;
        vertexData[4 * 1 + 3] = (1+k)/2;
        vertexData[4 * 2 + 3] = (1-k)/2;
        vertexData[4 * 3 + 3] = (1+k)/2;
        vertexData[4 * 4 + 3] = (1-k)/2;
        vertexData[4 * 5 + 3] = (1-k)/2;

        mDataBuffer.put(vertexData, 0, vertexData.length).position(0);
        isCropped = true;
    }

    private void process(int inTex, int outTex, int width, int height) {

        GLES20.glUseProgram(mShaderProgram);

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffers[0]);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, outTex);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0,
                GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        // set texture as colour attachment
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D, outTex, 0);
        GLES20.glViewport(0, 0, width, height);

        int aPositionLocation = GLES20.glGetAttribLocation(mShaderProgram, "position");
        int aTextureCoordLocation = GLES20.glGetAttribLocation(mShaderProgram, "inputTextureCoordinate");
        int uTextureSamper = GLES20.glGetUniformLocation(mShaderProgram, "textureCoordinate");
        GLES20.glUniform1i(uTextureSamper, 0);

        // 输入纹理采样
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, inTex);
        if (mDataBuffer != null) {
            mDataBuffer.position(0);
            GLES20.glEnableVertexAttribArray(aPositionLocation);
            GLES20.glVertexAttribPointer(aPositionLocation, 2, GLES20.GL_FLOAT, false, 16, mDataBuffer);

            mDataBuffer.position(2);
            GLES20.glEnableVertexAttribArray(aTextureCoordLocation);
            GLES20.glVertexAttribPointer(aTextureCoordLocation, 2, GLES20.GL_FLOAT, false, 16, mDataBuffer);
        }
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);

        GLES20.glDisableVertexAttribArray(aPositionLocation);
        GLES20.glDisableVertexAttribArray(aTextureCoordLocation);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
    }

}
