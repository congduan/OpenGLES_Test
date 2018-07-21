package com.dc.testopengl.filter;

import com.dc.testopengl.shader.BaseShader;

/**
 * 画面裁剪
 * Created by duancong on 21/07/2018.
 */

public class CropFilter extends Filter {

    public CropFilter(){
        super(new BaseShader());
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void destroy() {
        super.destroy();
    }

    @Override
    public void process(int inTex, int outTex) {

    }

    @Override
    public void process(int inTex, int outTex, int inputWidth, int inputHeight, int outputWidth, int outputHeight) {

    }

}
