package com.dc.testopengl.filter;

import android.opengl.GLES11Ext;
import android.opengl.GLES20;

import com.dc.testopengl.shader.OESShader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by duancong on 21/07/2018.
 */

class OES2RGBAFilter extends Filter {

    int[] mFrameBuffers = new int[1];
    //每行前两个值为顶点坐标，后两个为纹理坐标
    private static final float[] vertexData = {
            1f,  1f,  1f,  1f,
            -1f,  1f,  0f,  1f,
            -1f, -1f,  0f,  0f,
            1f,  1f,  1f,  1f,
            -1f, -1f,  0f,  0f,
            1f, -1f,  1f,  0f
    };

    private FloatBuffer mDataBuffer;

    public OES2RGBAFilter(){
        super(new OESShader());
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
        processTexOESToTex2D(inTex, outTex, outputWidth, outputHeight, getTransformMatrix());
    }

    private void processTexOESToTex2D(int mOESTextureId, int outTex, int width, int height, float[] matrix) {

        GLES20.glUseProgram(mShaderProgram);

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffers[0]);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, outTex);
        // 绑定一个空的glTexImage2D，方便framebuffer 填充数据
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

        //获取Shader中定义的变量在program中的位置
        int aPositionLocation = GLES20.glGetAttribLocation(mShaderProgram, "aPosition");
        int aTextureCoordLocation = GLES20.glGetAttribLocation(mShaderProgram, "aTextureCoordinate");
        int uTextureMatrixLocation = GLES20.glGetUniformLocation(mShaderProgram, "uTextureMatrix");
        int uTextureSamplerLocation = GLES20.glGetUniformLocation(mShaderProgram, "uTextureSampler");

        //激活纹理单元0
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        //绑定纹理到纹理单元0
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mOESTextureId);
        //将此纹理单元床位片段着色器的uTextureSampler外部纹理采样器
        GLES20.glUniform1i(uTextureSamplerLocation, 0);

        //将纹理矩阵传给片段着色器
        GLES20.glUniformMatrix4fv(uTextureMatrixLocation, 1, false, matrix, 0);

        //将顶点和纹理坐标传给顶点着色器
        if (mDataBuffer != null) {
            //顶点坐标从位置0开始读取
            mDataBuffer.position(0);
            //使能顶点属性
            GLES20.glEnableVertexAttribArray(aPositionLocation);
            //顶点坐标每次读取两个顶点值，之后间隔16（每行4个值 * 4个字节）的字节继续读取两个顶点值
            GLES20.glVertexAttribPointer(aPositionLocation, 2, GLES20.GL_FLOAT, false, 16, mDataBuffer);

            //纹理坐标从位置2开始读取
            mDataBuffer.position(2);
            GLES20.glEnableVertexAttribArray(aTextureCoordLocation);
            //纹理坐标每次读取两个顶点值，之后间隔16（每行4个值 * 4个字节）的字节继续读取两个顶点值
            GLES20.glVertexAttribPointer(aTextureCoordLocation, 2, GLES20.GL_FLOAT, false, 16, mDataBuffer);
        }

        //绘制两个三角形（6个顶点）
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
