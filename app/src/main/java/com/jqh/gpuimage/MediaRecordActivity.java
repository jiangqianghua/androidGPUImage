package com.jqh.gpuimage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.jqh.gpuimagelib.audio.AudioRecordUtil;
import com.jqh.gpuimagelib.camera.GPUCameraView;
import com.jqh.gpuimagelib.encodec.JqhBaseMediaEncoder;
import com.jqh.gpuimagelib.encodec.JqhMediaEncodec;
import com.jqh.gpuimagelib.render.filter.BaseGPUImageFilter;
import com.jqh.gpuimagelib.render.filter.GPUImageBeautyFilter;
import com.jqh.gpuimagelib.render.filter.GPUImageBrightnessFilter;
import com.jqh.gpuimagelib.render.filter.GPUImageBulgeDistortionFilter;
import com.jqh.gpuimagelib.render.filter.GPUImageCGAColorspaceFilter;
import com.jqh.gpuimagelib.render.filter.GPUImageColorBalanceFilter;
import com.jqh.gpuimagelib.render.filter.GPUImageGreyFilter;
import com.jqh.gpuimagelib.render.filter.GPUImageOpacityFilter;
import com.jqh.gpuimagelib.render.filter.GPUImageSphereRefractionFilter;
import com.jqh.gpuimagelib.render.filter.GPUImageSwirlFilter;
import com.jqh.gpuimagelib.render.filter.GPUImageVibranceFilter;
import com.jqh.gpuimagelib.render.filter.GPUImageVignetteFilter;
import com.jqh.gpuimagelib.render.filter.GPUImageWhiteBalanceFilter;
import com.jqh.gpuimagelib.render.filter.GPUImageZoomBlurFilter;
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
//        cameraView.addFilter(new GPUImageOpacityFilter(this, 0.5f));

        cameraView.addFilter(new BaseGPUImageFilter(this));

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
        cameraView.addFilter(new BaseGPUImageFilter(this));
        jqhMediaEncodec.addFilter(new BaseGPUImageFilter(this));
    }

    public void opacityFilterClick(View view) {
        cameraView.addFilter(new GPUImageOpacityFilter(this, 0.5f));
        jqhMediaEncodec.addFilter(new GPUImageOpacityFilter(this, 0.5f));
    }

    public void greyFilterClick(View view) {
        cameraView.addFilter(new GPUImageGreyFilter(this));
        jqhMediaEncodec.addFilter(new GPUImageGreyFilter(this));
    }

    public void imgTextureClick(View view) {
        if (!isAddTexture) {
            isAddTexture = true;
            Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.a);
            cameraView.addTexture(new BaseTexture(this, textureKey, bitmap, 0.1f, 0.1f, 0.1f));
            jqhMediaEncodec.addTexture(new BaseTexture(this, textureKey, bitmap,0.1f, 0.1f, 0.1f));
            handler.postDelayed(updateRunable, 1000);
        } else {
            cameraView.removeTexture(textureKey);
            jqhMediaEncodec.removeTexture(textureKey);
            isAddTexture = false;
            handler.removeCallbacks(updateRunable);
        }

    }

    public void textTextureClick(View view) {
        cameraView.addTexture(new TextTexture(this, "999", "这是水印", 50, "#ff00ff", left, top, scale));
        jqhMediaEncodec.addTexture(new TextTexture(this, "999", "这是水印", 50, "#ff00ff", left, top, scale));
//        handler.postDelayed(updateRunable, 1000);
    }

    public void updateTexture(){
        cameraView.updateTexture(textureKey, left, top, scale);
        if (jqhMediaEncodec == null) return ;
        jqhMediaEncodec.updateTexture(textureKey, left, top, scale);
    }

    public void zoomblurFilterClick(View view) {
        cameraView.addFilter(new GPUImageZoomBlurFilter(this, new PointF(0.5f, 0.5f), 1.0f));
        jqhMediaEncodec.addFilter(new GPUImageZoomBlurFilter(this, new PointF(0.5f, 0.5f), 1.0f));
    }

    public void WhiteBlanceFilterClick(View view) {
        cameraView.addFilter(new GPUImageWhiteBalanceFilter(this));
        jqhMediaEncodec.addFilter(new GPUImageWhiteBalanceFilter(this));
    }

    public void VignetteFilterClick(View view) {
        cameraView.addFilter(new GPUImageVignetteFilter(this));
        jqhMediaEncodec.addFilter(new GPUImageVignetteFilter(this));
    }

    public void VibranceFilterClick(View view) {
        cameraView.addFilter(new GPUImageVibranceFilter(this));
        jqhMediaEncodec.addFilter(new GPUImageVibranceFilter(this));
    }

    public void SwirlFilterClick(View view) {
        cameraView.addFilter(new GPUImageSwirlFilter(this));
        jqhMediaEncodec.addFilter(new GPUImageSwirlFilter(this));
    }

    public void SphereRefractionFilterClick(View view) {
        cameraView.addFilter(new GPUImageSphereRefractionFilter(this));
        jqhMediaEncodec.addFilter(new GPUImageSphereRefractionFilter(this));
    }

    public void BrightnessFilterClick(View view) {
        cameraView.addFilter(new GPUImageBrightnessFilter(this, 0.7f));
        jqhMediaEncodec.addFilter(new GPUImageBrightnessFilter(this));
    }

    public void BulgeDistortionFilterClick(View view) {
        cameraView.addFilter(new GPUImageBulgeDistortionFilter(this));
        jqhMediaEncodec.addFilter(new GPUImageBulgeDistortionFilter(this));
    }

    public void CGAColorspaceFilterClick(View view) {
        cameraView.addFilter(new GPUImageCGAColorspaceFilter(this));
        jqhMediaEncodec.addFilter(new GPUImageCGAColorspaceFilter(this));
    }

    public void ColorBalanceFilterClick(View view) {
        cameraView.addFilter(new GPUImageColorBalanceFilter(this));
        jqhMediaEncodec.addFilter(new GPUImageColorBalanceFilter(this));
    }

    public void beautyFilterClick(View view) {
        cameraView.addFilter(new GPUImageBeautyFilter(this));
        jqhMediaEncodec.addFilter(new GPUImageBeautyFilter(this));
    }

    public void takephotoClick(View view) {
        // 拍照
        Bitmap bitmap = loadBitmapFromView(cameraView);
        ImageView imageView = findViewById(R.id.imageview);
        imageView.setImageBitmap(bitmap);

    }

    private Bitmap loadBitmapFromView(View v) {
        int w = v.getWidth();
        int h = v.getHeight();
        Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);

        c.drawColor(Color.WHITE);
        /** 如果不设置canvas画布为白色，则生成透明 */

        v.layout(0, 0, w, h);
        v.draw(c);

        return bmp;
    }


    class UpdateRunable implements Runnable {
        @Override
        public void run() {
            left += 0.01f;
            top += 0.01f;
            scale += 0.01f;
            if (left > 1f) return ;
            updateTexture();
            handler.postDelayed(updateRunable, 2000);
        }
    }



}
