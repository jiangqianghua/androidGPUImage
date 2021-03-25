package com.jqh.gpuimagelib.render.filter;

import android.content.Context;
import android.opengl.GLES20;

public class GPUImageCrosshatchFilter extends BaseGPUImageFilter {

    public static final String CROSSHATCH_FRAGMENT_SHADER = "" +
            "varying highp vec2 ft_Position;\n" +
            "uniform sampler2D inputImageTexture;\n" +
            "uniform highp float crossHatchSpacing;\n" +
            "uniform highp float lineWidth;\n" +
            "const highp vec3 W = vec3(0.2125, 0.7154, 0.0721);\n" +
            "void main()\n" +
            "{\n" +
            "highp float luminance = dot(texture2D(inputImageTexture, ft_Position).rgb, W);\n" +
            "lowp vec4 colorToDisplay = vec4(1.0, 1.0, 1.0, 1.0);\n" +
            "if (luminance < 1.00)\n" +
            "{\n" +
            "if (mod(ft_Position.x + ft_Position.y, crossHatchSpacing) <= lineWidth)\n" +
            "{\n" +
            "colorToDisplay = vec4(0.0, 0.0, 0.0, 1.0);\n" +
            "}\n" +
            "}\n" +
            "if (luminance < 0.75)\n" +
            "{\n" +
            "if (mod(ft_Position.x - ft_Position.y, crossHatchSpacing) <= lineWidth)\n" +
            "{\n" +
            "colorToDisplay = vec4(0.0, 0.0, 0.0, 1.0);\n" +
            "}\n" +
            "}\n" +
            "if (luminance < 0.50)\n" +
            "{\n" +
            "if (mod(ft_Position.x + ft_Position.y - (crossHatchSpacing / 2.0), crossHatchSpacing) <= lineWidth)\n" +
            "{\n" +
            "colorToDisplay = vec4(0.0, 0.0, 0.0, 1.0);\n" +
            "}\n" +
            "}\n" +
            "if (luminance < 0.3)\n" +
            "{\n" +
            "if (mod(ft_Position.x - ft_Position.y - (crossHatchSpacing / 2.0), crossHatchSpacing) <= lineWidth)\n" +
            "{\n" +
            "colorToDisplay = vec4(0.0, 0.0, 0.0, 1.0);\n" +
            "}\n" +
            "}\n" +
            "gl_FragColor = colorToDisplay;\n" +
            "}\n";

    private float crossHatchSpacing;
    private int crossHatchSpacingLocation;
    private float lineWidth;
    private int lineWidthLocation;

    /**
     * Using default values of crossHatchSpacing: 0.03f and lineWidth: 0.003f.
     */
    public GPUImageCrosshatchFilter(Context context) {
        this(context,0.03f, 0.003f);
    }

    public GPUImageCrosshatchFilter(Context context,float crossHatchSpacing, float lineWidth) {
        super(context);
        this.crossHatchSpacing = crossHatchSpacing;
        this.lineWidth = lineWidth;
    }

    @Override
    protected String getFragmentSource() {
        return CROSSHATCH_FRAGMENT_SHADER;
    }

    @Override
    public void init() {
        if (inited) return;
        super.init();
        crossHatchSpacingLocation = GLES20.glGetUniformLocation(getProgram(), "crossHatchSpacing");
        lineWidthLocation = GLES20.glGetUniformLocation(getProgram(), "lineWidth");
    }

    @Override
    public void update() {
        if (!isNeedUpdate) return;
        super.update();
        setFloat(lineWidthLocation, this.lineWidth);
    }


    @Override
    public void onOutputSizeChanged(int width, int height) {
        if (!isNeedChangeWH) return ;
        super.onOutputSizeChanged(width, height);
        float singlePixelSpacing;
        if (width != 0) {
            singlePixelSpacing = 1.0f / (float) width;
        } else {
            singlePixelSpacing = 1.0f / 2048.0f;
        }

        if (crossHatchSpacing < singlePixelSpacing) {
            this.crossHatchSpacing = singlePixelSpacing;
        } else {
            this.crossHatchSpacing = crossHatchSpacing;
        }

        setFloat(crossHatchSpacingLocation, this.crossHatchSpacing);
    }
}
