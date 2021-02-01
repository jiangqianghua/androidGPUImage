package com.jqh.gpuimagelib.filter;

import android.content.Context;
import android.graphics.PointF;
import android.opengl.GLES20;

public class GPUImageZoomBlurFilter extends BaseGPUImageFilter {

    public static final String ZOOM_BLUR_FRAGMENT_SHADER = "" +
            "varying highp vec2 ft_Position;\n" +
            "\n" +
            "uniform sampler2D sTexture;\n" +
            "\n" +
            "uniform highp vec2 blurCenter;\n" +
            "uniform highp float blurSize;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "    // TODO: Do a more intelligent scaling based on resolution here\n" +
            "    highp vec2 samplingOffset = 1.0/100.0 * (blurCenter - ft_Position) * blurSize;\n" +
            "    \n" +
            "    lowp vec4 fragmentColor = texture2D(sTexture, ft_Position) * 0.18;\n" +
            "    fragmentColor += texture2D(sTexture, ft_Position + samplingOffset) * 0.15;\n" +
            "    fragmentColor += texture2D(sTexture, ft_Position + (2.0 * samplingOffset)) *  0.12;\n" +
            "    fragmentColor += texture2D(sTexture, ft_Position + (3.0 * samplingOffset)) * 0.09;\n" +
            "    fragmentColor += texture2D(sTexture, ft_Position + (4.0 * samplingOffset)) * 0.05;\n" +
            "    fragmentColor += texture2D(sTexture, ft_Position - samplingOffset) * 0.15;\n" +
            "    fragmentColor += texture2D(sTexture, ft_Position - (2.0 * samplingOffset)) *  0.12;\n" +
            "    fragmentColor += texture2D(sTexture, ft_Position - (3.0 * samplingOffset)) * 0.09;\n" +
            "    fragmentColor += texture2D(sTexture, ft_Position - (4.0 * samplingOffset)) * 0.05;\n" +
            "    \n" +
            "    gl_FragColor = fragmentColor;\n" +
            "}\n";


    private PointF blurCenter;
    private int blurCenterLocation;
    private float blurSize;
    private int blurSizeLocation;

    public GPUImageZoomBlurFilter(Context context, PointF blurCenter, float blurSize) {
        super(context);
        this.blurCenter = blurCenter;
        this.blurSize = blurSize;
    }

    @Override
    public String getFragmentSource() {
        return ZOOM_BLUR_FRAGMENT_SHADER;
    }

    @Override
    public void init() {
        super.init();
        blurCenterLocation = GLES20.glGetUniformLocation(getProgram(), "blurCenter");
        blurSizeLocation = GLES20.glGetUniformLocation(getProgram(), "blurSize");
    }

    @Override
    public void update() {
        super.update();
        float[] vec2 = new float[2];
        vec2[0] = blurCenter.x;
        vec2[1] = blurCenter.y;
        GLES20.glUniform2fv(blurCenterLocation, 1, vec2, 0);
        GLES20.glUniform1f(blurSizeLocation, blurSize);
    }
}
