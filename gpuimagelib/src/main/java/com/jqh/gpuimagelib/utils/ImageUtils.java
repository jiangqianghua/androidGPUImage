package com.jqh.gpuimagelib.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

public class ImageUtils {
    public static Bitmap compress(Bitmap bm) {
        int len = bm.getByteCount(); // 原始图片的byte[]数组大小
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 10, out);
        int compressedLen = out.toByteArray().length; // 这里out.toByteArray()所返回的byte[]数组大小确实变小了！
        Bitmap compressedBm = BitmapFactory.decodeByteArray(out.toByteArray(), 0, compressedLen);
        return compressedBm;
    }
}
