package com.dc.testopengl.shader;

/**
 * Created by duancong on 21/07/2018.
 */

public class BaseShader extends ShaderSource {

    private static final String VERTEX_SHADER = "" +
            "precision highp float;\n" +
            "attribute vec4 position;\n" +
            "attribute vec2 inputTextureCoordinate;\n" +
            "varying vec2 textureCoordinate;\n" +
            "void main(void)\n" +
            "{\n" +
            "    gl_Position = position;\n" +
            "    textureCoordinate = inputTextureCoordinate;\n" +
            "}";

    private static final String FRAGMENT_SHADER = "" +
            " precision highp float;\n" +
            " \n" +
            " varying vec2 textureCoordinate;\n" +
            " \n" +
            " uniform sampler2D inputImageTexture;\n" +
            " \n" +
            " void main()\n" +
            " {\n" +
            "     gl_FragColor = texture2D(inputImageTexture, textureCoordinate);\n" +
            " }";


    @Override
    public String getVertexShader() {
        return VERTEX_SHADER;
    }

    @Override
    public String getFragmentShader() {
        return FRAGMENT_SHADER;
    }
}
