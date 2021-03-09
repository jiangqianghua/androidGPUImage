package com.jqh.gpuimage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.jqh.gpuimagelib.audio.AudioRecordUtil;
import com.jqh.gpuimagelib.camera.GPUCameraView;
import com.jqh.gpuimagelib.encodec.JqhBaseMediaEncoder;
import com.jqh.gpuimagelib.encodec.JqhMediaEncodec;
import com.jqh.gpuimagelib.filter.GPUImageGreyFilter;
import com.jqh.gpuimagelib.filter.GPUImageImgFliter;
import com.jqh.gpuimagelib.filter.GPUImageOpacityFilter;
import com.jqh.gpuimagelib.filter.GPUImageTextFilter;
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
        cameraView.addFilter(new GPUImageGreyFilter(this));
        cameraView.addFilter(new GPUImageTextFilter(this,"我爱熊毛毛"));

        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.a);
        cameraView.addFilter(new GPUImageImgFliter(this, bitmap));

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
}
