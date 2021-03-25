package com.jqh.gpuimagelib.render.filter;

import android.content.Context;
import android.opengl.GLES20;

public class GPUImageContrastFilter extends BaseGPUImageFilter {
    public static final String CONTRAST_FRAGMENT_SHADER = "" +
            "varying highp vec2 ft_Position;\n" +
            " \n" +
            " uniform sampler2D inputImageTexture;\n" +
            " uniform lowp float contrast;\n" +
            " \n" +
            " void main()\n" +
            " {\n" +
            "     lowp vec4 textureColor = texture2D(inputImageTexture, ft_Position);\n" +
            "     \n" +
            "     gl_FragColor = vec4(((textureColor.rgb - vec3(0.5)) * contrast + vec3(0.5)), textureColor.w);\n" +
            " }";

    private int contrastLocation;
    private float contrast;
    public GPUImageContrastFilter(Context context) {
        this(context,1.2f);
    }

    public GPUImageContrastFilter(Context context, float contrast) {
        super(context);
        this.contrast = contrast;
    }

    @Override
    protected String getFragmentSource() {
        return CONTRAST_FRAGMENT_SHADER;
    }

    @Override
    public void init() {
        if (inited) return;
        super.init();
        contrastLocation = GLES20.glGetUniformLocation(getProgram(), "contrast");
    }

    @Override
    public void update() {
        if (!isNeedUpdate) return;
        super.update();
        setFloat(contrastLocation, this.contrast);
    }

    public void setContrast(final float contrast) {
        this.contrast = contrast;
        isNeedUpdate = true;
    }


}
