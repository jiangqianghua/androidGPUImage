package com.jqh.gpuimagelib.camera;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;

import com.jqh.gpuimagelib.utils.DisplayUtil;

import java.io.IOException;
import java.util.List;

public class GPUCamera {
    private int width;
    private int height;

    private Camera camera;
    public GPUCamera(Context context) {
        this.width = DisplayUtil.getScreenWidth(context);
        this.height = DisplayUtil.getScreenHeight(context);
    }

    private SurfaceTexture surfaceTexture;
    public void initCamera(SurfaceTexture surfaceTexture, int cameraId){
        this.surfaceTexture = surfaceTexture;
        setCameraParam(cameraId);
    }

    private void setCameraParam(int cameraId) {
        try{
            camera = Camera.open(cameraId);
            camera.setPreviewTexture(surfaceTexture);

            Camera.Parameters parameters = camera.getParameters();
            parameters.setFlashMode("off");
            parameters.setPreviewFormat(ImageFormat.NV21);
            Camera.Size size = getFitSize(parameters.getSupportedPictureSizes());
            parameters.setPictureSize(size.width,
                    size.height);
            Camera.Size preSize = getFitSize(parameters.getSupportedPreviewSizes());
            parameters.setPreviewSize(preSize.width,
                    preSize.height);
            camera.setParameters(parameters);

            camera.startPreview();


        }catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void stopPreview(){
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    public void changeCamera(int cameraId) {
        if (camera != null) {
            stopPreview();
        }
        setCameraParam(cameraId);
    }

    private Camera.Size getFitSize(List<Camera.Size> sizes) {
        if (width < height) {
            int t = height;
            height = width;
            width = t;
        }
        float scale = 1.0f * width/ height;
        int fitIndex = -1;
        float minDiff = 100f;
        int i = 0 ;
        for (Camera.Size size: sizes) {
            float cameraScale = 1.0f * size.width / size.height;
            float curScaleDiff = Math.abs(cameraScale - scale);
            if (curScaleDiff < minDiff) {
                minDiff = curScaleDiff;
                fitIndex = i;
            }
            i++;
        }
        return sizes.get(fitIndex);
    }
}
