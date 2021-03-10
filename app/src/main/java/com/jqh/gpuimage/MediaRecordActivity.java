package com.jqh.gpuimage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.jqh.gpuimagelib.audio.AudioRecordUtil;
import com.jqh.gpuimagelib.camera.GPUCameraView;
import com.jqh.gpuimagelib.encodec.JqhBaseMediaEncoder;
import com.jqh.gpuimagelib.encodec.JqhMediaEncodec;
import com.jqh.gpuimagelib.render.filter.BaseRenderFilter;
import com.jqh.gpuimagelib.render.filter.GreyRenderFilter;
import com.jqh.gpuimagelib.render.filter.OpacityRenderFilter;
import com.jqh.gpuimagelib.utils.DisplayUtil;

import java.io.File;

public class MediaRecordActivity extends AppCompatActivity {
    private JqhMediaEncodec jqhMediaEncodec ;
    private String path;
    AudioRecordUtil audioRecordUtil;
    private Button recordBtn;
    private GPUCameraView cameraView;


    private Context getContext(){
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_record);

        cameraView = findViewById(R.id.camera_view);
        recordBtn = findViewById(R.id.record_btn);
        String cachePath = getDiskCachePath(getApplicationContext());
        path = cachePath + File.separator + "record.mp4" ;
//        cameraView.addFilter(new OpacityRenderFilter(this, 0.5f));

        cameraView.addFilter(new BaseRenderFilter(this));

    }

    public void record(View view) {
        if (jqhMediaEncodec == null) {
            audioRecordUtil = new AudioRecordUtil();
            recordBtn.setText("停止录制");


            jqhMediaEncodec = new JqhMediaEncodec(getContext(), cameraView.getTextureId());
            int w = DisplayUtil.getScreenWidth(getContext());
            int h = DisplayUtil.getScreenHeight(getContext());
            jqhMediaEncodec.initEncodec(cameraView.getEglContext(), path, 720, 1280, 44100, 2 );
            jqhMediaEncodec.setOnMediaInfoListener(new JqhBaseMediaEncoder.OnMediaInfoListener(){
                @Override
                public void onMediaTime(long times) {
                    Log.d("jqh123", "time is = " + times);
                }
            });
            // 声音初始化完成后，开始进行录制
            jqhMediaEncodec.startRecord();
            audioRecordUtil.startRecord();

            audioRecordUtil.setOnRecordListener(new AudioRecordUtil.OnRecordListener() {
                @Override
                public void recordByte(byte[] audioData, int readSize) {
                    if (jqhMediaEncodec != null) {
                        jqhMediaEncodec.putPCMData(audioData, readSize);
                    }
                }
            });

        } else {
            audioRecordUtil.stop();
            jqhMediaEncodec.stopRecord();
            recordBtn.setText("开始录制");
            jqhMediaEncodec = null;

        }
    }

    public static String getDiskCachePath(Context context) {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            return context.getExternalCacheDir().getPath();
        } else {
            return context.getCacheDir().getPath();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        cameraView.previewAngle(this);
    }

    public void defaultFilterClick(View view) {
        cameraView.addFilter(new BaseRenderFilter(this));
        jqhMediaEncodec.addFilter(new BaseRenderFilter(this));
    }

    public void opacityFilterClick(View view) {
        cameraView.addFilter(new OpacityRenderFilter(this, 0.5f));
        jqhMediaEncodec.addFilter(new OpacityRenderFilter(this, 0.5f));
    }

    public void greyFilterClick(View view) {
        cameraView.addFilter(new GreyRenderFilter(this));
        jqhMediaEncodec.addFilter(new GreyRenderFilter(this));
    }
}
