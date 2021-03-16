package com.jqh.gpuimagelib.render.filter;

import android.content.Context;

public class GPUImageCGAColorspaceFilter extends BaseGPUImageFilter {
    public static final String CGACOLORSPACE_FRAGMENT_SHADER = "" +
            "varying highp vec2 ft_Position;\n" +
            "\n" +
            "uniform sampler2D inputImageTexture;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "highp vec2 sampleDivisor = vec2(1.0 / 200.0, 1.0 / 320.0);\n" +
            "//highp vec4 colorDivisor = vec4(colorDepth);\n" +
            "\n" +
            "highp vec2 samplePos = ft_Position - mod(ft_Position, sampleDivisor);\n" +
            "highp vec4 color = texture2D(inputImageTexture, samplePos );\n" +
            "\n" +
            "//gl_FragColor = texture2D(inputImageTexture, samplePos );\n" +
            "mediump vec4 colorCyan = vec4(85.0 / 255.0, 1.0, 1.0, 1.0);\n" +
            "mediump vec4 colorMagenta = vec4(1.0, 85.0 / 255.0, 1.0, 1.0);\n" +
            "mediump vec4 colorWhite = vec4(1.0, 1.0, 1.0, 1.0);\n" +
            "mediump vec4 colorBlack = vec4(0.0, 0.0, 0.0, 1.0);\n" +
            "\n" +
            "mediump vec4 endColor;\n" +
            "highp float blackDistance = distance(color, colorBlack);\n" +
            "highp float whiteDistance = distance(color, colorWhite);\n" +
            "highp float magentaDistance = distance(color, colorMagenta);\n" +
            "highp float cyanDistance = distance(color, colorCyan);\n" +
            "\n" +
            "mediump vec4 finalColor;\n" +
            "\n" +
            "highp float colorDistance = min(magentaDistance, cyanDistance);\n" +
            "colorDistance = min(colorDistance, whiteDistance);\n" +
            "colorDistance = min(colorDistance, blackDistance); \n" +
            "\n" +
            "if (colorDistance == blackDistance) {\n" +
            "finalColor = colorBlack;\n" +
            "} else if (colorDistance == whiteDistance) {\n" +
            "finalColor = colorWhite;\n" +
            "} else if (colorDistance == cyanDistance) {\n" +
            "finalColor = colorCyan;\n" +
            "} else {\n" +
            "finalColor = colorMagenta;\n" +
            "}\n" +
            "\n" +
            "gl_FragColor = finalColor;\n" +
            "}\n";

    public GPUImageCGAColorspaceFilter(Context context) {

        super(context);
    }

    @Override
    public String getFragmentSource() {
        return CGACOLORSPACE_FRAGMENT_SHADER;
    }
}
