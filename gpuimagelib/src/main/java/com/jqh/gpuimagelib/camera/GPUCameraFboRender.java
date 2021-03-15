package com.jqh.gpuimagelib.camera;

import android.content.Context;
import android.opengl.GLES20;

import com.jqh.gpuimagelib.opengl.ShaderUtils;
import com.jqh.gpuimagelib.render.CommonFboRender;
import com.jqh.gpuimagelib.render.filter.BaseGPUImageFilter;
import com.jqh.gpuimagelib.render.textrue.BaseTexture;


public class GPUCameraFboRender {
    private Context context;
    private CommonFboRender commonFboRender;

    public GPUCameraFboRender(Context context) {
        this.context = context;
        commonFboRender = new CommonFboRender();
        commonFboRender.init(context);

    }

    public void onCreate(){
        // 设置一下两个可以让水印背景透明
        GLES20.glEnable (GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        commonFboRender.onCreate();

    }

    public void onChange(int width, int height) {
        GLES20.glViewport(0,0, width, height);
        commonFboRender.setWH(width, height);
    }

    public void onDraw(int textureId){
        ShaderUtils.clearScreenDefault();

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
