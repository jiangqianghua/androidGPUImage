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


    protected float showWidth = 0, showHeight = 0;

    protected int screenWidth = 0 , screenHeight = 0;

    protected float left = 0f, top = 0;

    private float showScale = 0f;

    private float[] vertexData = VertexUtils.getInitData();


    public BaseTexture(Context context, String id, Bitmap bitmap, float left, float top, float showScale) {
        this.context = context;
        this.bitmap = bitmap;
        this.id = id;
        this.left = left;
        this.top = top;
        this.showScale = showScale;

//        int width = bitmap.getWidth();
//        int height = bitmap.getHeight();
//        // 计算图片宽高比例
//        float scale = 1.0f * width / height;
//
//        if (w / h > scale) {
//            w = h * scale;
//        } else {
//            h = w / scale;
//        }
    }

    public void updateSize(int width, int height) {
        if (width == 0 || height == 0) return ;
        if (screenHeight == height && screenWidth == width) return ;

        screenWidth = width;
        screenHeight = height;

        float showLeft = width * left;
        float showTop = height * top;

        showWidth = showScale * width;
        showHeight = showScale * height;

        float scale = 1.0f * bitmap.getWidth() / bitmap.getHeight();

        if (showWidth / showHeight > scale) {
            showWidth = showHeight * scale;
        } else {
            showHeight = showWidth / scale;
        }

        // 计算位置
        vertexData = VertexUtils.createData(showLeft, showTop, showWidth, showHeight, screenWidth, screenHeight);


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
