package com.jqh.gpuimagelib.render.filter;

import android.content.Context;
import android.graphics.PointF;
import android.opengl.GLES20;
// 变形小球
public class GPUImageBulgeDistortionFilter extends BaseGPUImageFilter {
    public static final String BULGE_FRAGMENT_SHADER = "" +
            "varying highp vec2 ft_Position;\n" +
            "\n" +
            "uniform sampler2D inputImageTexture;\n" +
            "\n" +
            "uniform highp float aspectRatio;\n" +
            "uniform highp vec2 center;\n" +
            "uniform highp float radius;\n" +
            "uniform highp float scale;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "highp vec2 textureCoordinateToUse = vec2(ft_Position.x, (ft_Position.y * aspectRatio + 0.5 - 0.5 * aspectRatio));\n" +
            "highp float dist = distance(center, textureCoordinateToUse);\n" +
            "textureCoordinateToUse = ft_Position;\n" +
            "\n" +
            "if (dist < radius)\n" +
            "{\n" +
            "textureCoordinateToUse -= center;\n" +
            "highp float percent = 1.0 - ((radius - dist) / radius) * scale;\n" +
            "percent = percent * percent;\n" +
            "\n" +
            "textureCoordinateToUse = textureCoordinateToUse * percent;\n" +
            "textureCoordinateToUse += center;\n" +
            "}\n" +
            "\n" +
            "gl_FragColor = texture2D(inputImageTexture, textureCoordinateToUse );    \n" +
            "}\n";

    private float scale;
    private int scaleLocation;
    private float radius;
    private int radiusLocation;
    private PointF center;
    private int centerLocation;
    private float aspectRatio;
    private int aspectRatioLocation;


    public GPUImageBulgeDistortionFilter(Context context) {
        this(context,0.25f, 0.5f, new PointF(0.5f, 0.5f));
    }

    public GPUImageBulgeDistortionFilter(Context context,float radius, float scale, PointF center) {
        super(context);
        this.radius = radius;
        this.scale = scale;
        this.center = center;
    }

    @Override
    public void init() {
        if (inited) return;
        super.init();
        scaleLocation = GLES20.glGetUniformLocation(getProgram(), "scale");
        radiusLocation = GLES20.glGetUniformLocation(getProgram(), "radius");
        centerLocation = GLES20.glGetUniformLocation(getProgram(), "center");
        aspectRatioLocation = GLES20.glGetUniformLocation(getProgram(), "aspectRatio");
    }


    @Override
    public void update() {
        if (!isNeedUpdate) return;
        super.update();
        setFloat(radiusLocation, radius);
        setFloat(scaleLocation, scale);
        setPoint(centerLocation, center);
    }

    @Override
    public void onOutputSizeChanged(int width, int height) {
        aspectRatio = (float) height / width;
        setFloat(aspectRatioLocation, aspectRatio);
        super.onOutputSizeChanged(width, height);
    }

    @Override
    public String getFragmentSource() {
        return BULGE_FRAGMENT_SHADER;
    }

}
