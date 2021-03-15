package com.jqh.gpuimagelib.render.textrue;

import android.content.Context;
import android.graphics.Bitmap;

import com.jqh.gpuimagelib.opengl.ShaderUtils;
import com.jqh.gpuimagelib.utils.VertexUtils;

public class BaseTexture {

    private String id;
    protected int textureId = 0;

    protected Bitmap bitmap;

    private Context context;

    protected float width = 0f, height = 0f;

    protected float left = 0.1f, top = 1.0f;

    private float scale = 0f;

    private float[] vertexData = VertexUtils.getInitData();


    public BaseTexture(Context context, String id, Bitmap bitmap) {
        this.context = context;
        this.bitmap = bitmap;
        this.id = id;

        width = bitmap.getWidth();
        height = bitmap.getHeight();
        // 计算图片宽高比例
        scale = 1.0f * width / height;
    }

    public void init(){
        if (textureId > 0) return ;
        textureId = ShaderUtils.loadBitmapTexture(bitmap);
    }

    public int getTextureId() {
        return textureId;
    }

    public String getId() {
        return id;
    }

    public float[] getVertexData() {
        return vertexData;
    }
}
