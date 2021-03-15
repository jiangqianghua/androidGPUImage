package com.jqh.gpuimagelib.render.filter;

import android.content.Context;

import com.jqh.gpuimagelib.R;
import com.jqh.gpuimagelib.opengl.ShaderUtils;

public class GPUImageGreyFilter extends BaseGPUImageFilter {
    public GPUImageGreyFilter(Context context) {
        super(context);
    }

    @Override
    protected String getFragmentSource() {
        return ShaderUtils.getRawResource(context, R.raw.fragment_shader2);
    }
}
