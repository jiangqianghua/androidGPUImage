package com.jqh.gpuimagelib.render.filter;

import android.content.Context;
import android.opengl.GLES20;

public class GPUImageMonochromeFilter extends BaseGPUImageFilter {

    public static final String MONOCHROME_FRAGMENT_SHADER = "" +
            " precision lowp float;\n" +
            "  \n" +
            "  varying highp vec2 ft_Position;\n" +
            "  \n" +
            "  uniform sampler2D inputImageTexture;\n" +
            "  uniform float intensity;\n" +
            "  uniform vec3 filterColor;\n" +
            "  \n" +
            "  const mediump vec3 luminanceWeighting = vec3(0.2125, 0.7154, 0.0721);\n" +
            "  \n" +
            "  void main()\n" +
            "  {\n" +
            " 	//desat, then apply overlay blend\n" +
            " 	lowp vec4 textureColor = texture2D(inputImageTexture, ft_Position);\n" +
            " 	float luminance = dot(textureColor.rgb, luminanceWeighting);\n" +
            " 	\n" +
            " 	lowp vec4 desat = vec4(vec3(luminance), 1.0);\n" +
            " 	\n" +
            " 	//overlay\n" +
            " 	lowp vec4 outputColor = vec4(\n" +
            "                                  (desat.r < 0.5 ? (2.0 * desat.r * filterColor.r) : (1.0 - 2.0 * (1.0 - desat.r) * (1.0 - filterColor.r))),\n" +
            "                                  (desat.g < 0.5 ? (2.0 * desat.g * filterColor.g) : (1.0 - 2.0 * (1.0 - desat.g) * (1.0 - filterColor.g))),\n" +
            "                                  (desat.b < 0.5 ? (2.0 * desat.b * filterColor.b) : (1.0 - 2.0 * (1.0 - desat.b) * (1.0 - filterColor.b))),\n" +
            "                                  1.0\n" +
            "                                  );\n" +
            " 	\n" +
            " 	//which is better, or are they equal?\n" +
            " 	gl_FragColor = vec4( mix(textureColor.rgb, outputColor.rgb, intensity), textureColor.a);\n" +
            "  }";

    private int intensityLocation;
    private float intensity;
    private int filterColorLocation;
    private float[] color;

    public GPUImageMonochromeFilter(Context context) {
        this(context,1.0f, new float[]{0.6f, 0.45f, 0.3f, 1.0f});
    }

    public GPUImageMonochromeFilter(Context context, final float intensity, final float[] color) {
        super(context);
        this.intensity = intensity;
        this.color = color;
    }
    @Override
    protected String getFragmentSource() {
        return MONOCHROME_FRAGMENT_SHADER;
    }

    @Override
    public void init() {
        if (inited) return;
        super.init();
        intensityLocation = GLES20.glGetUniformLocation(getProgram(), "intensity");
        filterColorLocation = GLES20.glGetUniformLocation(getProgram(), "filterColor");
    }

    @Override
    public void update() {
        if (!isNeedUpdate) return;
        super.update();
        setFloat(intensityLocation, this.intensity);
        setFloatVec3(filterColorLocation, new float[]{this.color[0], this.color[1], this.color[2]});
    }
}
