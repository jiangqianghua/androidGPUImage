package com.jqh.gpuimagelib.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

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

    public static Bitmap matrix(Bitmap bit) {
        Matrix matrix = new Matrix();
        matrix.setScale(0.25f, 0.25f);
        Bitmap bitmap = Bitmap.createBitmap(bit, 0, 0, bit.getWidth(),
                bit.getHeight(), matrix, true);
//        bitmap = compress(bitmap);
        return bitmap;
    }

}
