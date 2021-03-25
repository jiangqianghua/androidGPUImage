package com.jqh.gpuimagelib.render.filter;

import android.content.Context;
import android.opengl.GLES20;

public class GPUImageExposureFilter extends BaseGPUImageFilter {

    public static final String EXPOSURE_FRAGMENT_SHADER = "" +
            " varying highp vec2 ft_Position;\n" +
            " \n" +
            " uniform sampler2D inputImageTexture;\n" +
            " uniform highp float exposure;\n" +
            " \n" +
            " void main()\n" +
            " {\n" +
            "     highp vec4 textureColor = texture2D(inputImageTexture, ft_Position);\n" +
            "     \n" +
            "     gl_FragColor = vec4(textureColor.rgb * pow(2.0, exposure), textureColor.w);\n" +
            " } ";

    private int exposureLocation;
    private float exposure;
    public GPUImageExposureFilter(Context context) {
        this(context,1.0f);
    }

    public GPUImageExposureFilter(Context context, final float exposure) {
        super(context);
        this.exposure = exposure;
    }

    @Override
    protected String getFragmentSource() {
        return EXPOSURE_FRAGMENT_SHADER;
    }

    @Override
    public void init() {
        if (inited) return;
        super.init();
        exposureLocation = GLES20.glGetUniformLocation(getProgram(), "exposure");
    }

    @Override
    public void update() {
        if (!isNeedUpdate) return;
        super.update();
        setFloat(exposureLocation, this.exposure);
    }
}
