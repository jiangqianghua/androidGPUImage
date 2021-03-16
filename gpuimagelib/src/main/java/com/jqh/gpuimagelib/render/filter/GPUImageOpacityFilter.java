package com.jqh.gpuimagelib.render.filter;

import android.content.Context;
import android.opengl.GLES20;

import com.jqh.gpuimagelib.R;
import com.jqh.gpuimagelib.opengl.ShaderUtils;
import com.jqh.gpuimagelib.utils.LogUtils;

public class GPUImageOpacityFilter extends BaseGPUImageFilter {
    private int opacityLocation;
    private float opacity;
    public GPUImageOpacityFilter(Context context, float opacity) {
        super(context);
        this.opacity = opacity;
    }

    @Override
    public void init() {
        if (inited) return;
        super.init();
        opacityLocation = GLES20.glGetUniformLocation(getProgram(), "opacity");
        LogUtils.logd("init Opacity opacityLocation = " + opacityLocation);
    }

    @Override
    protected String getFragmentSource() {
        return ShaderUtils.getRawResource(context, R.raw.fragement_opacity_shader);
    }

    @Override
    public void update() {
        if (!isNeedUpdate) return;
        super.update();
        GLES20.glUniform1f(opacityLocation, opacity);
    }

    public void setOpacity(float opacity) {
        this.opacity = opacity;
        setNeedUpdate();
    }
}
