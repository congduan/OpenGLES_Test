package com.dc.testopengl;

import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.dc.testopengl.camera.CameraCallback;
import com.dc.testopengl.camera.CameraManager;

public class CameraActivity extends Activity {

    private GLSurfaceView surfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 全屏显示
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_camera);

        initGLSurfaceView();

        CameraManager.getInstance().addCallback(new CameraCallback() {
            @Override
            public void onOpened() {

            }

            @Override
            public void onClosed() {

            }
        });
    }

    private void initGLSurfaceView() {
        surfaceView = findViewById(R.id.glSurfaceView);
        surfaceView.setEGLContextClientVersion(2);
        surfaceView.setRenderer(new CameraRenderer(this, surfaceView, new CameraRenderer.SurfaceTextureInitCallback() {
            @Override
            public void onInit(SurfaceTexture surfaceTexture) {

                CameraManager.getInstance().openInActivity(CameraActivity.this);
                CameraManager.getInstance().startPreview(surfaceTexture);
            }
        }));
        surfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    @Override
    protected void onResume() {
        super.onResume();
        surfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        surfaceView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CameraManager.getInstance().stopPreview();
        CameraManager.getInstance().close();
    }
}
