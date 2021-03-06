package com.jqh.gpuimage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.graphics.RectF;
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
import com.jqh.gpuimagelib.listener.OnDetectorFaceListener;
import com.jqh.gpuimagelib.render.filter.BaseGPUImageFilter;
import com.jqh.gpuimagelib.render.filter.GPUImageBeautyFilter;
import com.jqh.gpuimagelib.render.filter.GPUImageBrightnessFilter;
import com.jqh.gpuimagelib.render.filter.GPUImageBulgeDistortionFilter;
import com.jqh.gpuimagelib.render.filter.GPUImageCGAColorspaceFilter;
import com.jqh.gpuimagelib.render.filter.GPUImageColorBalanceFilter;
import com.jqh.gpuimagelib.render.filter.GPUImageColorInvertFilter;
import com.jqh.gpuimagelib.render.filter.GPUImageColorMatrixFilter;
import com.jqh.gpuimagelib.render.filter.GPUImageContrastFilter;
import com.jqh.gpuimagelib.render.filter.GPUImageCrosshatchFilter;
import com.jqh.gpuimagelib.render.filter.GPUImageExposureFilter;
import com.jqh.gpuimagelib.render.filter.GPUImageFalseColorFilter;
import com.jqh.gpuimagelib.render.filter.GPUImageGammaFilter;
import com.jqh.gpuimagelib.render.filter.GPUImageGlassSphereFilter;
import com.jqh.gpuimagelib.render.filter.GPUImageGreyFilter;
import com.jqh.gpuimagelib.render.filter.GPUImageHalftoneFilter;
import com.jqh.gpuimagelib.render.filter.GPUImageHazeFilter;
import com.jqh.gpuimagelib.render.filter.GPUImageHighlightShadowFilter;
import com.jqh.gpuimagelib.render.filter.GPUImageHueFilter;
import com.jqh.gpuimagelib.render.filter.GPUImageKuwaharaFilter;
import com.jqh.gpuimagelib.render.filter.GPUImageLuminanceFilter;
import com.jqh.gpuimagelib.render.filter.GPUImageLuminanceThresholdFilter;
import com.jqh.gpuimagelib.render.filter.GPUImageMonochromeFilter;
import com.jqh.gpuimagelib.render.filter.GPUImageOpacityFilter;
import com.jqh.gpuimagelib.render.filter.GPUImagePixelationFilter;
import com.jqh.gpuimagelib.render.filter.GPUImagePosterizeFilter;
import com.jqh.gpuimagelib.render.filter.GPUImageRGBFilter;
import com.jqh.gpuimagelib.render.filter.GPUImageSaturationFilter;
import com.jqh.gpuimagelib.render.filter.GPUImageSphereRefractionFilter;
import com.jqh.gpuimagelib.render.filter.GPUImageSwirlFilter;
import com.jqh.gpuimagelib.render.filter.GPUImageVibranceFilter;
import com.jqh.gpuimagelib.render.filter.GPUImageVignetteFilter;
import com.jqh.gpuimagelib.render.filter.GPUImageWhiteBalanceFilter;
import com.jqh.gpuimagelib.render.filter.GPUImageZoomBlurFilter;
import com.jqh.gpuimagelib.render.textrue.BaseTexture;
import com.jqh.gpuimagelib.render.textrue.TextTexture;
import com.jqh.gpuimagelib.utils.DisplayUtil;
import com.jqh.gpuimagelib.utils.FaceUtils;
import com.jqh.gpuimagelib.utils.LogUtils;

import java.io.File;

public class DouyinActivity extends AppCompatActivity {

    private JqhMediaEncodec jqhMediaEncodec ;
    private String path;
    AudioRecordUtil audioRecordUtil;
    private Button recordBtn, detectorBtn;
    private GPUCameraView cameraView;

    private Handler handler = new Handler(Looper.getMainLooper());

    private String textureKey = "123";

    private boolean isAddTexture = false;

    private float left = 0.0f, top = 0.0f, scale = 0.2f;

    private UpdateRunable updateRunable = new UpdateRunable();
    private boolean isDetector = false;

    private ImageView imageView ;

    private boolean isRecord = false;
    private Context getContext(){
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_douyin);

        cameraView = findViewById(R.id.camera_view);
        recordBtn = findViewById(R.id.record_btn);
        detectorBtn = findViewById(R.id.detectorFace);
        String cachePath = getDiskCachePath(getApplicationContext());
        path = cachePath + File.separator + "record.mp4" ;
//        cameraView.addFilter(new GPUImageOpacityFilter(this, 0.5f));

        cameraView.addFilter(new BaseGPUImageFilter(this));
        cameraView.isDetectorFace(isDetector);

        cameraView.getCameraRender().setOnDetectorFaceListener(new OnDetectorFaceListener() {
            @Override
            public void onDetectorRect(RectF rectF, int w, int h) {
                left = rectF.left * 1.0f / w;
                top = rectF.top * 1.0f / h;
                scale = (rectF.right - rectF.left) * 1.0f / w;
                updateTexture();
            }

            @Override
            public void onBitmap(Bitmap bitmap) {

            }
        });

        imageView = findViewById(R.id.imageview);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.setVisibility(View.GONE);
            }
        });




    }

    public void record(View view) {
        if (!isRecord) {
            isRecord = true;

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

            recordBtn.setText("停止录制");
            audioRecordUtil = new AudioRecordUtil();
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
            isRecord = false;
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
            Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.mipmap.d);
            cameraView.addTexture(new BaseTexture(this, textureKey, bitmap, 0.1f, 0.1f, 0.1f));
            jqhMediaEncodec.addTexture(new BaseTexture(this, textureKey, bitmap,0.1f, 0.1f, 0.1f));
//            handler.postDelayed(updateRunable, 1000);
        } else {
            cameraView.removeTexture(textureKey);
            jqhMediaEncodec.removeTexture(textureKey);
            isAddTexture = false;
//            handler.removeCallbacks(updateRunable);
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
        LogUtils.logd("开始检测人脸 left=" + left + " top=" + top);
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
        cameraView.setOnTakePhoneListener(new GPUCameraView.OnTakePhoneListener() {
            @Override
            public void onResult(final Bitmap bitmap) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        FaceUtils.showFace(bitmap, imageView);
//                        imageView.setImageBitmap(bitmap);
                        imageView.setVisibility(View.VISIBLE);
                    }
                });
            }
        });

        cameraView.takePhoto();

    }

    public void switchCameraClick(View view) {
        cameraView.switchCamera();
    }

    public void ColorInvertFilterClick(View view) {
        cameraView.addFilter(new GPUImageColorInvertFilter(this));
        jqhMediaEncodec.addFilter(new GPUImageColorInvertFilter(this));
    }

    public void ColorMatrixFilterClick(View view) {
        cameraView.addFilter(new GPUImageColorMatrixFilter(this));
        jqhMediaEncodec.addFilter(new GPUImageColorMatrixFilter(this));
    }

    public void ContrastFilterClick(View view) {
        cameraView.addFilter(new GPUImageContrastFilter(this));
        jqhMediaEncodec.addFilter(new GPUImageContrastFilter(this));
    }

    public void CrosshatchFilterClick(View view) {
        cameraView.addFilter(new GPUImageCrosshatchFilter(this));
        jqhMediaEncodec.addFilter(new GPUImageCrosshatchFilter(this));
    }

    public void ExposureFilterClick(View view) {
        cameraView.addFilter(new GPUImageExposureFilter(this));
        jqhMediaEncodec.addFilter(new GPUImageExposureFilter(this));
    }

    public void FalseColorFilterClick(View view) {
        cameraView.addFilter(new GPUImageFalseColorFilter(this));
        jqhMediaEncodec.addFilter(new GPUImageFalseColorFilter(this));
    }

    public void GammaFilterClick(View view) {
        cameraView.addFilter(new GPUImageGammaFilter(this));
        jqhMediaEncodec.addFilter(new GPUImageGammaFilter(this));
    }

    public void GlassSphereFilterClick(View view) {
        cameraView.addFilter(new GPUImageGlassSphereFilter(this));
        jqhMediaEncodec.addFilter(new GPUImageGlassSphereFilter(this));
    }

    public void HalftoneFilterClick(View view) {
        cameraView.addFilter(new GPUImageHalftoneFilter(this));
        jqhMediaEncodec.addFilter(new GPUImageHalftoneFilter(this));
    }

    public void HazeFilterClick(View view) {
        cameraView.addFilter(new GPUImageHazeFilter(this));
        jqhMediaEncodec.addFilter(new GPUImageHazeFilter(this));
    }

    public void HighlightShadowFilterClick(View view) {
        cameraView.addFilter(new GPUImageHighlightShadowFilter(this));
        jqhMediaEncodec.addFilter(new GPUImageHighlightShadowFilter(this));
    }

    public void HueFilterClick(View view) {
        cameraView.addFilter(new GPUImageHueFilter(this));
        jqhMediaEncodec.addFilter(new GPUImageHueFilter(this));
    }

    public void KuwaharaFilterClick(View view) {
        cameraView.addFilter(new GPUImageKuwaharaFilter(this));
        jqhMediaEncodec.addFilter(new GPUImageKuwaharaFilter(this));
    }

    public void LuminanceFilterClick(View view) {
        cameraView.addFilter(new GPUImageLuminanceFilter(this));
        jqhMediaEncodec.addFilter(new GPUImageLuminanceFilter(this));
    }

    public void LuminanceThresholdFilterClick(View view) {
        cameraView.addFilter(new GPUImageLuminanceThresholdFilter(this));
        jqhMediaEncodec.addFilter(new GPUImageLuminanceThresholdFilter(this));
    }

    public void MonochromeFilterClick(View view) {
        cameraView.addFilter(new GPUImageMonochromeFilter(this));
        jqhMediaEncodec.addFilter(new GPUImageMonochromeFilter(this));
    }

    public void PixelationFilterClick(View view) {
        cameraView.addFilter(new GPUImagePixelationFilter(this));
        jqhMediaEncodec.addFilter(new GPUImagePixelationFilter(this));
    }

    public void PosterizeFilterClick(View view) {
        cameraView.addFilter(new GPUImagePosterizeFilter(this));
        jqhMediaEncodec.addFilter(new GPUImagePosterizeFilter(this));
    }

    public void RGB_RFilterClick(View view) {
        cameraView.addFilter(new GPUImageRGBFilter(this, 1, 0 ,0));
        jqhMediaEncodec.addFilter(new GPUImageRGBFilter(this, 1, 0 ,0));
    }

    public void RGB_GFilterClick(View view) {
        cameraView.addFilter(new GPUImageRGBFilter(this, 0, 1 ,0));
        jqhMediaEncodec.addFilter(new GPUImageRGBFilter(this, 0, 1 ,0));
    }


    public void RGB_BFilterClick(View view) {
        cameraView.addFilter(new GPUImageRGBFilter(this, 0, 0 ,1));
        jqhMediaEncodec.addFilter(new GPUImageRGBFilter(this, 0, 0 ,1));
    }

    public void SaturatioFilterClick(View view) {
        cameraView.addFilter(new GPUImageSaturationFilter(this, 0));
        jqhMediaEncodec.addFilter(new GPUImageSaturationFilter(this, 0));
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

    public void detectorFaceClick(View view) {
        isDetector = !isDetector;
        cameraView.isDetectorFace(isDetector);
        if (isDetector) {
            detectorBtn.setText("关闭人脸检测");
        } else {
            detectorBtn.setText("打开人脸检测");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (audioRecordUtil != null) {
            audioRecordUtil.stop();
        }
        if (jqhMediaEncodec != null) {
            jqhMediaEncodec.stopRecord();
        }

        jqhMediaEncodec = null;

        cameraView.onDestory();
    }
}
