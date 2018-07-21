package com.dc.testopengl.shader;

import android.opengl.GLES20;

/**
 * Created by duancong on 21/07/2018.
 */

public class Filter {
    protected String mVertexCode;
    protected String mFragmentCode;

    protected int mShaderProgram;

    public Filter(){

    }

    public Filter(String vertex, String fragment){
        mVertexCode = vertex;
        mFragmentCode = fragment;
    }

    //加载着色器，GL_VERTEX_SHADER代表生成顶点着色器，GL_FRAGMENT_SHADER代表生成片段着色器
    public int loadShader(int type, String shaderSource) {
        //创建Shader
        int shader = GLES20.glCreateShader(type);
        if (shader == 0) {
            throw new RuntimeException("Create Filter Failed!" + GLES20.glGetError());
        }
        //加载Shader代码
        GLES20.glShaderSource(shader, shaderSource);
        //编译Shader
        GLES20.glCompileShader(shader);
        return shader;
    }

    //将两个Shader链接至program中
    public int linkProgram(int verShader, int fragShader) {
        //创建program
        int program = GLES20.glCreateProgram();
        if (program == 0) {
            throw new RuntimeException("Create Program Failed!" + GLES20.glGetError());
        }
        //附着顶点和片段着色器
        GLES20.glAttachShader(program, verShader);
        GLES20.glAttachShader(program, fragShader);
        //链接program
        GLES20.glLinkProgram(program);
        //告诉OpenGL ES使用此program
        GLES20.glUseProgram(program);
        return program;
    }

}
