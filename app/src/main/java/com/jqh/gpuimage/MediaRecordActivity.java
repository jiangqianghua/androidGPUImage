package com.jqh.gpuimage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
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
import com.jqh.gpuimagelib.render.filter.ZoomBlurFilter;
import com.jqh.gpuimagelib.render.textrue.BaseTexture;
import com.jqh.gpuimagelib.render.textrue.TextTexture;
import com.jqh.gpuimagelib.utils.DisplayUtil;

import java.io.File;

public class MediaRecordActivity extends AppCompatActivity {
    private JqhMediaEncodec jqhMediaEncodec ;
    private String path;
    AudioRecordUtil audioRecordUtil;
    private Button recordBtn;
    private GPUCameraView cameraView;

    private Handler handler = new Handler(Looper.getMainLooper());

    private String textureKey = "123";

    private boolean isAddTexture = false;

    private float left = 0.0f, top = 0.0f, scale = 0.2f;

    private UpdateRunable updateRunable = new UpdateRunable();

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

    public void imgTextureClick(View view) {
        if (!isAddTexture) {
            isAddTexture = true;
            Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.a);
            cameraView.addTexture(new BaseTexture(this, textureKey, bitmap, 0.1f, 0.1f, 0.1f));
            jqhMediaEncodec.addTexture(new BaseTexture(this, textureKey, bitmap,0.1f, 0.1f, 0.1f));
        } else {
            cameraView.removeTexture(textureKey);
            jqhMediaEncodec.removeTexture(textureKey);
            isAddTexture = false;
        }

    }

    public void textTextureClick(View view) {
        cameraView.addTexture(new TextTexture(this, "999", "这是水印", 50, "#ff00ff", left, top, scale));
        jqhMediaEncodec.addTexture(new TextTexture(this, "999", "这是水印", 50, "#ff00ff", left, top, scale));
        handler.postDelayed(updateRunable, 1000);
    }

    public void updateTexture(){
        cameraView.updateTexture("999", left, top, scale);
        if (jqhMediaEncodec == null) return ;
        jqhMediaEncodec.updateTexture("999", left, top, scale);
    }

    public void zoomblurFilterClick(View view) {
        cameraView.addFilter(new ZoomBlurFilter(this, new PointF(0.5f, 0.5f), 1.0f));
        jqhMediaEncodec.addFilter(new ZoomBlurFilter(this, new PointF(0.5f, 0.5f), 1.0f));
    }

    class UpdateRunable implements Runnable {
        @Override
        public void run() {
            left += 0.01f;
            top += 0.01f;
            scale += 0.01f;
            if (left > 1f) return ;
            updateTexture();
            handler.postDelayed(updateRunable, 100);
        }
    }



}
