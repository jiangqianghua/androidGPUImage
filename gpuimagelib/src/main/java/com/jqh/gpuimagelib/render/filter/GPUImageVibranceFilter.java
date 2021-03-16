package com.jqh.gpuimagelib.render.filter;

import android.content.Context;
import android.opengl.GLES20;

public class GPUImageVibranceFilter extends BaseGPUImageFilter {
    public static final String VIBRANCE_FRAGMENT_SHADER = "" +
            "varying highp vec2 ft_Position;\n" +
            "\n" +
            "uniform sampler2D inputImageTexture;\n" +
            "uniform lowp float vibrance;\n" +
            "\n" +
            "void main() {\n" +
            "    lowp vec4 color = texture2D(inputImageTexture, ft_Position);\n" +
            "    lowp float average = (color.r + color.g + color.b) / 3.0;\n" +
            "    lowp float mx = max(color.r, max(color.g, color.b));\n" +
            "    lowp float amt = (mx - average) * (-vibrance * 3.0);\n" +
            "    color.rgb = mix(color.rgb, vec3(mx), amt);\n" +
            "    gl_FragColor = color;\n" +
            "}";

    private int vibranceLocation;
    private float vibrance;
    public GPUImageVibranceFilter(Context context) {
        this(context, 10f);
    }
    public GPUImageVibranceFilter(Context context, float vibrance) {
        super(context);
        this.vibrance = vibrance;
    }

    @Override
    public void init() {
        if (inited) return;
        super.init();
        vibranceLocation = GLES20.glGetUniformLocation(getProgram(), "vibrance");
    }


    @Override
    public void update() {
        if (!isNeedUpdate) return;
        super.update();
        setFloat(vibranceLocation, vibrance);
    }

    @Override
    public String getFragmentSource() {
        return VIBRANCE_FRAGMENT_SHADER;
    }

    public void setVibrance(final float vibrance) {
        this.vibrance = vibrance;
       isNeedUpdate = true;
    }
}
