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
//        vertexData = new float[]{
//                -0.5f, -0.5f,
//                0.5f, -0.5f,
//                -0.5f, 0.5f,
//                0.5f, 0.5f
//        };
//
//        fragmentData = new float[] {
//                0f, 0.5f,
//                0.5f, 0.5f,
//                0f, 0f,
//                0.5f, 0f
//
//        };
        this.opacity = opacity;
//        vertexBuffer = ShaderUtils.allocateBuffer(vertexData);
//        fragmentBuffer = ShaderUtils.allocateBuffer(fragmentData);
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
        return ShaderUtils.getRawResource(context, isMedia() ? R.raw.fragement_opacity_shader_camera : R.raw.fragement_opacity_shader);
    }

    public void setOpacity(float opacity) {
        this.opacity = opacity;

    }
}
