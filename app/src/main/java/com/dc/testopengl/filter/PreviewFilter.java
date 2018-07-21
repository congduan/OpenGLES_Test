package com.dc.testopengl.filter;

import android.opengl.GLES20;

import com.dc.testopengl.shader.BaseShader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by duancong on 21/07/2018.
 */

public class PreviewFilter extends Filter {

    //每行前两个值为顶点坐标，后两个为纹理坐标
    private static final float[] vertexData = {
            1f, 1f, 1f, 1f,
            -1f, 1f, 0f, 1f,
            -1f, -1f, 0f, 0f,
            1f, 1f, 1f, 1f,
            -1f, -1f, 0f, 0f,
            1f, -1f, 1f, 0f
    };

    private FloatBuffer mDataBuffer;

    public PreviewFilter() {
        super(new BaseShader());
    }

    @Override
    public void init() {
        super.init();
        mDataBuffer = createBuffer(vertexData);
    }

    @Override
    public void process(int inTex, int outTex, int inputWidth, int inputHeight, int outputWidth, int outputHeight) {
        draw(inTex);
    }

    public FloatBuffer createBuffer(float[] vertexData) {
        FloatBuffer buffer = ByteBuffer.allocateDirect(vertexData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        buffer.put(vertexData, 0, vertexData.length).position(0);
        return buffer;
    }

    private void draw(int textureId) {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

        GLES20.glUseProgram(mShaderProgram);
        int aPositionLocation = GLES20.glGetAttribLocation(mShaderProgram, "position");
        int aTextureCoordLocation = GLES20.glGetAttribLocation(mShaderProgram, "inputTextureCoordinate");
        int uTextureSamper = GLES20.glGetUniformLocation(mShaderProgram, "textureCoordinate");

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
//        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
//        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
//        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
//        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

        GLES20.glUniform1i(uTextureSamper, 0);

        if (mDataBuffer != null) {
            mDataBuffer.position(0);
            GLES20.glEnableVertexAttribArray(aPositionLocation);
            GLES20.glVertexAttribPointer(aPositionLocation, 2, GLES20.GL_FLOAT, false, 16, mDataBuffer);

            mDataBuffer.position(2);
            GLES20.glEnableVertexAttribArray(aTextureCoordLocation);
            GLES20.glVertexAttribPointer(aTextureCoordLocation, 2, GLES20.GL_FLOAT, false, 16, mDataBuffer);
        }

        //绘制两个三角形（6个顶点）
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
    }
}
