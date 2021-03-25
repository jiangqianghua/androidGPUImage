package com.jqh.gpuimagelib.render.filter;

import android.content.Context;

public class GPUImageColorInvertFilter extends BaseGPUImageFilter {
    public static final String COLOR_INVERT_FRAGMENT_SHADER = "" +
            "varying highp vec2 ft_Position;\n" +
            "\n" +
            "uniform sampler2D inputImageTexture;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "    lowp vec4 textureColor = texture2D(inputImageTexture, ft_Position);\n" +
            "    \n" +
            "    gl_FragColor = vec4((1.0 - textureColor.rgb), textureColor.w);\n" +
            "}";

    public GPUImageColorInvertFilter(Context context) {
        super(context);
    }

    @Override
    protected String getFragmentSource() {
        return COLOR_INVERT_FRAGMENT_SHADER;
    }
}
