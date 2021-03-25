package com.jqh.gpuimagelib.render.filter;

import android.content.Context;
import android.opengl.GLES20;

public class GPUImageGammaFilter extends BaseGPUImageFilter {
    public static final String GAMMA_FRAGMENT_SHADER = "" +
            "varying highp vec2 ft_Position;\n" +
            " \n" +
            " uniform sampler2D inputImageTexture;\n" +
            " uniform lowp float gamma;\n" +
            " \n" +
            " void main()\n" +
            " {\n" +
            "     lowp vec4 textureColor = texture2D(inputImageTexture, ft_Position);\n" +
            "     \n" +
            "     gl_FragColor = vec4(pow(textureColor.rgb, vec3(gamma)), textureColor.w);\n" +
            " }";

    private int gammaLocation;
    private float gamma;

    public GPUImageGammaFilter(Context context) {
        this(context,1.2f);
    }

    public GPUImageGammaFilter(Context context,final float gamma) {
        super(context);
        this.gamma = gamma;
    }
    @Override
    protected String getFragmentSource() {
        return GAMMA_FRAGMENT_SHADER;
    }

    @Override
    public void init() {
        if (inited) return;
        super.init();
        gammaLocation = GLES20.glGetUniformLocation(getProgram(), "gamma");
    }

    @Override
    public void update() {
        if (!isNeedUpdate) return;
        super.update();
        setFloat(gammaLocation, this.gamma);
    }
}
