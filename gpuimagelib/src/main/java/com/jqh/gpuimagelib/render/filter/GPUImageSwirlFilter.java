package com.jqh.gpuimagelib.render.filter;

import android.content.Context;
import android.graphics.PointF;
import android.opengl.GLES20;

public class GPUImageSwirlFilter extends BaseGPUImageFilter {
    public static final String SWIRL_FRAGMENT_SHADER = "" +
            "varying highp vec2 ft_Position;\n" +
            "\n" +
            "uniform sampler2D inputImageTexture;\n" +
            "\n" +
            "uniform highp vec2 center;\n" +
            "uniform highp float radius;\n" +
            "uniform highp float angle;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "highp vec2 textureCoordinateToUse = ft_Position;\n" +
            "highp float dist = distance(center, ft_Position);\n" +
            "if (dist < radius)\n" +
            "{\n" +
            "textureCoordinateToUse -= center;\n" +
            "highp float percent = (radius - dist) / radius;\n" +
            "highp float theta = percent * percent * angle * 8.0;\n" +
            "highp float s = sin(theta);\n" +
            "highp float c = cos(theta);\n" +
            "textureCoordinateToUse = vec2(dot(textureCoordinateToUse, vec2(c, -s)), dot(textureCoordinateToUse, vec2(s, c)));\n" +
            "textureCoordinateToUse += center;\n" +
            "}\n" +
            "\n" +
            "gl_FragColor = texture2D(inputImageTexture, textureCoordinateToUse );\n" +
            "\n" +
            "}\n";

    private float angle;
    private int angleLocation;
    private float radius;
    private int radiusLocation;
    private PointF center;
    private int centerLocation;

    public GPUImageSwirlFilter(Context context) {
        this(context,0.5f, 1.0f, new PointF(0.5f, 0.5f));
    }

    public GPUImageSwirlFilter(Context context, float radius, float angle, PointF center) {
        super(context);
        this.radius = radius;
        this.angle = angle;
        this.center = center;
    }

    @Override
    public void init() {
        if (inited) return;
        super.init();
        angleLocation = GLES20.glGetUniformLocation(getProgram(), "angle");
        radiusLocation = GLES20.glGetUniformLocation(getProgram(), "radius");
        centerLocation = GLES20.glGetUniformLocation(getProgram(), "center");
    }


    @Override
    public void update() {
        if (!isNeedUpdate) return;
        super.update();
        setFloat(radiusLocation, radius);
        setFloat(angleLocation, angle);
        setPoint(centerLocation, center);
    }

    @Override
    public String getFragmentSource() {
        return SWIRL_FRAGMENT_SHADER;
    }

}
