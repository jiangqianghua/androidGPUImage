package com.jqh.gpuimagelib.render.filter;

import android.content.Context;
import android.opengl.GLES20;

public class GPUImageHalftoneFilter extends BaseGPUImageFilter {

    public static final String HALFTONE_FRAGMENT_SHADER = "" +
            "varying highp vec2 ft_Position;\n" +

            "uniform sampler2D inputImageTexture;\n" +

            "uniform highp float fractionalWidthOfPixel;\n" +
            "uniform highp float aspectRatio;\n" +

            "const highp vec3 W = vec3(0.2125, 0.7154, 0.0721);\n" +

            "void main()\n" +
            "{\n" +
            "  highp vec2 sampleDivisor = vec2(fractionalWidthOfPixel, fractionalWidthOfPixel / aspectRatio);\n" +
            "  highp vec2 samplePos = ft_Position - mod(ft_Position, sampleDivisor) + 0.5 * sampleDivisor;\n" +
            "  highp vec2 textureCoordinateToUse = vec2(ft_Position.x, (ft_Position.y * aspectRatio + 0.5 - 0.5 * aspectRatio));\n" +
            "  highp vec2 adjustedSamplePos = vec2(samplePos.x, (samplePos.y * aspectRatio + 0.5 - 0.5 * aspectRatio));\n" +
            "  highp float distanceFromSamplePoint = distance(adjustedSamplePos, textureCoordinateToUse);\n" +
            "  lowp vec3 sampledColor = texture2D(inputImageTexture, samplePos).rgb;\n" +
            "  highp float dotScaling = 1.0 - dot(sampledColor, W);\n" +
            "  lowp float checkForPresenceWithinDot = 1.0 - step(distanceFromSamplePoint, (fractionalWidthOfPixel * 0.5) * dotScaling);\n" +
            "  gl_FragColor = vec4(vec3(checkForPresenceWithinDot), 1.0);\n" +
            "}";

    private int fractionalWidthOfPixelLocation;
    private int aspectRatioLocation;

    private float fractionalWidthOfAPixel;
    private float aspectRatio;

    public GPUImageHalftoneFilter(Context context) {
        this(context,0.01f);
    }

    public GPUImageHalftoneFilter(Context context,float fractionalWidthOfAPixel) {
        super(context);
        this.fractionalWidthOfAPixel = fractionalWidthOfAPixel;
    }

    @Override
    protected String getFragmentSource() {
        return HALFTONE_FRAGMENT_SHADER;
    }

    @Override
    public void init() {
        if (inited) return;
        super.init();
        fractionalWidthOfPixelLocation = GLES20.glGetUniformLocation(getProgram(), "fractionalWidthOfPixel");
        aspectRatioLocation = GLES20.glGetUniformLocation(getProgram(), "aspectRatio");
    }

    @Override
    public void update() {
        if (!isNeedUpdate) return;
        super.update();
        setFloat(fractionalWidthOfPixelLocation, this.fractionalWidthOfAPixel);
    }

    @Override
    public void onOutputSizeChanged(int width, int height) {
        if (!isNeedChangeWH) return;
        super.onOutputSizeChanged(width, height);
        aspectRatio = (float) height / width;
        setFloat(aspectRatioLocation, aspectRatio);
        super.onOutputSizeChanged(width, height);
    }
}
