package com.jqh.gpuimagelib.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;

import com.jqh.gpuimagelib.opengl.GLSurfaceView;
import com.jqh.gpuimagelib.opengl.ShaderUtils;
import com.jqh.gpuimagelib.render.filter.BaseGPUImageFilter;



public class GPUImageRender implements GLSurfaceView.GLRender {

    BaseGPUImageFilter filter;
    private Context context;


    private int textureId; // 纹理id
    private Bitmap bitmap;


    public GPUImageRender(Context context) {
        this.context = context;
        filter = new BaseGPUImageFilter(context);
    }

    @Override
    public void onSurfaceCreate() {
        textureId = ShaderUtils.loadBitmapTexture(bitmap);
        initRender();
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        GLES20.glViewport(0,0, width, height);
    }

    @Override
    public void onDrawFrame() {

        ShaderUtils.clearScreenDefault();
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);

        initRender();
        GLES20.glUseProgram(filter.getProgram());

        filter.update();

        ShaderUtils.renderTexture(filter.getvPosition(), filter.getfPosition(), filter.getVertexBuffer(), filter.getFragmentBuffer());
        //  解除绑定纹理
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);


    }

    private void initRender(){
        filter.init();
    }

    public void setImage(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public void setFilter(BaseGPUImageFilter filter) {
        this.filter = filter;
    }
}
