package com.jqh.gpuimagelib.camera;

import android.content.Context;
import android.opengl.GLES20;

import com.jqh.gpuimagelib.filter.BaseGPUImageFilter;
import com.jqh.gpuimagelib.opengl.ShaderUtils;
import com.jqh.gpuimagelib.render.CommonFboRender;


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
    }

    public void onDraw(int textureId){
        ShaderUtils.clearScreenDefault();

        commonFboRender.onDraw(textureId);

    }

    public void addFilter(BaseGPUImageFilter filter) {

    }
}
