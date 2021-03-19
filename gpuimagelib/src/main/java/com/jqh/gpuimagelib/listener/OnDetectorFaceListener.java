package com.jqh.gpuimagelib.listener;

import android.graphics.Bitmap;
import android.graphics.RectF;

public interface OnDetectorFaceListener {
    void onDetectorRect(RectF rectF);
    void onBitmap(Bitmap bitmap);
}
