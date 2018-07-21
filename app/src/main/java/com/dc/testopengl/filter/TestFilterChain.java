package com.dc.testopengl.filter;

/**
 * Created by duancong on 21/07/2018.
 */

public class TestFilterChain extends FilterChain {

    Filter filter1;
    Filter filter2;
    Filter filter3;

    public TestFilterChain(){

        filter1 = new OES2RGBAFilter();
        filter2 = new CropFilter();
        filter3 = new PreviewFilter();

        add(filter1);
        add(filter2);
        add(filter3);
    }

    public void setTransfromMatrix(float[] matrix){
        filter1.setTransformMatrix(matrix);
        filter2.setTransformMatrix(matrix);
    }


}
