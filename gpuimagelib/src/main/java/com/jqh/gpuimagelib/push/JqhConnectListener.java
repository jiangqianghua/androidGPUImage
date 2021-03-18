package com.jqh.gpuimagelib.push;

public interface JqhConnectListener {
    void onConnecting();
    void onConnectSuccess();
    void onConnectFail(String msg);
}
