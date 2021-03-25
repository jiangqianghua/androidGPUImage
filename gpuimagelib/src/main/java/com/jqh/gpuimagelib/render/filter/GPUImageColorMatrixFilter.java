package com.jqh.gpuimagelib.render.filter;

import android.content.Context;
import android.opengl.GLES20;

import com.jqh.gpuimagelib.utils.LogUtils;

public class GPUImageColorMatrixFilter extends BaseGPUImageFilter {
    public static final String COLOR_MATRIX_FRAGMENT_SHADER = "" +
            "varying highp vec2 ft_Position;\n" +
            "\n" +
            "uniform sampler2D inputImageTexture;\n" +
            "\n" +
            "uniform lowp mat4 colorMatrix;\n" +
            "uniform lowp float intensity;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "    lowp vec4 textureColor = texture2D(inputImageTexture, ft_Position);\n" +
            "    lowp vec4 outputColor = textureColor * colorMatrix;\n" +
            "    \n" +
            "    gl_FragColor = (intensity * outputColor) + ((1.0 - intensity) * textureColor);\n" +
            "}";
    private float intensity;
    private float[] colorMatrix;
    private int colorMatrixLocation;
    private int intensityLocation;

    public GPUImageColorMatrixFilter(Context context) {
        this(context, 0.0f, new float[]{
                1.0f, 0.0f, 0.0f, 0.0f,
                0.0f, 1.0f, 0.0f, 0.0f,
                0.0f, 0.0f, 1.0f, 0.0f,
                0.0f, 0.0f, 0.0f, 1.0f
        });
    }
    public GPUImageColorMatrixFilter(Context context, final float intensity, final float[] colorMatrix) {
        super(context);
        this.intensity = intensity;
        this.colorMatrix = colorMatrix;
    }

    @Override
    protected String getFragmentSource() {
        return COLOR_MATRIX_FRAGMENT_SHADER;
    }

    @Override
    public void init() {
        if (inited) return;
        super.init();
        colorMatrixLocation = GLES20.glGetUniformLocation(getProgram(), "colorMatrix");
        intensityLocation = GLES20.glGetUniformLocation(getProgram(), "intensity");
    }

    @Override
    public void update() {
        if (!isNeedUpdate) return;
        super.update();
        setFloat(intensityLocation, intensity);
        setUniformMatrix4f(colorMatrixLocation, colorMatrix);
    }

    public void setIntensity(final float intensity) {
        this.intensity = intensity;
        isNeedUpdate = true;
    }

    public void setColorMatrix(final float[] colorMatrix) {
        this.colorMatrix = colorMatrix;
        isNeedUpdate = true;
    }

}
