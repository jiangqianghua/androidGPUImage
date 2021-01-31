package com.jqh.gpuimagelib.filter;

import android.content.Context;
import android.opengl.GLES20;

import com.jqh.gpuimagelib.R;
import com.jqh.gpuimagelib.opengl.ShaderUtils;

public class GPUImageOpacityFilter extends BaseGPUImageFilter {

    private int opacityLocation;
    private float opacity;
    public GPUImageOpacityFilter(Context context, float opacity) {
        super(context);
        this.opacity = opacity;
    }


    @Override
    public void init() {
        super.init();
        opacityLocation = GLES20.glGetUniformLocation(getProgram(), "opacity");
    }

    @Override
    public void update() {
        super.update();
        GLES20.glUniform1f(opacityLocation, opacity);
    }

    @Override
    public String getVertexSource() {
        return super.getVertexSource();
    }

    @Override
    public String getFragmentSource() {
        return ShaderUtils.getRawResource(context, R.raw.fragement_opacity_shader);
    }

    public void setOpacity(float opacity) {
        this.opacity = opacity;

    }
}
