package com.jqh.gpuimagelib.render.filter;

import android.content.Context;
import android.opengl.GLES20;

public class GPUImageFalseColorFilter extends BaseGPUImageFilter {

    public static final String FALSECOLOR_FRAGMENT_SHADER = "" +
            "precision lowp float;\n" +
            "\n" +
            "varying highp vec2 ft_Position;\n" +
            "\n" +
            "uniform sampler2D inputImageTexture;\n" +
            "uniform float intensity;\n" +
            "uniform vec3 firstColor;\n" +
            "uniform vec3 secondColor;\n" +
            "\n" +
            "const mediump vec3 luminanceWeighting = vec3(0.2125, 0.7154, 0.0721);\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "lowp vec4 textureColor = texture2D(inputImageTexture, ft_Position);\n" +
            "float luminance = dot(textureColor.rgb, luminanceWeighting);\n" +
            "\n" +
            "gl_FragColor = vec4( mix(firstColor.rgb, secondColor.rgb, luminance), textureColor.a);\n" +
            "}\n";

    private float[] firstColor;
    private int firstColorLocation;
    private float[] secondColor;
    private int secondColorLocation;

    public GPUImageFalseColorFilter(Context context) {
        this(context,0f, 0f, 0.5f, 1f, 0f, 0f);
    }

    public GPUImageFalseColorFilter(Context context,float firstRed, float firstGreen, float firstBlue, float secondRed, float secondGreen, float secondBlue) {
        this(context, new float[]{firstRed, firstGreen, firstBlue}, new float[]{secondRed, secondGreen, secondBlue});
    }

    public GPUImageFalseColorFilter(Context context,float[] firstColor, float[] secondColor) {
        super(context);
        this.firstColor = firstColor;
        this.secondColor = secondColor;
    }

    @Override
    protected String getFragmentSource() {
        return FALSECOLOR_FRAGMENT_SHADER;
    }

    @Override
    public void init() {
        if (inited) return;
        super.init();
        firstColorLocation = GLES20.glGetUniformLocation(getProgram(), "firstColor");
        secondColorLocation = GLES20.glGetUniformLocation(getProgram(), "secondColor");
    }

    @Override
    public void update() {
        if (!isNeedUpdate) return;
        super.update();
        setFloatVec3(firstColorLocation, firstColor);
        setFloatVec3(secondColorLocation, secondColor);
    }
}
