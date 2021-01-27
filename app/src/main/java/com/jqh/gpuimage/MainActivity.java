package com.jqh.gpuimage;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.jqh.gpuimagelib.filter.GPUImageGreyFilter;
import com.jqh.gpuimagelib.filter.GPUImageLightFilter;
import com.jqh.gpuimagelib.filter.GPUImageOpacityFilter;
import com.jqh.gpuimagelib.image.GPUImageView;

public class MainActivity extends AppCompatActivity {

    private GPUImageView gpuImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.a);

        gpuImageView = findViewById(R.id.gpuImageView);
        gpuImageView.setImage(bitmap)

        .addFilter(new GPUImageGreyFilter(this))
                .addFilter(new GPUImageLightFilter(this))

                .flush();
    }
}
