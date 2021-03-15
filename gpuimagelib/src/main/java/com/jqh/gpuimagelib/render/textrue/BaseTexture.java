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

    }
    // 显示界面宽高改变
    public void updateScreenWH(int width, int height) {
        if (width == 0 || height == 0) return ;
        if (screenHeight == height && screenWidth == width) return ;

        screenWidth = width;
        screenHeight = height;

        updateSize();
    }
    // 更新纹理大小
    public void updateSize() {

        float showLeft = screenWidth * left;
        float showTop = screenHeight * top;

        showWidth = showScale * screenWidth;
        showHeight = showScale * screenHeight;

        float scale = 1.0f * bitmap.getWidth() / bitmap.getHeight();

        if (showWidth / showHeight > scale) {
            showWidth = showHeight * scale;
        } else {
            showHeight = showWidth / scale;
        }

        // 计算位置
        vertexData = VertexUtils.createData(showLeft, showTop, showWidth, showHeight, screenWidth, screenHeight);


    }
    // 更新纹理
    public void updateTexture(float left, float top, float scale) {
        this.left = left;
        this.top = top;
        this.showScale = scale;
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
