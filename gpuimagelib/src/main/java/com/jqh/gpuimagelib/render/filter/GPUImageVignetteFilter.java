package com.jqh.gpuimagelib.render.filter;

import android.content.Context;
import android.graphics.PointF;
import android.opengl.GLES20;

public class GPUImageVignetteFilter extends BaseGPUImageFilter {
    public static final String VIGNETTING_FRAGMENT_SHADER = "" +
            " uniform sampler2D inputImageTexture;\n" +
            " varying highp vec2 ft_Position;\n" +
            " \n" +
            " uniform lowp vec2 vignetteCenter;\n" +
            " uniform lowp vec3 vignetteColor;\n" +
            " uniform highp float vignetteStart;\n" +
            " uniform highp float vignetteEnd;\n" +
            " \n" +
            " void main()\n" +
            " {\n" +
            "     /*\n" +
            "     lowp vec3 rgb = texture2D(inputImageTexture, ft_Position).rgb;\n" +
            "     lowp float d = distance(ft_Position, vec2(0.5,0.5));\n" +
            "     rgb *= (1.0 - smoothstep(vignetteStart, vignetteEnd, d));\n" +
            "     gl_FragColor = vec4(vec3(rgb),1.0);\n" +
            "      */\n" +
            "     \n" +
            "     lowp vec3 rgb = texture2D(inputImageTexture, ft_Position).rgb;\n" +
            "     lowp float d = distance(ft_Position, vec2(vignetteCenter.x, vignetteCenter.y));\n" +
            "     lowp float percent = smoothstep(vignetteStart, vignetteEnd, d);\n" +
            "     gl_FragColor = vec4(mix(rgb.x, vignetteColor.x, percent), mix(rgb.y, vignetteColor.y, percent), mix(rgb.z, vignetteColor.z, percent), 1.0);\n" +
            " }";

    private int vignetteCenterLocation;
    private PointF vignetteCenter;
    private int vignetteColorLocation;
    private float[] vignetteColor;
    private int vignetteStartLocation;
    private float vignetteStart;
    private int vignetteEndLocation;
    private float vignetteEnd;

    public GPUImageVignetteFilter(Context context) {
        this(context,new PointF(), new float[]{0.0f, 0.0f, 0.0f}, 0.3f, 0.75f);
    }

    public GPUImageVignetteFilter(Context context, final PointF vignetteCenter, final float[] vignetteColor, final float vignetteStart, final float vignetteEnd) {
        super(context);
        this.vignetteCenter = vignetteCenter;
        this.vignetteColor = vignetteColor;
        this.vignetteStart = vignetteStart;
        this.vignetteEnd = vignetteEnd;

    }

    @Override
    public void init() {
        if (inited) return;
        super.init();
        vignetteCenterLocation = GLES20.glGetUniformLocation(getProgram(), "vignetteCenter");
        vignetteColorLocation = GLES20.glGetUniformLocation(getProgram(), "vignetteColor");
        vignetteStartLocation = GLES20.glGetUniformLocation(getProgram(), "vignetteStart");
        vignetteEndLocation = GLES20.glGetUniformLocation(getProgram(), "vignetteEnd");
    }


    @Override
    public void update() {
        if (!isNeedUpdate) return;
        super.update();
        setPoint(vignetteCenterLocation, this.vignetteCenter);
        setFloatVec3(vignetteColorLocation, this.vignetteColor);
        setFloat(vignetteStartLocation, this.vignetteStart);
        setFloat(vignetteEndLocation, this.vignetteEnd);
    }

    @Override
    public String getFragmentSource() {
        return VIGNETTING_FRAGMENT_SHADER;
    }


}
