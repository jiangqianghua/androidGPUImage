package com.jqh.gpuimagelib.render.filter;

import android.content.Context;
import android.opengl.GLES20;

public class GPUImageLuminanceThresholdFilter extends BaseGPUImageFilter {

    public static final String LUMINANCE_THRESHOLD_FRAGMENT_SHADER = "" +
            "varying highp vec2 ft_Position;\n" +
            "\n" +
            "uniform sampler2D inputImageTexture;\n" +
            "uniform highp float threshold;\n" +
            "\n" +
            "const highp vec3 W = vec3(0.2125, 0.7154, 0.0721);\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "    highp vec4 textureColor = texture2D(inputImageTexture, ft_Position);\n" +
            "    highp float luminance = dot(textureColor.rgb, W);\n" +
            "    highp float thresholdResult = step(threshold, luminance);\n" +
            "    \n" +
            "    gl_FragColor = vec4(vec3(thresholdResult), textureColor.w);\n" +
            "}";
    private int uniformThresholdLocation;
    private float threshold;

    public GPUImageLuminanceThresholdFilter(Context context) {
        this(context,0.5f);
    }

    public GPUImageLuminanceThresholdFilter(Context context, float threshold) {
        super(context);
        this.threshold = threshold;
    }

    @Override
    protected String getFragmentSource() {
        return LUMINANCE_THRESHOLD_FRAGMENT_SHADER;
    }

    @Override
    public void init() {
        if (inited) return;
        super.init();
        uniformThresholdLocation = GLES20.glGetUniformLocation(getProgram(), "threshold");
    }

    @Override
    public void update() {
        if (!isNeedUpdate) return;
        super.update();
        setFloat(uniformThresholdLocation, threshold);
    }
}
