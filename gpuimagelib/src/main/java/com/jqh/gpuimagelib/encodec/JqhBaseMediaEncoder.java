package com.jqh.gpuimagelib.encodec;

import android.content.Context;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.util.Log;
import android.view.Surface;


import com.jqh.gpuimagelib.opengl.EglHelper;
import com.jqh.gpuimagelib.opengl.GLSurfaceView;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;

import javax.microedition.khronos.egl.EGLContext;

public class JqhBaseMediaEncoder {

    private Surface surface;
    private EGLContext eglContext;
    public GLSurfaceView.GLRender glRender;
    public final static int RENDERMODE_WHEN_DIRTY = 0 ; // 手动刷新
    public final static int RENDERMODE_CONTINUOUSLY = 1; // 自动刷新

    private int renderMode = RENDERMODE_CONTINUOUSLY ;
    private int width;
    private int height;

    private MediaMuxer mediaMuxer;
    private MediaCodec videoEncodec;
    private MediaFormat videoFormat;
    private MediaCodec.BufferInfo videoBufferinfo;

    private MediaCodec audioEncodec;
    private MediaFormat audioFormat;
    private MediaCodec.BufferInfo audioBufferinfo;
    private AudioEncodecThread audioEncodecThread;
    private long audioPts = 0;
    private int sampleRate;

    private boolean encodecStart;

    private JqhGLMediaThread jqhGLMediaThread;
    private VideoEncodecThread videoEncodecThread;

    private OnMediaInfoListener onMediaInfoListener;

    private boolean audioExit;
    private boolean videoExit;

    public void setOnMediaInfoListener(OnMediaInfoListener onMediaInfoListener) {
        this.onMediaInfoListener = onMediaInfoListener;
    }

    public JqhBaseMediaEncoder(Context context) {

    }

    public void setRender(GLSurfaceView.GLRender glRender) {
        this.glRender = glRender;
    }

    public void setRenderMode(int renderMode) {
        if (glRender == null) {
            throw new RuntimeException("must set redner before");

        }
        this.renderMode = renderMode;
    }
    // 提供给外部调用的初始化方法
    public  void initEncodec(EGLContext eglContext, String savePath, int width, int height, int sampleRate, int channelCount) {
        this.width = width;
        this.height = height;
        this.eglContext = eglContext;
        initMediaEncodec(savePath, width, height, sampleRate, channelCount);
    }

    public void startRecord(){
        if (surface != null && eglContext != null) {
            audioExit = false;
            videoExit = false;
            encodecStart = false;
            audioPts = 0;
            jqhGLMediaThread = new JqhGLMediaThread(new WeakReference<JqhBaseMediaEncoder>(this));
            videoEncodecThread = new VideoEncodecThread(new WeakReference<JqhBaseMediaEncoder>(this));
            audioEncodecThread = new AudioEncodecThread(new WeakReference<JqhBaseMediaEncoder>(this));
            jqhGLMediaThread.isCreate = true;
            jqhGLMediaThread.isChange = true;
            jqhGLMediaThread.start();
            videoEncodecThread.start();
            audioEncodecThread.start();

        }
    }

    public void stopRecord(){
        if (jqhGLMediaThread != null && videoEncodecThread != null && audioEncodecThread != null) {
            videoEncodecThread.exit();
            audioEncodecThread.exit();
            jqhGLMediaThread.onDestory();
            jqhGLMediaThread = null;
            videoEncodecThread = null;
            audioEncodecThread = null;
        }
    }

    private void initMediaEncodec(String savePath, int width, int height, int sampleRate, int channelCount) {
        try{
            mediaMuxer = new MediaMuxer(savePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            initVideoEncodec(MediaFormat.MIMETYPE_VIDEO_AVC, width, height);
            initAudioEncodec(MediaFormat.MIMETYPE_AUDIO_AAC, sampleRate, channelCount);
        }catch (IOException e) {
            e.printStackTrace();
        }

    }
    private void initVideoEncodec(String mimeType, int width, int height) {
        try {
            videoBufferinfo = new MediaCodec.BufferInfo();
            videoFormat = MediaFormat.createVideoFormat(mimeType, width, height);
            // 格式是surface，因为我们是从surface取数据
            videoFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
            //设置码率, 每个像素 * argb 四个字节
            videoFormat.setInteger(MediaFormat.KEY_BIT_RATE, width * height * 4);
            //帧率
            videoFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 30);
            // 设置关键帧间隔  1秒一个关键帧
            videoFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1);

            videoEncodec = MediaCodec.createEncoderByType(mimeType);
            videoEncodec.configure(videoFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);

            surface = videoEncodec.createInputSurface();
        }catch (IOException e) {
            e.printStackTrace();
            videoEncodec = null;
            videoFormat = null;
            videoBufferinfo = null;

        }

    }

    private void initAudioEncodec (String mimeType, int sampleRate, int channelCount) {
        try{
            this.sampleRate = sampleRate;
            audioBufferinfo = new MediaCodec.BufferInfo();
            audioFormat = MediaFormat.createAudioFormat(mimeType, sampleRate, channelCount);
            audioFormat.setInteger(MediaFormat.KEY_BIT_RATE, 96000);
            audioFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);
            audioFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 4096 * 2);
            audioEncodec = MediaCodec.createEncoderByType(mimeType);
            audioEncodec.configure(audioFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        }catch (Exception e) {
            e.printStackTrace();
            audioBufferinfo = null;
            audioFormat = null;
            audioEncodec = null;
        }

    }

    public void putPCMData(byte[] buffer, int size) {
        if (audioEncodecThread != null && !audioEncodecThread.isExit && buffer != null && size > 0) {
            int inputBufferindex = audioEncodec.dequeueInputBuffer(0);
            if (inputBufferindex >= 0) {
                ByteBuffer byteBuffer = audioEncodec.getInputBuffers()[inputBufferindex];
                byteBuffer.clear();
                byteBuffer.put(buffer);
                long pts = getAudioPts(size, sampleRate);
                audioEncodec.queueInputBuffer(inputBufferindex, 0, size, pts, 0);
            }
        }
    }

    static class JqhGLMediaThread extends  Thread {
        private WeakReference<JqhBaseMediaEncoder> encoder;

        private EglHelper eglHelper;
        private Object object;

        private boolean isCreate = false;
        private boolean isExit = false;
        private boolean isChange = false;
        private  boolean isStart = false;


        public JqhGLMediaThread(WeakReference<JqhBaseMediaEncoder> encoder) {
            this.encoder = encoder;
        }

        @Override
        public void run() {
            super.run();
            isExit = false;
            isStart = false;
            object = new Object();
            eglHelper = new EglHelper();
            eglHelper.initEgl(encoder.get().surface, encoder.get().eglContext);
            while(true) {
                if (isExit) {
                    release();
                    break;
                }
                if (isStart) {
                    // isStart是为了保证第二遍执行是否手动刷新
                    if (encoder.get().renderMode == RENDERMODE_WHEN_DIRTY) {
                        // 手动刷新，等待
                        synchronized (object) {
                            try{
                                object.wait();
                            }catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                        }
                    } else if (encoder.get().renderMode == RENDERMODE_CONTINUOUSLY) {
                        try{
                            Thread.sleep(1000/60);
                        }catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        throw  new RuntimeException("rednerMode in wrong value");
                    }
                }


                onCreate();
                onChange(encoder.get().width, encoder.get().height);
                onDraw();
                isStart = true;
            }
        }

        private void onCreate(){
            if (isCreate && encoder.get().glRender != null) {
                isCreate = false;
                encoder.get().glRender.onSurfaceCreate();
            }
        }

        private void onChange(int width, int height) {
            if (isChange && encoder.get().glRender != null) {
                isChange = false;
                encoder.get().glRender.onSurfaceChanged(width, height);
            }
        }

        private void onDraw(){
            if (encoder.get().glRender != null && eglHelper != null) {
                encoder.get().glRender.onDrawFrame();
                // 首次保证被调用两次，否则显示不出来，这是opengl的一个bug
                if (!isStart) {
                    encoder.get().glRender.onDrawFrame();
                }
                eglHelper.swapBuffers();
            }
        }
        // 请求刷新
        private void requestRender(){
            if (object != null) {
                synchronized (object) {
                    object.notifyAll();
                }
            }
        }

        public void onDestory(){
            isExit = true;
            // 释放object锁
            requestRender();
        }

        public void release(){
            if (eglHelper != null) {
                eglHelper.distoryEgl();
                eglHelper = null;
                object = null;
                encoder = null;
            }
        }

    }


    static class VideoEncodecThread extends Thread {
        private WeakReference<JqhBaseMediaEncoder> encoder;
        private boolean ixExit;
        private MediaCodec videoEncoder;
        private MediaFormat videoFormat;
        private MediaCodec.BufferInfo videoBufferinfo;
        private boolean isExit;
        private int videoTrackIndex = -1;
        private MediaMuxer mediaMuxer;
        private long pts;
        public VideoEncodecThread(WeakReference<JqhBaseMediaEncoder> encoder) {
            this.encoder = encoder;
            videoEncoder = encoder.get().videoEncodec;
            videoFormat = encoder.get().videoFormat;
            videoBufferinfo = encoder.get().videoBufferinfo;
            mediaMuxer = encoder.get().mediaMuxer;
            videoTrackIndex = -1;
        }

        @Override
        public void run() {
            super.run();
            pts = 0;
            videoTrackIndex = -1;
            isExit = false;
            videoEncoder.start();
            while (true) {
                if (isExit) {
                    videoEncoder.stop();
                    videoEncoder.release();
                    videoEncoder = null;

                    // 停止mediamuxer，用来写头部信息
                    encoder.get().videoExit = true;
                    if (encoder.get().audioExit) {
                        mediaMuxer.stop();
                        mediaMuxer.release();
                        mediaMuxer = null;
                    }
                    Log.d("jqh123", "录制完成");
                    break;
                }
                // 得到一个输出队列的索引
                int outputBufferIndex = videoEncoder.dequeueOutputBuffer(videoBufferinfo, 0);
                if (outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                    // 如果格式发生变化，执行该代码
                    videoTrackIndex = mediaMuxer.addTrack(videoEncoder.getOutputFormat());
                    if (encoder.get().audioEncodecThread.audioTrackIndex != -1) {
                        mediaMuxer.start();
                        encoder.get().encodecStart = true;
                    }
                } else {
                    while (outputBufferIndex >= 0) {
                        if (encoder.get().encodecStart) {
                            // 从索引里面获取buffer数据
                            ByteBuffer outputBuffer = videoEncoder.getOutputBuffers()[outputBufferIndex];
                            // 设置便宜位置
                            outputBuffer.position(videoBufferinfo.offset);
                            // 设置从偏移位置到size的大小范围
                            outputBuffer.limit(videoBufferinfo.offset + videoBufferinfo.size);
                            // 开始编码
                            // 写入
                            if (pts == 0) {
                                pts = videoBufferinfo.presentationTimeUs;
                            }
                            videoBufferinfo.presentationTimeUs = videoBufferinfo.presentationTimeUs - pts;
                            mediaMuxer.writeSampleData(videoTrackIndex, outputBuffer, videoBufferinfo );
                            if (encoder.get().onMediaInfoListener != null) {
                                encoder.get().onMediaInfoListener.onMediaTime(videoBufferinfo.presentationTimeUs/1000000);
                            }
                        }
                        // 编码完成，释放资源
                        videoEncoder.releaseOutputBuffer(outputBufferIndex, false);
                        // 再从队列获取数据
                        outputBufferIndex = videoEncoder.dequeueOutputBuffer(videoBufferinfo, 0);
                    }
                }

            }

        }

        public void exit(){
            isExit = true;
        }
    }

    static class AudioEncodecThread extends  Thread {
        private WeakReference<JqhBaseMediaEncoder> encoder;
        private boolean isExit;
        private MediaCodec audioEncodec;
        private MediaCodec.BufferInfo bufferInfo;
        private MediaMuxer mediaMuxer;

        private int audioTrackIndex = -1;
        long pts;

        public AudioEncodecThread(WeakReference<JqhBaseMediaEncoder> encoder) {
            this.encoder = encoder;
            audioEncodec = encoder.get().audioEncodec;
            bufferInfo = encoder.get().audioBufferinfo;
            mediaMuxer = encoder.get().mediaMuxer;
            audioTrackIndex = -1;

        }

        @Override
        public void run() {
            super.run();
            pts = 0;
            audioTrackIndex = -1;
            isExit = false;
            audioEncodec.start();
            while (true) {
                if (isExit) {

                    audioEncodec.stop();
                    audioEncodec.release();
                    audioEncodec = null;

                    encoder.get().audioExit = true;
                    if (encoder.get().videoExit) {
                        mediaMuxer.stop();
                        mediaMuxer.release();
                        mediaMuxer = null;
                    }
                    break;
                }

                int outputBufferIndex = audioEncodec.dequeueOutputBuffer(bufferInfo, 0);
                if (outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED){
                    if (mediaMuxer != null) {
                        audioTrackIndex = mediaMuxer.addTrack(audioEncodec.getOutputFormat());
                    // 只有audio 和video 的index都不等于-1， 就开启
                    if (encoder.get().videoEncodecThread.videoTrackIndex != -1) {
                            mediaMuxer.start();
                            encoder.get().encodecStart = true; // 编码开始
                        }
                    }
                } else {
                    while (outputBufferIndex >= 0) {
                        if (encoder.get().encodecStart) {
                            ByteBuffer outputBuffer = audioEncodec.getOutputBuffers()[outputBufferIndex];
                            outputBuffer.position(bufferInfo.offset);
                            outputBuffer.limit(bufferInfo.offset + bufferInfo.size);
                            if (pts == 0) {
                                pts = bufferInfo.presentationTimeUs;
                            }
                            bufferInfo.presentationTimeUs = bufferInfo.presentationTimeUs - pts;
                            mediaMuxer.writeSampleData(audioTrackIndex, outputBuffer, bufferInfo);
                        }
                        audioEncodec.releaseOutputBuffer(outputBufferIndex, false);
                        outputBufferIndex = audioEncodec.dequeueOutputBuffer(bufferInfo, 0);
                    }
                }

            }
        }

        public void exit(){
            isExit = true;
        }
    }
    public interface  OnMediaInfoListener{
        void onMediaTime(long times);
    }

    private long getAudioPts(int size, int sampleRate) {
        audioPts += (long)(1.0 * size / (sampleRate * 2 * 2) * 1000000.0);
        return audioPts;
    }
}
