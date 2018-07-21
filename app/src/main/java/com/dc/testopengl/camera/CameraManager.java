package com.dc.testopengl.camera;

import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.Size;
import android.view.Surface;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by duancong on 21/07/2018.
 */

public class CameraManager {

    private static CameraManager mInstance = null;
    private List<CameraCallback> mCameraCallbacks = new ArrayList<>();

    private int mCameraId;
    private Camera mCamera;

    public static CameraManager getInstance() {
        if (mInstance == null) {
            mInstance = new CameraManager();
        }
        return mInstance;
    }

    public void addCallback(CameraCallback callback) {
        mCameraCallbacks.add(callback);
    }

    public void openInActivity(Activity activity) {
        //TODO
        mCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
        mCamera = Camera.open(mCameraId);
        Camera.Parameters parameters = mCamera.getParameters();
//        parameters.set("orientation", "portrait");//会导致设置参数抛出异常
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        parameters.setPreviewSize(1280, 720);
        mCamera.setDisplayOrientation(90);
        setCameraDisplayOrientation(activity, mCameraId, mCamera);
        try {
            mCamera.setParameters(parameters);
        }catch (Exception ex){
            ex.printStackTrace();
        }

        for (CameraCallback cameraCallback : mCameraCallbacks) {
            cameraCallback.onOpened();
        }
    }

    public Size getCameraSize(){
        return new Size(mCamera.getParameters().getPreviewSize().width, mCamera.getParameters().getPreviewSize().height);
    }

    public static void setCameraDisplayOrientation(Activity activity,
                                                   int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    public void startPreview(SurfaceTexture surfaceTexture) {
        if(mCamera == null){
            return;
        }
        //讲此SurfaceTexture作为相机预览输出
        try {
            mCamera.setPreviewTexture(surfaceTexture);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        //开启预览
        mCamera.startPreview();
    }

    public void stopPreview() {
        if (mCamera != null) {
            mCamera.stopPreview();
        }
    }

    public void close() {
        releaseCamera();

        for (CameraCallback cameraCallback : mCameraCallbacks) {
            cameraCallback.onClosed();
        }
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }


}
