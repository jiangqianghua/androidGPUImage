package com.jqh.gpuimagelib.filter;

import android.content.Context;

import com.jqh.gpuimagelib.R;
import com.jqh.gpuimagelib.opengl.ShaderUtils;

public abstract class BaseGPUImageFilter {
    protected Context context;

    public BaseGPUImageFilter(Context context) {
        this.context = context;
    }

    public String getVertexSource(){
        return  ShaderUtils.getRawResource(context, R.raw.vertex_shader);
    }

    public String getFragmentSource(){
        return ShaderUtils.getRawResource(context, R.raw.fragment_shader);
    }
}
