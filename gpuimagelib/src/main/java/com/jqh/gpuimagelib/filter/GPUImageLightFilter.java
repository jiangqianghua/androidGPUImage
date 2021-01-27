package com.jqh.gpuimagelib.filter;

import android.content.Context;

import com.jqh.gpuimagelib.R;
import com.jqh.gpuimagelib.opengl.ShaderUtils;

public class GPUImageLightFilter extends BaseGPUImageFilter {

    public GPUImageLightFilter(Context context) {
        super(context);
    }

    @Override
    public String getVertexSource() {
        return super.getVertexSource();
    }

    @Override
    public String getFragmentSource() {
        return ShaderUtils.getRawResource(context, R.raw.fragment_shader3);
    }
}
