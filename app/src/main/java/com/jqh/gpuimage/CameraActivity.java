package com.jqh.gpuimage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Configuration;
import android.graphics.PointF;
import android.os.Bundle;

import com.jqh.gpuimagelib.camera.GPUCameraView;
import com.jqh.gpuimagelib.filter.GPUImageOpacityFilter;
import com.jqh.gpuimagelib.filter.GPUImageZoomBlurFilter;

public class CameraActivity extends AppCompatActivity {

    private GPUCameraView gpuCameraView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        gpuCameraView = findViewById(R.id.gpucamera_view);

//        gpuCameraView.setFilter(new GPUImageOpacityFilter(this, 0.5f));

        gpuCameraView.setFilter(new GPUImageZoomBlurFilter(this,new PointF(0.5f, 0.5f), 1.0f));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gpuCameraView.onDestory();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        gpuCameraView.previewAngle(this);
    }
}
