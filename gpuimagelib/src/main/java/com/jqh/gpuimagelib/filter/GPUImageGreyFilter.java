package com.jqh.gpuimagelib.filter;

import android.content.Context;

import com.jqh.gpuimagelib.R;
import com.jqh.gpuimagelib.opengl.ShaderUtils;

public class GPUImageGreyFilter extends BaseGPUImageFilter {

    public GPUImageGreyFilter(Context context) {
        super(context);
    }

    @Override
    public String getVertexSource() {
        return super.getVertexSource();
    }

    @Override
    public String getFragmentSource() {
        return ShaderUtils.getRawResource(context, R.raw.fragment_shader2);
    }
}
