package com.jqh.gpuimagelib.encodec;

import android.content.Context;
import android.opengl.GLES20;


import com.jqh.gpuimagelib.opengl.GLSurfaceView;

import com.jqh.gpuimagelib.render.CommonFboRender;
import com.jqh.gpuimagelib.render.filter.BaseGPUImageFilter;
import com.jqh.gpuimagelib.render.textrue.BaseTexture;

public class JqhEncodecRender implements GLSurfaceView.GLRender {

    private Context context;

    private CommonFboRender commonFboRender;

    private int textureId;

    public JqhEncodecRender(Context context, int textureid) {
        this.context = context;
        this.textureId = textureid;
        commonFboRender = new CommonFboRender();
        commonFboRender.init(context);
    }

    @Override
    public void onSurfaceCreate() {
        // 设置一下两个可以让水印背景透明
        GLES20.glEnable (GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        commonFboRender.onCreate();
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        GLES20.glViewport(0,0, width, height);
        commonFboRender.setWH(width, height);
    }

    @Override
    public void onDrawFrame() {
        // 用颜色刷新
        commonFboRender.onDraw(textureId);
    }
    public void addFilter(BaseGPUImageFilter filter) {
        this.commonFboRender.setFilter(filter);
    }

    public void addTexture(BaseTexture baseTexture) {
        if (baseTexture != null){
            commonFboRender.addTexture(baseTexture);
        }
    }

    public void removeTexture(String key) {
        commonFboRender.removeTexture(key);
    }

    public void updateTexture(String id, float left, float top, float scale) {
        commonFboRender.updateTexture(id, left, top, scale);
    }

}
