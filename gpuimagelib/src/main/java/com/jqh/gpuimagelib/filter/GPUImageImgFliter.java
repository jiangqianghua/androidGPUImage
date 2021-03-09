package com.jqh.gpuimagelib.filter;

import android.content.Context;
import android.graphics.Bitmap;

import com.jqh.gpuimagelib.opengl.ShaderUtils;

public class GPUImageImgFliter extends BaseGPUImageFilter {
    private Bitmap bitmap;
    private int textureId;
    public GPUImageImgFliter(Context context, Bitmap bitmap) {
        super(context);
        this.bitmap = bitmap;

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
    }

    @Override
    public void init() {
        super.init();
        textureId = ShaderUtils.loadBitmapTexture(bitmap);
    }

    @Override
    public int getTexture() {
        return textureId;
    }


}
