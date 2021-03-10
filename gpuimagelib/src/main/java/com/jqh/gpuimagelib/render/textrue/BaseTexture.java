package com.jqh.gpuimagelib.render.textrue;

import android.content.Context;
import android.graphics.Bitmap;

import com.jqh.gpuimagelib.opengl.ShaderUtils;

public class BaseTexture {

    protected int textureId;

    protected Bitmap bitmap;

    private Context context;

    BaseTexture(Context context, Bitmap bitmap) {
        this.context = context;
        this.bitmap = bitmap;
    }

    public void init(){
        textureId = ShaderUtils.loadBitmapTexture(bitmap);
    }

    public int getTextureId() {
        return textureId;
    }
}
