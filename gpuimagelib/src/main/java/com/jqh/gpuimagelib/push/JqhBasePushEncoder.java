package com.jqh.gpuimagelib.push;

import android.content.Context;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.util.Log;
import android.view.Surface;

import com.jqh.gpuimagelib.audio.AudioRecordUtil;
import com.jqh.gpuimagelib.opengl.EglHelper;
import com.jqh.gpuimagelib.opengl.GLSurfaceView;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;

import javax.microedition.khronos.egl.EGLContext;

public abstract class JqhBasePushEncoder {

    private Surface surface;
    private EGLContext eglContext;

    private int width;
    private int height;

    private MediaCodec videoEncodec;
    private MediaFormat videoFormat;
    private MediaCodec.BufferInfo videoBufferinfo;

    private MediaCodec audioEncodec;
    private MediaFormat audioFormat;
    private MediaCodec.BufferInfo audioBufferinfo;
    private long audioPts = 0;
    private int sampleRate;

    private WlEGLMediaThread wlEGLMediaThread;
    private VideoEncodecThread videoEncodecThread;
    private AudioEncodecThread audioEncodecThread;
    private AudioRecordUtil audioRecordUtil;


    private GLSurfaceView.GLRender wlGLRender;

    public final static int RENDERMODE_WHEN_DIRTY = 0;
    public final static int RENDERMODE_CONTINUOUSLY = 1;
    private int mRenderMode = RENDERMODE_CONTINUOUSLY;

    private OnMediaInfoListener onMediaInfoListener;


    public JqhBasePushEncoder(Context context) {
    }

    public void setRender(GLSurfaceView.GLRender wlGLRender) {
        this.wlGLRender = wlGLRender;
    }

    public void setmRenderMode(int mRenderMode) {
        if(wlGLRender == null)
        {
            throw  new RuntimeException("must set render before");
        }
        this.mRenderMode = mRenderMode;
    }

    public void setOnMediaInfoListener(OnMediaInfoListener onMediaInfoListener) {
        this.onMediaInfoListener = onMediaInfoListener;
    }

    public void initEncodec(EGLContext eglContext, int width, int height, int sampleRate, int channelCount)
    {
        this.width = width;
        this.height = height;
        this.eglContext = eglContext;
        initMediaEncodec(width, height, sampleRate, channelCount);
        initPCMRecord();
    }

    private void initPCMRecord(){
        audioRecordUtil = new AudioRecordUtil();
        audioRecordUtil.setOnRecordListener(new AudioRecordUtil.OnRecordListener() {
            @Override
            public void recordByte(byte[] audioData, int readSize) {
                if (audioRecordUtil.isStart()) putPCMData(audioData, readSize);
            }
        });
    }

    public void startRecord()
    {
        if(surface != null && eglContext != null)
        {

            audioPts = 0;

            wlEGLMediaThread = new WlEGLMediaThread(new WeakReference<JqhBasePushEncoder>(this));
            videoEncodecThread = new VideoEncodecThread(new WeakReference<JqhBasePushEncoder>(this));
            audioEncodecThread = new AudioEncodecThread(new WeakReference<JqhBasePushEncoder>(this));
            wlEGLMediaThread.isCreate = true;
            wlEGLMediaThread.isChange = true;
            wlEGLMediaThread.start();
            videoEncodecThread.start();
            audioEncodecThread.start();
            audioRecordUtil.startRecord();
        }
    }

    public void stopRecord()
    {
        if(wlEGLMediaThread != null && videoEncodecThread != null && audioEncodecThread != null)
        {
            audioRecordUtil.stop();
            videoEncodecThread.exit();
            audioEncodecThread.exit();
            wlEGLMediaThread.onDestory();
            videoEncodecThread = null;
            wlEGLMediaThread = null;
            audioEncodecThread = null;
        }
    }

    private void initMediaEncodec(int width, int height, int sampleRate, int channelCount)
    {
        initVideoEncodec(MediaFormat.MIMETYPE_VIDEO_AVC, width, height);
        initAudioEncodec(MediaFormat.MIMETYPE_AUDIO_AAC, sampleRate, channelCount);
    }


    private void initVideoEncodec(String mimeType, int width, int height)
    {
        try {
            videoBufferinfo = new MediaCodec.BufferInfo();
            videoFormat = MediaFormat.createVideoFormat(mimeType, width, height);
            videoFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
            videoFormat.setInteger(MediaFormat.KEY_BIT_RATE, width * height * 4);
            videoFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 30);
            videoFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1);

            videoEncodec = MediaCodec.createEncoderByType(mimeType);
            videoEncodec.configure(videoFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);

            surface = videoEncodec.createInputSurface();

        } catch (IOException e) {
            e.printStackTrace();
            videoEncodec = null;
            videoFormat = null;
            videoBufferinfo = null;
        }

    }

    private void initAudioEncodec(String mimeType, int sampleRate, int channelCount)
    {
        try {
            this.sampleRate = sampleRate;
            audioBufferinfo = new MediaCodec.BufferInfo();
            audioFormat = MediaFormat.createAudioFormat(mimeType, sampleRate, channelCount);
            audioFormat.setInteger(MediaFormat.KEY_BIT_RATE, 96000);
            audioFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);
            audioFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 4096 * 10);

            audioEncodec = MediaCodec.createEncoderByType(mimeType);
            audioEncodec.configure(audioFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);

        } catch (IOException e) {
            e.printStackTrace();
            audioBufferinfo = null;
            audioFormat = null;
            audioEncodec = null;
        }
    }

    public void putPCMData(byte[] buffer, int size)
    {
        if(audioEncodecThread != null && !audioEncodecThread.isExit && buffer != null && size > 0)
        {
            int inputBufferindex = audioEncodec.dequeueInputBuffer(0);
            if(inputBufferindex >= 0)
            {
                ByteBuffer byteBuffer = audioEncodec.getInputBuffers()[inputBufferindex];
                byteBuffer.clear();
                byteBuffer.put(buffer);
                long pts = getAudioPts(size, sampleRate);
                audioEncodec.queueInputBuffer(inputBufferindex, 0, size, pts, 0);
            }
        }
    }

    static class WlEGLMediaThread extends Thread
    {
        private WeakReference<JqhBasePushEncoder> encoder;
        private EglHelper eglHelper;
        private Object object;

        private boolean isExit = false;
        private boolean isCreate = false;
        private boolean isChange = false;
        private boolean isStart = false;

        public WlEGLMediaThread(WeakReference<JqhBasePushEncoder> encoder) {
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

            while(true)
            {
                if(isExit)
                {
                    release();
                    break;
                }

                if(isStart)
                {
                    if(encoder.get().mRenderMode == RENDERMODE_WHEN_DIRTY)
                    {
                        synchronized (object)
                        {
                            try {
                                object.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    else if(encoder.get().mRenderMode == RENDERMODE_CONTINUOUSLY)
                    {
                        try {
                            Thread.sleep(1000 / 60);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    else
                    {
                        throw  new RuntimeException("mRenderMode is wrong value");
                    }
                }
                onCreate();
                onChange(encoder.get().width, encoder.get().height);
                onDraw();
                isStart = true;
            }

        }

        private void onCreate()
        {
            if(isCreate && encoder.get().wlGLRender != null)
            {
                isCreate = false;
                encoder.get().wlGLRender.onSurfaceCreate();
            }
        }

        private void onChange(int width, int height)
        {
            if(isChange && encoder.get().wlGLRender != null)
            {
                isChange = false;
                encoder.get().wlGLRender.onSurfaceChanged(width, height);
            }
        }

        private void onDraw()
        {
            if(encoder.get().wlGLRender != null && eglHelper != null)
            {
                encoder.get().wlGLRender.onDrawFrame();
                if(!isStart)
                {
                    encoder.get().wlGLRender.onDrawFrame();
                }
                eglHelper.swapBuffers();

            }
        }

        private void requestRender()
        {
            if(object != null)
            {
                synchronized (object)
                {
                    object.notifyAll();
                }
            }
        }

        public void onDestory()
        {
            isExit = true;
            requestRender();
        }

        public void release()
        {
            if(eglHelper != null)
            {
                eglHelper.distoryEgl();
                eglHelper = null;
                object = null;
                encoder = null;
            }
        }
    }

    static class VideoEncodecThread extends Thread
    {
        private WeakReference<JqhBasePushEncoder> encoder;

        private boolean isExit;

        private MediaCodec videoEncodec;
        private MediaCodec.BufferInfo videoBufferinfo;

        private long pts;
        private byte[] sps;
        private byte[] pps;
        private boolean keyFrame = false;

        public VideoEncodecThread(WeakReference<JqhBasePushEncoder> encoder) {
            this.encoder = encoder;
            videoEncodec = encoder.get().videoEncodec;
            videoBufferinfo = encoder.get().videoBufferinfo;
        }

        @Override
        public void run() {
            super.run();
            pts = 0;
            isExit = false;
            videoEncodec.start();
            while(true)
            {
                if(isExit)
                {

                    videoEncodec.stop();
                    videoEncodec.release();
                    videoEncodec = null;
                    Log.d("ywl5320", "录制完成");
                    break;
                }

                int outputBufferIndex = videoEncodec.dequeueOutputBuffer(videoBufferinfo, 0);
                keyFrame = false;
                if(outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED)
                {
                    Log.d("ywl5320", "INFO_OUTPUT_FORMAT_CHANGED");

                    ByteBuffer spsb = videoEncodec.getOutputFormat().getByteBuffer("csd-0");
                    sps = new byte[spsb.remaining()];
                    spsb.get(sps, 0, sps.length);

                    ByteBuffer ppsb = videoEncodec.getOutputFormat().getByteBuffer("csd-1");
                    pps = new byte[ppsb.remaining()];
                    ppsb.get(pps, 0, pps.length);

                    Log.d("jqh123", "sps:" + byteToHex(sps));
                    Log.d("jqh123", "pps:" + byteToHex(pps));

                }
                else
                {
                    while (outputBufferIndex >= 0)
                    {
                        ByteBuffer outputBuffer = videoEncodec.getOutputBuffers()[outputBufferIndex];
                        outputBuffer.position(videoBufferinfo.offset);
                        outputBuffer.limit(videoBufferinfo.offset + videoBufferinfo.size);
                        //
                        if(pts == 0)
                        {
                            pts = videoBufferinfo.presentationTimeUs;
                        }
                        videoBufferinfo.presentationTimeUs = videoBufferinfo.presentationTimeUs - pts;


                        byte[] data = new byte[outputBuffer.remaining()];
                        outputBuffer.get(data, 0, data.length);
                        Log.d("jqh123", "data:" + byteToHex(data));

                        // 判断是否是关键帧
                        if (videoBufferinfo.flags == MediaCodec.BUFFER_FLAG_KEY_FRAME) {
                            keyFrame = true;
                            // 如果是关键帧，先回调sps和pps，这样可以后进的用户也能观看
                            if (encoder.get().onMediaInfoListener != null) {
                                encoder.get().onMediaInfoListener.onSPSPPSInfo(sps, pps);
                            }
                        }
                        // 返回数据编码后的数据
                        if (encoder.get().onMediaInfoListener != null) {
                            encoder.get().onMediaInfoListener.onVideoInfo(data, keyFrame);
                            encoder.get().onMediaInfoListener.onMediaTime((int) (videoBufferinfo.presentationTimeUs / 1000000));
                        }

                        videoEncodec.releaseOutputBuffer(outputBufferIndex, false);
                        outputBufferIndex = videoEncodec.dequeueOutputBuffer(videoBufferinfo, 0);
                    }
                }
            }
        }

        public void exit()
        {
            isExit = true;
        }

    }

    static class AudioEncodecThread extends Thread
    {

        private WeakReference<JqhBasePushEncoder> encoder;
        private boolean isExit;

        private MediaCodec audioEncodec;
        private MediaCodec.BufferInfo bufferInfo;

        long pts;


        public AudioEncodecThread(WeakReference<JqhBasePushEncoder> encoder) {
            this.encoder = encoder;
            audioEncodec = encoder.get().audioEncodec;
            bufferInfo = encoder.get().audioBufferinfo;
        }

        @Override
        public void run() {
            super.run();
            pts = 0;
            isExit = false;
            audioEncodec.start();
            while(true)
            {
                if(isExit)
                {
                    //

                    audioEncodec.stop();
                    audioEncodec.release();
                    audioEncodec = null;
                    break;
                }

                int outputBufferIndex = audioEncodec.dequeueOutputBuffer(bufferInfo, 0);
                if(outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED)
                {
                }
                else
                {
                    while(outputBufferIndex >= 0)
                    {

                        ByteBuffer outputBuffer = audioEncodec.getOutputBuffers()[outputBufferIndex];
                        outputBuffer.position(bufferInfo.offset);
                        outputBuffer.limit(bufferInfo.offset + bufferInfo.size);
                        if(pts == 0)
                        {
                            pts = bufferInfo.presentationTimeUs;
                        }
                        bufferInfo.presentationTimeUs = bufferInfo.presentationTimeUs - pts;

                        // 获取编码后的aac数据
                        byte[] data = new byte[outputBuffer.remaining()];
                        outputBuffer.get(data, 0, data.length);
                        if (data != null && encoder.get().onMediaInfoListener != null) {
                            encoder.get().onMediaInfoListener.onAudioInfo(data);
                        }
                        audioEncodec.releaseOutputBuffer(outputBufferIndex, false);
                        outputBufferIndex = audioEncodec.dequeueOutputBuffer(bufferInfo, 0);
                    }
                }

            }

        }
        public void exit()
        {
            isExit = true;
        }
    }

    public interface OnMediaInfoListener
    {
        void onMediaTime(int times);
        void onSPSPPSInfo(byte[] sps, byte[] pps);
        void onVideoInfo(byte[] data, boolean keyframe);
        void onAudioInfo(byte[] data);
    }

    private long getAudioPts(int size, int sampleRate)
    {
        audioPts += (long)(1.0 * size / (sampleRate * 2 * 2) * 1000000.0);
        return audioPts;
    }

    public static String byteToHex(byte[] bytes)
    {
        StringBuffer stringBuffer = new StringBuffer();
        for(int i = 0; i < bytes.length; i++)
        {
            String hex = Integer.toHexString(bytes[i]);
            if(hex.length() == 1)
            {
                stringBuffer.append("0" + hex);
            }
            else
            {
                stringBuffer.append(hex);
            }
            if(i > 20)
            {
                break;
            }
        }
        return stringBuffer.toString();
    }

}
