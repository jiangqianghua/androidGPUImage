package com.jqh.gpuimagelib.render.filter;

import android.content.Context;

public class GPUImageLuminanceFilter extends BaseGPUImageFilter {
    public static final String LUMINANCE_FRAGMENT_SHADER = "" +
            "precision highp float;\n" +
            "\n" +
            "varying vec2 ft_Position;\n" +
            "\n" +
            "uniform sampler2D inputImageTexture;\n" +
            "\n" +
            "// Values from \"Graphics Shaders: Theory and Practice\" by Bailey and Cunningham\n" +
            "const highp vec3 W = vec3(0.2125, 0.7154, 0.0721);\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "    lowp vec4 textureColor = texture2D(inputImageTexture, ft_Position);\n" +
            "    float luminance = dot(textureColor.rgb, W);\n" +
            "    \n" +
            "    gl_FragColor = vec4(vec3(luminance), textureColor.a);\n" +
            "}";


    public GPUImageLuminanceFilter(Context context) {
        super(context);
    }

    @Override
    protected String getFragmentSource() {
        return LUMINANCE_FRAGMENT_SHADER;
    }
}
