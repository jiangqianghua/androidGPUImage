package com.jqh.gpuimagelib.filter;

import android.content.Context;

import com.jqh.gpuimagelib.R;
import com.jqh.gpuimagelib.opengl.ShaderUtils;

public class BaseGPUImageFilter {
    protected Context context;

    private boolean isMedia = false;

    public boolean isMedia() {
        return isMedia;
    }

    public int program ;

    public void setProgram(int program) {
        this.program = program;
    }

    public void setIsMedia(boolean is) {
        isMedia = is;
    }

    public void init(){

    }

    public void update(){

    }


    public int getProgram() {
        return program;
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
