package com.dc.testopengl.test;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;


public class FirstTriangleTest extends Activity {
	public GLSurfaceView glView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		glView = new GLSurfaceView(this);
		glView.setRenderer(new SimpleRenderer());
		setContentView(glView);
	}

	@Override
	protected void onResume() {
		super.onResume();
		glView.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
		glView.onPause();
	}

	class SimpleRenderer implements Renderer {

		FloatBuffer vertices;
		ShortBuffer indices;

		public SimpleRenderer() {

			ByteBuffer byteBuffer = ByteBuffer.allocateDirect(3 * 3 * 4);
			byteBuffer.order(ByteOrder.nativeOrder());
			vertices = byteBuffer.asFloatBuffer();
			vertices.put(new float[] { 0f, 0f, 0f, 
					                  1080f, 0f, 0f,
					                  590f, 1920f, 0f });
			ByteBuffer indicesBuffer = ByteBuffer.allocateDirect(3 * 2);
			indicesBuffer.order(ByteOrder.nativeOrder());
			indices = indicesBuffer.asShortBuffer();
			indices.put(new short[] { 0, 1, 2 });
			indices.flip();
			vertices.flip();
		}

		@Override
		public void onSurfaceCreated(GL10 gl, EGLConfig config) {
			Log.d("GLSurfaceViewTest", "surface created");
		}

		@Override
		public void onSurfaceChanged(GL10 gl, int width, int height) {
			Log.d("GLSurfaceViewTest", "surface changed: " + width + "x"
					+ height);
		}

		@Override
		public void onDrawFrame(GL10 gl) {
			gl.glViewport(0, 0, glView.getWidth(), glView.getHeight());
			gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
			gl.glMatrixMode(GL10.GL_PROJECTION);
			gl.glLoadIdentity();
			gl.glOrthof(0, 1080, 0, 1920, 0, 1);
			gl.glColor4f(1, 0, 0, 1);
			gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertices);
			gl.glDrawElements(GL10.GL_TRIANGLE_STRIP, 3,
					GL10.GL_UNSIGNED_SHORT, indices);
		}
	}
}
