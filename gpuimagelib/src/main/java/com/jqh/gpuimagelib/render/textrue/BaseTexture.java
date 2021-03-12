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

    private float[] vertexData = VertexUtils.getInitData1();


    public BaseTexture(Context context, String id, Bitmap bitmap) {
        this.context = context;
        this.bitmap = bitmap;
        this.id = id;
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
