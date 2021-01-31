package com.jqh.gpuimage;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;

import com.jqh.gpuimagelib.filter.GPUImageGreyFilter;
import com.jqh.gpuimagelib.filter.GPUImageLightFilter;
import com.jqh.gpuimagelib.filter.GPUImageOpacityFilter;
import com.jqh.gpuimagelib.image.GPUImageView;

public class MainActivity extends AppCompatActivity {

    private GPUImageView gpuImageView;

    private float opacity = 0.1f;

    private SeekBar seekBar;

    private GPUImageOpacityFilter opacityFilter ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        seekBar = findViewById(R.id.seekBar);

        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.a);

        gpuImageView = findViewById(R.id.gpuImageView);
        gpuImageView.setImage(bitmap);
//
        initEvent();
    }

    public void opacityClick(View view) {
        seekBar.setMax(100);
        opacityFilter = new GPUImageOpacityFilter(this, opacity);
        gpuImageView.setFilter(opacityFilter);
    }

    private void initEvent(){
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (opacityFilter != null) {
                    opacityFilter.setOpacity(1.0f * progress / 100);
                    gpuImageView.flush();
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
