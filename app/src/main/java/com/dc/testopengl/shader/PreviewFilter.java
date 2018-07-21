package com.dc.testopengl.shader;

import android.opengl.GLES11Ext;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by duancong on 21/07/2018.
 */

public class PreviewFilter extends Filter {

    private static final String VERTEX_SHADER = "" +
            //顶点坐标
            "attribute vec4 aPosition;\n" +
            //纹理矩阵
            "uniform mat4 uTextureMatrix;\n" +
            //自己定义的纹理坐标
            "attribute vec4 aTextureCoordinate;\n" +
            //传给片段着色器的纹理坐标
            "varying vec2 vTextureCoord;\n" +
            "void main()\n" +
            "{\n" +
            //根据自己定义的纹理坐标和纹理矩阵求取传给片段着色器的纹理坐标
            "  vTextureCoord = (uTextureMatrix * aTextureCoordinate).xy;\n" +
            "  gl_Position = aPosition;\n" +
            "}\n";

    private static final String FRAGMENT_SHADER = "" +
            //使用外部纹理必须支持此扩展
            "#extension GL_OES_EGL_image_external : require\n" +
            "precision mediump float;\n" +
            //外部纹理采样器
            "uniform samplerExternalOES uTextureSampler;\n" +
            "varying vec2 vTextureCoord;\n" +
            "void main() \n" +
            "{\n" +
            //获取此纹理（预览图像）对应坐标的颜色值
            "  vec4 vCameraColor = texture2D(uTextureSampler, vTextureCoord);\n" +
            //将此灰度值作为输出颜色的RGB值，这样就会变成黑白滤镜
            "  gl_FragColor = vCameraColor;\n" +
            "}\n";

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

    public PreviewFilter(){
        super(VERTEX_SHADER, FRAGMENT_SHADER);
    }

    public void compile(){
        mDataBuffer = createBuffer(vertexData);
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, VERTEX_SHADER);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER);
        mShaderProgram = linkProgram(vertexShader, fragmentShader);
    }

    public FloatBuffer createBuffer(float[] vertexData) {
        FloatBuffer buffer = ByteBuffer.allocateDirect(vertexData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        buffer.put(vertexData, 0, vertexData.length).position(0);
        return buffer;
    }

    public void draw(float[] transformMatrix, int mOESTextureId){
        //glClear(GL_COLOR_BUFFER_BIT);
        GLES20.glClearColor(1.0f, 0.0f, 0.0f, 0.0f);

        //获取Shader中定义的变量在program中的位置
        int aPositionLocation = GLES20.glGetAttribLocation(mShaderProgram, "aPosition");
        int aTextureCoordLocation = GLES20.glGetAttribLocation(mShaderProgram, "aTextureCoordinate");
        int uTextureMatrixLocation = GLES20.glGetUniformLocation(mShaderProgram, "uTextureMatrix");
        int uTextureSamplerLocation = GLES20.glGetUniformLocation(mShaderProgram, "uTextureSampler");

        //激活纹理单元0
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        //绑定外部纹理到纹理单元0
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mOESTextureId);
        //将此纹理单元床位片段着色器的uTextureSampler外部纹理采样器
        GLES20.glUniform1i(uTextureSamplerLocation, 0);

        //将纹理矩阵传给片段着色器
        GLES20.glUniformMatrix4fv(uTextureMatrixLocation, 1, false, transformMatrix, 0);

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
    }
}
