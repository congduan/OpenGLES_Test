package com.dc.testopengl.filter;

import android.opengl.GLES20;
import android.util.Size;

import com.dc.testopengl.shader.ShaderSource;

/**
 * Created by duancong on 21/07/2018.
 */

public abstract class Filter implements IGLResource{
    protected String mVertexCode;
    protected String mFragmentCode;

    protected int mShaderProgram;
    private int mVertexShader;
    private int mFragmentShader;

    private int mInputWidth;
    private int mInputHeight;

    private int mOutputWidth;
    private int mOutputHeight;

    private float[] mTransformMatrix;

    public void setTransformMatrix(float[] matrix){
        mTransformMatrix = matrix;
    }

    public float[] getTransformMatrix() {
        return mTransformMatrix;
    }

    public Filter(){

    }

    public Filter(int inputWidth, int inputHeight, int outputWidth, int outputHeight){
        this.mInputWidth = inputWidth;
        this.mInputHeight = inputHeight;
        this.mOutputWidth = outputWidth;
        this.mOutputHeight = outputHeight;
    }

    public Filter(Size inputSize, Size outputSize){
        this.mInputWidth = inputSize.getWidth();
        this.mInputHeight = inputSize.getHeight();
        this.mOutputWidth = outputSize.getWidth();
        this.mOutputHeight = outputSize.getHeight();
    }

    public void setSize(int inputWidth, int inputHeight, int outputWidth, int outputHeight){
        this.mInputWidth = inputWidth;
        this.mInputHeight = inputHeight;
        this.mOutputWidth = outputWidth;
        this.mOutputHeight = outputHeight;
    }

    public int getInputWidth() {
        return mInputWidth;
    }

    public int getInputHeight() {
        return mInputHeight;
    }

    public int getOutputHeight() {
        return mOutputHeight;
    }

    public int getOutputWidth() {
        return mOutputWidth;
    }

    public Filter(ShaderSource shader){
        mVertexCode = shader.getVertexShader();
        mFragmentCode = shader.getFragmentShader();
    }

    public Filter(String vertex, String fragment){
        mVertexCode = vertex;
        mFragmentCode = fragment;
    }

    public void compile(){
        mVertexShader = loadShader(GLES20.GL_VERTEX_SHADER, mVertexCode);
        mFragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, mFragmentCode);
        mShaderProgram = linkProgram(mVertexShader, mFragmentShader);
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

    @Override
    public void init() {
        compile();
    }

    @Override
    public void destroy(){
        if(mShaderProgram != 0) {
            GLES20.glDeleteProgram(mShaderProgram);
        }
    }

    public abstract void process(int inTex, int outTex);
    public abstract void process(int inTex, int outTex, int inputWidth, int inputHeight, int outputWidth, int outputHeight);

}
