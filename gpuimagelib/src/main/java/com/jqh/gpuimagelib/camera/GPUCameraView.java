package com.jqh.gpuimagelib.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.WindowManager;

import com.jqh.gpuimagelib.opengl.GLSurfaceView;
import com.jqh.gpuimagelib.render.filter.BaseGPUImageFilter;
import com.jqh.gpuimagelib.render.textrue.BaseTexture;

import javax.microedition.khronos.opengles.GL10;

public class GPUCameraView extends GLSurfaceView {

    private GPUCameraRender jqhCameraRender;
    private GPUCamera jqhCamera ;
    private int textureId = -1;

    private int cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
    public GPUCameraView(Context context) {
        this(context, null);
    }

    public GPUCameraView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GPUCameraView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        jqhCameraRender = new GPUCameraRender(context);
        jqhCamera = new GPUCamera(context);

        setRender(jqhCameraRender);
        previewAngle(context);
        jqhCameraRender.setOnSurfaceCreateListener(new GPUCameraRender.OnSurfaceCreateListener() {
            @Override
            public void onSurfaceCreate(SurfaceTexture surfaceTexture, int _textureId) {
                jqhCamera.initCamera(surfaceTexture, cameraId);
                textureId = _textureId;

                jqhCameraRender.setGl((GL10) getEglContext().getGL());

            }

            @Override
            public void onCreateBitmap(Bitmap bitmap) {
                if (onTakePhoneListener != null) {
                    onTakePhoneListener.onResult(bitmap);
                }
            }
        });

    }
    

    public void onDestory(){
        if (jqhCamera != null) {
            jqhCamera.stopPreview();
        }
    }

    public void previewAngle(Context context) {
        int angle = ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
        jqhCameraRender.reSetMatrix();//  初始化矩阵
        switch (angle) {
            case Surface.ROTATION_0:
                Log.d("CameraView", "angle 0");
                if (cameraId == Camera.CameraInfo.CAMERA_FACING_BACK){
                    jqhCameraRender.setAngle(90f, 0f, 0f, 1f);
                    jqhCameraRender.setAngle(180f, 1f, 0f, 0f);
                } else {
                    jqhCameraRender.setAngle(90f, 0f, 0f, 1f);
                }

                break;
            case Surface.ROTATION_90:
                Log.d("CameraView", "angle 90");
                if (cameraId == Camera.CameraInfo.CAMERA_FACING_BACK){
//                    jqhCameraRender.setAngle(180, 1, 0, 0);

                    jqhCameraRender.setAngle(180f, 0f, 0f, 1f);
                    jqhCameraRender.setAngle(180f, 0f, 1f, 0f);
                } else {
                    jqhCameraRender.setAngle(180f, 0f, 0f, 1f);
                }
                break;
            case Surface.ROTATION_180:
                Log.d("CameraView", "angle 180");
                if (cameraId == Camera.CameraInfo.CAMERA_FACING_BACK){
                    jqhCameraRender.setAngle(90f, 0f, 0f, 0f);
                    jqhCameraRender.setAngle(180f, 0f, 1f, 0f);
                } else {
                    jqhCameraRender.setAngle(-90f, 0f, 0f, 1f);
                }
                break;
            case Surface.ROTATION_270:
                Log.d("CameraView", "angle 270");
                if (cameraId == Camera.CameraInfo.CAMERA_FACING_BACK){
                    jqhCameraRender.setAngle(180f, 0f, 1f, 0f);
                } else {
                    jqhCameraRender.setAngle(0f, 0f, 0f, 1f);
                }
                break;

        }
    }

    public int getTextureId(){
        return textureId;
    }

    public GPUCameraView addFilter(BaseGPUImageFilter filter) {
        if (filter != null && jqhCameraRender != null) {
            jqhCameraRender.addFilter(filter);
        }
        return this;
    }

    public void addTexture(BaseTexture baseTexture) {
        if (baseTexture != null){
            jqhCameraRender.addTexture(baseTexture);
        }
    }

    public void removeTexture(String key) {
        jqhCameraRender.removeTexture(key);
    }

    public void updateTexture(String id, float left, float top, float scale) {
        jqhCameraRender.updateTexture(id, left, top, scale);
    }

    public void takePhoto(){
        jqhCameraRender.takePhoto();
    }

    public interface OnTakePhoneListener{
        void onResult(Bitmap bitmap);
    }

    private OnTakePhoneListener onTakePhoneListener;

    public void setOnTakePhoneListener(OnTakePhoneListener onTakePhoneListener) {
        this.onTakePhoneListener = onTakePhoneListener;
    }
}
