package com.jqh.gpuimagelib.listener;

import android.graphics.Bitmap;
import android.graphics.RectF;

public interface OnDetectorFaceListener {
    void onDetectorRect(RectF rectF, int w, int h);
    void onBitmap(Bitmap bitmap);
}
