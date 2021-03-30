# Android GPUImage
[![License](https://img.shields.io/badge/license-Apache%202-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![Download](https://api.bintray.com/packages/cats-oss/maven/gpuimage/images/download.svg) ](https://bintray.com/cats-oss/maven/gpuimage/_latestVersion)
[![Build Status](https://app.bitrise.io/app/d8d8090a71066e7c/status.svg?token=sJNbvX8CkecWcUA5Z898lQ&branch=master)](https://app.bitrise.io/app/d8d8090a71066e7c)

Idea from: [iOS GPUImage framework](https://github.com/BradLarson/GPUImage2)


## Requirements
* Android 2.2 or higher (OpenGL ES 2.0)

## Usage

### Gradle dependency

```groovy

dependencies {
        implementation project(path: ':gpuimagelib')
}
```

### Sample Code

#### 图片滤镜案例
```java
@Override
public void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity);
    Bitmap bitmap = 自己的bitmap
        gpuImageView = findViewById(R.id.gpuImageView);
        gpuImageView.setImage(bitmap);
        gpuImageView.setFilter(new GPUImageZoomBlurFilter(this,new PointF(0.5f, 0.5f), 1.0f));
//
}
```


#### Using GPUImageView
```xml
   <com.jqh.gpuimagelib.image.GPUImageView
       android:id="@+id/gpuImageView"
       android:layout_width="match_parent"
       android:layout_height="match_parent"/>
```

#### camera滤镜
```java
@Override
public void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity);

     cameraView = findViewById(R.id.camera_view);
     // 添加美颜滤镜
      cameraView.addFilter(new GPUImageBeautyFilter(this));
      
      // 添加图片纹理
      Bitmap bitmap = 自己的图片;
            cameraView.addTexture(new BaseTexture(this, textureKey, bitmap, 0.1f, 0.1f, 0.1f));
            
      // 添加水印
      cameraView.addTexture(new TextTexture(this, "999", "这是水印", 50, "#ff00ff", left, top, scale));
      
      // 是否开启人脸识别
      cameraView.isDetectorFace(false);
}
```

#### Using GPUImageView
```xml
      <com.jqh.gpuimagelib.camera.GPUCameraView
        android:id="@+id/camera_view"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
```
#### 直播推流:

Java:
```java
private GPUCameraView cameraView;
private JqhPushVideo jqhPushVideo;
private JqhPushEncodec jqhMediaEncodec;
@Override
public void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity);

     cameraView = findViewById(R.id.camera_view);
     // 添加美颜滤镜
      cameraView.addFilter(new GPUImageBeautyFilter(this));
      
      jqhMediaEncodec = new JqhPushEncodec(LiveActivity.this, cameraView.getTextureId());
        jqhMediaEncodec.initEncodec(cameraView.getEglContext(), 720/2, 1280/2, 44100 , 2);


        jqhMediaEncodec.setOnMediaInfoListener(new JqhPushEncodec.OnMediaInfoListener() {
            @Override
            public void onMediaTime(int times) {

            }

            @Override
            public void onSPSPPSInfo(byte[] sps, byte[] pps) {
                jqhPushVideo.pushSPSPPS(sps, pps);
            }

            @Override
            public void onVideoInfo(byte[] data, boolean keyframe) {
                jqhPushVideo.pushVideoData(data, keyframe);
            }

            @Override
            public void onAudioInfo(byte[] data) {
                jqhPushVideo.pushAudioData(data);
            }
        });
        jqhPushVideo= new JqhPushVideo();
        jqhPushVideo.setJqhConnectListener(new JqhConnectListener() {
            @Override
            public void onConnecting() {
                String threadName = Thread.currentThread().getName();
                Log.d("jqh123", "connecting " + threadName);
            }

            @Override
            public void onConnectSuccess() {
                Log.d("jqh123", "onConnectSuccess");
                jqhMediaEncodec.startRecord();
            }

            @Override
            public void onConnectFail(String msg) {
                Log.e("jqh123", msg);
            }
        });
        
        // 开始推流
        jqhPushVideo.initLivePush("rtmp://推流地址");
}
```
#### Using GPUImageView
```xml
      <com.jqh.gpuimagelib.camera.GPUCameraView
        android:id="@+id/camera_view"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
```

### Support status of [GPUImage for iOS](https://github.com/BradLarson/GPUImage2) shaders
- [x] Saturation
- [x] Contrast
- [x] Brightness
- [x] Levels
- [x] Exposure
- [x] RGB
- [x] RGB Diation
- [x] Hue
- [x] White Balance
- [x] Monochrome
- [x] False Color
- [x] Sharpen
- [ ] Unsharp Mask
- [x] Transform Operation
- [ ] Crop
- [x] Gamma
- [x] Highlights and Shadows
- [x] Haze
- [x] Sepia Tone
- [ ] Amatorka
- [ ] Miss Etikate
- [ ] Soft Elegance
- [x] Color Inversion
- [x] Solarize
- [x] Vibrance
- [ ] Highlight and Shadow Tint
- [x] Luminance
- [x] Luminance Threshold
- [ ] Average Color
- [ ] Average Luminance
- [ ] Average Luminance Threshold
- [ ] Adaptive Threshold
- [ ] Polar Pixellate
- [x] Pixellate
- [ ] Polka Dot
- [x] Halftone
- [x] Crosshatch
- [x] Sobel Edge Detection
- [ ] Prewitt Edge Detection
- [ ] Canny Edge Detection
- [x] Threshold Sobel EdgeDetection
- [ ] Harris Corner Detector
- [ ] Noble Corner Detector
- [ ] Shi Tomasi Feature Detector
- [ ] Colour FAST Feature Detector
- [ ] Low Pass Filter
- [ ] High Pass Filter
- [x] Sketch Filter
- [ ] Threshold Sketch Filter
- [x] Toon Filter
- [x] SmoothToon Filter
- [ ] Tilt Shift
- [x] CGA Colorspace Filter
- [x] Posterize
- [x] Convolution 3x3
- [x] Emboss Filter
- [x] Laplacian
- [x] Chroma Keying
- [x] Kuwahara Filter
- [ ] Kuwahara Radius3 Filter
- [x] Vignette
- [x] Gaussian Blur
- [x] Box Blur
- [x] Bilateral Blur
- [ ] Motion Blur
- [x] Zoom Blur
- [ ] iOS Blur
- [ ] Median Filter
- [x] Swirl Distortion
- [x] Bulge Distortion
- [ ] Pinch Distortion
- [x] Sphere Refraction
- [x] Glass Sphere Refraction
- [ ] Stretch Distortion
- [x] Dilation
- [ ] Erosion
- [ ] Opening Filter
- [ ] Closing Filter
- [ ] Local Binary Pattern
- [ ] Color Local Binary Pattern
- [x] Dissolve Blend
- [x] Chroma Key Blend
- [x] Add Blend
- [x] Divide Blend
- [x] Multiply Blend
- [x] Overlay Blend
- [x] Lighten Blend
- [x] Darken Blend
- [x] Color Burn Blend
- [x] Color Dodge Blend
- [x] Linear Burn Blend
- [x] Screen Blend
- [x] Difference Blend
- [x] Subtract Blend
- [x] Exclusion Blend
- [x] HardLight Blend
- [x] SoftLight Blend
- [x] Color Blend
- [x] Hue Blend
- [x] Saturation Blend
- [x] Luminosity Blend
- [x] Normal Blend
- [x] Source Over Blend
- [x] Alpha Blend
- [x] Non Maximum Suppression
- [ ] Thresholded Non Maximum Suppression
- [ ] Directional Non Maximum Suppression
- [x] Opacity
- [x] Weak Pixel Inclusion Filter
- [x] Color Matrix
- [x] Directional Sobel Edge Detection
- [x] Lookup
- [x] Tone Curve (*.acv files) 

## Others
- [x] Texture 3x3
- [x] Gray Scale

### Gradle
Make sure that you run the clean target when using maven.

```groovy
gradle clean assemble
```

## License
    Copyright 2018 CyberAgent, Inc.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
