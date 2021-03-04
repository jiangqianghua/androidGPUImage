package com.jqh.gpuimage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Configuration;
import android.os.Bundle;

import com.jqh.gpuimagelib.camera.GPUCameraView;

public class CameraActivity extends AppCompatActivity {

    private GPUCameraView gpuCameraView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        gpuCameraView = findViewById(R.id.gpucamera_view);
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
