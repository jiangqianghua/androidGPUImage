package com.jqh.gpuimagelib.filter;

import android.content.Context;

import com.jqh.gpuimagelib.R;
import com.jqh.gpuimagelib.image.GPUImageRender;
import com.jqh.gpuimagelib.opengl.ShaderUtils;

import java.lang.ref.WeakReference;

public class BaseGPUImageFilter {
    protected Context context;

    private boolean isMedia = false;


    private void setIsMedia(boolean is) {
        isMedia = is;
    }

    protected WeakReference<GPUImageRender> gpuImageRenderRef;

    public void init(){

    }

    public void update(){

    }

    public void setGPUImageRender(GPUImageRender render) {
        this.gpuImageRenderRef = new WeakReference<GPUImageRender>(render);
    }

    public int getProgram() {
        return this.gpuImageRenderRef.get().program;
    }

    public BaseGPUImageFilter(Context context) {
        this.context = context;
    }

    public String getVertexSource(){
        return  ShaderUtils.getRawResource(context, isMedia ? R.raw.vertex_shader_camera : R.raw.vertex_shader );
    }

    public String getFragmentSource(){
        return ShaderUtils.getRawResource(context, isMedia ? R.raw.fragment_shader_camera : R.raw.fragment_shader);
    }
}
