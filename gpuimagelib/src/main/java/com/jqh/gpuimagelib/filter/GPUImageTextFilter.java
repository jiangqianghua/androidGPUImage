package com.jqh.gpuimagelib.filter;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;

import com.jqh.gpuimagelib.R;
import com.jqh.gpuimagelib.opengl.ShaderUtils;

// 文字水印
public class GPUImageTextFilter extends BaseGPUImageFilter {

    private String text;
    private Bitmap bitmap;
    private int bitmapTextureid;
    public GPUImageTextFilter(Context context, String text) {
        super(context);
        vertexData = new float[]{
                -0.3f, -0.3f,
                0.3f, -0.3f,
                -0.3f, 0.3f,
                0.3f, 0.3f
        };

        fragmentData = new float[] {
                0f, 0.1f,
                0.1f, 0.1f,
                0f, 0f,
                0.1f, 0f

        };


        vertexBuffer = ShaderUtils.allocateBuffer(vertexData);
        fragmentBuffer = ShaderUtils.allocateBuffer(fragmentData);
        this.text = text;

        bitmap = ShaderUtils.createTextImage(this.text, 50, "#ff0000", "#00000000", 0);
    }

    @Override
    public void init() {
        super.init();
        bitmapTextureid = ShaderUtils.loadBitmapTexture(bitmap);
    }

    @Override
    public void update() {
        super.update();
    }

    @Override
    public int getTexture() {
        return bitmapTextureid;
    }
}
