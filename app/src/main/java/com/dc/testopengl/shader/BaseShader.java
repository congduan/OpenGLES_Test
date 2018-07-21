package com.dc.testopengl.shader;

/**
 * Created by duancong on 21/07/2018.
 */

public class BaseShader extends ShaderSource {

    private static final String VERTEX_SHADER = "" +
            "attribute vec4 vPosition; \n" +
            " \n" +
            "void main(void)\n" +
            "{\n" +
            "    gl_Position = vPosition;\n" +
            "}";


    private static final String FRAGMENT_SHADER = "" +
            "#extension GL_OES_EGL_image_external : require\n" +
            "precision mediump float;\n" +
            "uniform samplerExternalOES uTextureSampler;\n" +
            "varying vec2 vTextureCoord;\n" +
            "void main() \n" +
            "{\n" +
            "  gl_FragColor = texture2D(uTextureSampler, vTextureCoord);\n" +
            "}\n";

    @Override
    public String getVertexShader() {
        return VERTEX_SHADER;
    }

    @Override
    public String getFragmentShader() {
        return FRAGMENT_SHADER;

    }
}
