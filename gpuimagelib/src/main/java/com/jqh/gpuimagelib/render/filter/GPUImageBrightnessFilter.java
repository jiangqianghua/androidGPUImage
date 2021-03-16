package com.jqh.gpuimagelib.render.filter;

import android.content.Context;
import android.opengl.GLES20;
/**
 * brightness value ranges from -1.0 to 1.0, with 0.0 as the normal level
 */
public class GPUImageBrightnessFilter extends BaseGPUImageFilter {

    public static final String BRIGHTNESS_FRAGMENT_SHADER = "" +
            "varying highp vec2 ft_Position;\n" +
            " \n" +
            " uniform sampler2D inputImageTexture;\n" +
            " uniform lowp float brightness;\n" +
            " \n" +
            " void main()\n" +
            " {\n" +
            "     lowp vec4 textureColor = texture2D(inputImageTexture, ft_Position);\n" +
            "     \n" +
            "     gl_FragColor = vec4((textureColor.rgb + vec3(brightness)), textureColor.w);\n" +
            " }";

    private int brightnessLocation;
    private float brightness;

    public GPUImageBrightnessFilter(Context context) {
        this(context, 0.0f);
    }

    public GPUImageBrightnessFilter(Context context, final float brightness) {
        super(context);
        this.brightness = brightness;
    }

    @Override
    public void init() {
        if (inited) return;
        super.init();
        brightnessLocation = GLES20.glGetUniformLocation(getProgram(), "brightness");
    }


    @Override
    public void update() {
        if (!isNeedUpdate) return;
        super.update();
        setFloat(brightnessLocation, this.brightness);
    }

    @Override
    public String getFragmentSource() {
        return BRIGHTNESS_FRAGMENT_SHADER;
    }

}
