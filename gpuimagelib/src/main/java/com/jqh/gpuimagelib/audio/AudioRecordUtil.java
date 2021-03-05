package com.jqh.gpuimagelib.audio;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

public class AudioRecordUtil {

    private int bufferSizeInBytes;
    private AudioRecord audioRecord;
    private boolean start = false;
    public boolean isStart(){
        return start;
    }
    private int readSize = 0;
    public AudioRecordUtil(){
        bufferSizeInBytes =  AudioRecord.getMinBufferSize(44100,
                AudioFormat.CHANNEL_IN_STEREO,
                AudioFormat.ENCODING_PCM_16BIT);
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                44100,
                AudioFormat.CHANNEL_IN_STEREO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSizeInBytes);
    }

    public void startRecord(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                audioRecord.startRecording();
                start = true;
                byte[] audiodata = new byte[bufferSizeInBytes];
                while(start) {
                    readSize = audioRecord.read(audiodata, 0 , bufferSizeInBytes);
                    if (onRecordListener != null) {
                        onRecordListener.recordByte(audiodata, readSize);
                    }
                }
                if (audioRecord != null) {
                    audioRecord.stop();
                    audioRecord.release();
                    audioRecord = null;
                }
            }
        }).start();
    }

    public void stop(){
        start = false;
    }

    private OnRecordListener onRecordListener;

    public void setOnRecordListener(OnRecordListener onRecordListener) {
        this.onRecordListener = onRecordListener;
    }

    public interface OnRecordListener{
        void recordByte(byte[] audioData, int readSize);
    }
}
