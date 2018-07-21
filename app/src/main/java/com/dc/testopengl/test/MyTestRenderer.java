package com.dc.testopengl.test;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by duancong on 2017/12/4.
 */

public class MyTestRenderer implements GLSurfaceView.Renderer {
    private static final String TAG = "congduan";

    private static final int BYTES_PER_FLOAT = 4;

    Context mContext;

    FloatBuffer vertices;
    ShortBuffer indices;

    private int width;
    private int height;

    private GLSurfaceView glView;

    public MyTestRenderer(Context context, GLSurfaceView glView) {
        mContext = context;
        this.glView = glView;
        Log.i(TAG, "MyTestRenderer: "+glView.getWidth()+", "+glView.getHeight());
        initByteBuffer();
    }

    private void initByteBuffer() {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(3 * 3 * BYTES_PER_FLOAT);
        byteBuffer.order(ByteOrder.nativeOrder());
        vertices = byteBuffer.asFloatBuffer();
        vertices.put(new float[]{
                0f, 0f, 0f,
                1080f, 0f, 0f,
                590f, 1920f, 0f});
        ByteBuffer indicesBuffer = ByteBuffer.allocateDirect(3 * 2);
        indicesBuffer.order(ByteOrder.nativeOrder());
        indices = indicesBuffer.asShortBuffer();
        indices.put(new short[]{0, 1, 2});
        indices.flip();
        vertices.flip();
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        if(glView == null){
            Log.i(TAG, "onSurfaceCreated: glView is null");
        }
        Log.i(TAG, "onSurfaceCreated: "+glView.getWidth()+", "+glView.getHeight());
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        this.width = width;
        this.height = height;
        Log.i(TAG, "onSurfaceChanged: w="+width + ", h="+height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {

        gl.glViewport(0, 0, width+100, height+100);
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrthof(0, width, 0, height, 0, 1);
        gl.glColor4f(1, 0, 0, 1);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertices);
        gl.glDrawElements(GL10.GL_TRIANGLE_STRIP, 3,
                GL10.GL_UNSIGNED_SHORT, indices);

    }
}
