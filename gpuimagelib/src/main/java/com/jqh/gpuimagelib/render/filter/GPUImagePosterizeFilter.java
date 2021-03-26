package com.jqh.gpuimagelib.render.filter;

import android.content.Context;
import android.opengl.GLES20;

/**
 * Reduces the color range of the image. <br>
 * <br>
 * colorLevels: ranges from 1 to 256, with a default of 10
 */
public class GPUImagePosterizeFilter extends BaseGPUImageFilter {
    public static final String POSTERIZE_FRAGMENT_SHADER = "" +
            "varying highp vec2 ft_Position;\n" +
            "\n" +
            "uniform sampler2D inputImageTexture;\n" +
            "uniform highp float colorLevels;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "   highp vec4 textureColor = texture2D(inputImageTexture, ft_Position);\n" +
            "   \n" +
            "   gl_FragColor = floor((textureColor * colorLevels) + vec4(0.5)) / colorLevels;\n" +
            "}";

    private int glUniformColorLevels;
    private int colorLevels;

    public GPUImagePosterizeFilter(Context context) {
        this(context, 10);
    }

    public GPUImagePosterizeFilter(Context context, final int colorLevels) {
        super(context);
        this.colorLevels = colorLevels;
    }


    @Override
    public void init() {
        if (inited) return;
        super.init();
        glUniformColorLevels = GLES20.glGetUniformLocation(getProgram(), "colorLevels");
    }

    @Override
    protected String getFragmentSource() {
        return POSTERIZE_FRAGMENT_SHADER;
    }

    @Override
    public void update() {
        if (!isNeedUpdate) return;
        super.update();
        setFloat(glUniformColorLevels, colorLevels);
    }
}
