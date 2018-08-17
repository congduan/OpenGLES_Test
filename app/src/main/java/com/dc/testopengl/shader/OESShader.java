package com.dc.testopengl.shader;

/**
 * Created by duancong on 21/07/2018.
 */

public class OESShader extends ShaderSource {

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
            " vec4 pos = vec4(aPosition.x, -aPosition.y, 0, 1);\n"+
            "  gl_Position = pos;\n" +
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


    @Override
    public String getVertexShader() {
        return VERTEX_SHADER;
    }

    @Override
    public String getFragmentShader() {
        return FRAGMENT_SHADER;
    }
}
