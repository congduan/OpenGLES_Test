package com.dc.testopengl;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by duancong on 2017/12/4.
 */

public class MyGLSurfaceView extends GLSurfaceView {

    private Renderer mRenderer;

    public MyGLSurfaceView(Context context) {
        super(context);
    }

    public MyGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setRenderer(Renderer renderer) {
        super.setRenderer(renderer);
        mRenderer = renderer;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {


        return true;
    }
}
