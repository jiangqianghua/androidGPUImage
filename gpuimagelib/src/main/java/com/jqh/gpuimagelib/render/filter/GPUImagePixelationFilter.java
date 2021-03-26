package com.jqh.gpuimagelib.render.filter;

import android.content.Context;
import android.opengl.GLES20;

public class GPUImagePixelationFilter extends BaseGPUImageFilter {
    public static final String PIXELATION_FRAGMENT_SHADER = "" +
            "precision highp float;\n" +

            "varying vec2 ft_Position;\n" +

            "uniform float imageWidthFactor;\n" +
            "uniform float imageHeightFactor;\n" +
            "uniform sampler2D inputImageTexture;\n" +
            "uniform float pixel;\n" +

            "void main()\n" +
            "{\n" +
            "  vec2 uv  = ft_Position.xy;\n" +
            "  float dx = pixel * imageWidthFactor;\n" +
            "  float dy = pixel * imageHeightFactor;\n" +
            "  vec2 coord = vec2(dx * floor(uv.x / dx), dy * floor(uv.y / dy));\n" +
            "  vec3 tc = texture2D(inputImageTexture, coord).xyz;\n" +
            "  gl_FragColor = vec4(tc, 1.0);\n" +
            "}";

    private int imageWidthFactorLocation;
    private int imageHeightFactorLocation;
    private float pixel;
    private int pixelLocation;

    public GPUImagePixelationFilter(Context context) {
        super(context);
        pixel = 10.0f;
    }

    @Override
    public void init() {
        if (inited) return;
        super.init();
        imageWidthFactorLocation = GLES20.glGetUniformLocation(getProgram(), "imageWidthFactor");
        imageHeightFactorLocation = GLES20.glGetUniformLocation(getProgram(), "imageHeightFactor");
        pixelLocation = GLES20.glGetUniformLocation(getProgram(), "pixel");
    }

    @Override
    protected String getFragmentSource() {
        return PIXELATION_FRAGMENT_SHADER;
    }

    @Override
    public void update() {
        if (!isNeedUpdate) return;
        super.update();
        setFloat(pixelLocation, this.pixel);
    }

    @Override
    public void onOutputSizeChanged(final int width, final int height) {
        if (!isNeedChangeWH) return;
        super.onOutputSizeChanged(width, height);
        setFloat(imageWidthFactorLocation, 1.0f / width);
        setFloat(imageHeightFactorLocation, 1.0f / height);
    }
}
