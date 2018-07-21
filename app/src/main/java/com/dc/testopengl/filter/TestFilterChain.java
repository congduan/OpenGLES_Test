package com.dc.testopengl.filter;

/**
 * Created by duancong on 21/07/2018.
 */

public class TestFilterChain extends FilterChain {

    Filter filter1;
    Filter filter2;

    public TestFilterChain(int width, int height){

        filter1 = new OES2RGBAFilter(width, height);
        filter2 = new PreviewFilter(width, height);

        add(filter1);
        add(filter2);
    }

    public void setTransfromMatrix(float[] matrix){
        filter1.setTransformMatrix(matrix);
        filter2.setTransformMatrix(matrix);
    }


}
