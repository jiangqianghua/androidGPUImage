package com.jqh.gpuimage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Configuration;
import android.graphics.PointF;
import android.os.Bundle;
import android.widget.SeekBar;

import com.jqh.gpuimagelib.camera.GPUCameraView;
import com.jqh.gpuimagelib.filter.GPUImageOpacityFilter;
import com.jqh.gpuimagelib.filter.GPUImageZoomBlurFilter;

public class CameraActivity extends AppCompatActivity {

    private GPUCameraView gpuCameraView;

    private float opacity = 0.1f;

    private SeekBar seekBar;

    private GPUImageOpacityFilter opacityFilter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        seekBar = findViewById(R.id.seekBar);
        gpuCameraView = findViewById(R.id.gpucamera_view);

        opacityFilter = new GPUImageOpacityFilter(this, 0.5f);
        gpuCameraView.setFilter(opacityFilter);

//        gpuCameraView.setFilter(new GPUImageZoomBlurFilter(this,new PointF(0.5f, 0.5f), 1.0f));

        initEvent();
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

    private void initEvent(){
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (opacityFilter != null) {
                    opacityFilter.setOpacity(1.0f * progress / 100);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}
