package com.jqh.gpuimagelib.render.filter;

import android.content.Context;
import android.opengl.GLES20;

/**
 * saturation: The degree of saturation or desaturation to apply to the image (0.0 - 2.0, with 1.0 as the default)
 */
public class GPUImageSaturationFilter extends BaseGPUImageFilter {

    public static final String SATURATION_FRAGMENT_SHADER = "" +
            " varying highp vec2 ft_Position;\n" +
            " \n" +
            " uniform sampler2D inputImageTexture;\n" +
            " uniform lowp float saturation;\n" +
            " \n" +
            " // Values from \"Graphics Shaders: Theory and Practice\" by Bailey and Cunningham\n" +
            " const mediump vec3 luminanceWeighting = vec3(0.2125, 0.7154, 0.0721);\n" +
            " \n" +
            " void main()\n" +
            " {\n" +
            "    lowp vec4 textureColor = texture2D(inputImageTexture, ft_Position);\n" +
            "    lowp float luminance = dot(textureColor.rgb, luminanceWeighting);\n" +
            "    lowp vec3 greyScaleColor = vec3(luminance);\n" +
            "    \n" +
            "    gl_FragColor = vec4(mix(greyScaleColor, textureColor.rgb, saturation), textureColor.w);\n" +
            "     \n" +
            " }";

    private int saturationLocation;
    private float saturation;

    public GPUImageSaturationFilter(Context context) {
        this(context, 1.0f);
    }

    public GPUImageSaturationFilter(Context context, final float saturation) {
        super(context);
        this.saturation = saturation;
    }

    @Override
    public void init() {
        if (inited) return;
        super.init();
        saturationLocation = GLES20.glGetUniformLocation(getProgram(), "saturation");
    }

    @Override
    protected String getFragmentSource() {
        return SATURATION_FRAGMENT_SHADER;
    }

    @Override
    public void update() {
        if (!isNeedUpdate) return;
        super.update();
        setFloat(saturationLocation, this.saturation);
    }
}
