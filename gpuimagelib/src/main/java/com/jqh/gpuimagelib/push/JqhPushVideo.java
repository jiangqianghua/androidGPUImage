package com.jqh.gpuimagelib.push;

import android.text.TextUtils;

public class JqhPushVideo {
    static {
        System.loadLibrary("jqhpush");
    }

    private JqhConnectListener jqhConnectListener;

    public void setJqhConnectListener(JqhConnectListener jqhConnectListener) {
        this.jqhConnectListener = jqhConnectListener;
    }

    private void onConnecting(){
        if (jqhConnectListener != null) {
            jqhConnectListener.onConnecting();
        }
    }



    private void onConnectSuccess(){
        if (jqhConnectListener != null) {
            jqhConnectListener.onConnectSuccess();
        }
    }


    private void onConnectFail(String msg){
        if (jqhConnectListener != null) {
            jqhConnectListener.onConnectFail(msg);
        }
    }

    public void initLivePush(String url) {
        if (!TextUtils.isEmpty(url)) {
            initPush(url);
        }
    }

    public void pushSPSPPS(byte[] sps, byte[] pps) {
        if (sps != null && pps != null){
            pushSPSPPS(sps, sps.length, pps, pps.length);
        }
    }

    public void pushVideoData(byte[] data, boolean keyframe) {
        if (data != null) pushVideoData(data, data.length, keyframe);
    }

    public void pushAudioData(byte[] data) {
        if (data != null) pushAudioData(data, data.length);
    }

    public void stopPush(){
        pushStop();
    }

    private native void initPush(String pushUrl);

    private native void pushSPSPPS(byte[] sps, int sps_len, byte[] pps, int pps_len);

    private native void pushVideoData(byte[] data, int data_len, boolean keyframe);

    private native void pushAudioData(byte[] data, int data_len);

    public native void pushStop();

    public native void startRecordAudio(String path);

    public native void stopRecordAudio();
}
