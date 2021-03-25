package com.jqh.gpuimagelib.render.filter;

import android.content.Context;
import android.opengl.GLES20;
/**
 * Adjusts the shadows and highlights of an image
 * shadows: Increase to lighten shadows, from 0.0 to 1.0, with 0.0 as the default.
 * highlights: Decrease to darken highlights, from 0.0 to 1.0, with 1.0 as the default.
 */
public class GPUImageHighlightShadowFilter extends BaseGPUImageFilter {

    public static final String HIGHLIGHT_SHADOW_FRAGMENT_SHADER = "" +
            " uniform sampler2D inputImageTexture;\n" +
            " varying highp vec2 ft_Position;\n" +
            "  \n" +
            " uniform lowp float shadows;\n" +
            " uniform lowp float highlights;\n" +
            " \n" +
            " const mediump vec3 luminanceWeighting = vec3(0.3, 0.3, 0.3);\n" +
            " \n" +
            " void main()\n" +
            " {\n" +
            " 	lowp vec4 source = texture2D(inputImageTexture, ft_Position);\n" +
            " 	mediump float luminance = dot(source.rgb, luminanceWeighting);\n" +
            " \n" +
            " 	mediump float shadow = clamp((pow(luminance, 1.0/(shadows+1.0)) + (-0.76)*pow(luminance, 2.0/(shadows+1.0))) - luminance, 0.0, 1.0);\n" +
            " 	mediump float highlight = clamp((1.0 - (pow(1.0-luminance, 1.0/(2.0-highlights)) + (-0.8)*pow(1.0-luminance, 2.0/(2.0-highlights)))) - luminance, -1.0, 0.0);\n" +
            " 	lowp vec3 result = vec3(0.0, 0.0, 0.0) + ((luminance + shadow + highlight) - 0.0) * ((source.rgb - vec3(0.0, 0.0, 0.0))/(luminance - 0.0));\n" +
            " \n" +
            " 	gl_FragColor = vec4(result.rgb, source.a);\n" +
            " }";

    private int shadowsLocation;
    private float shadows;
    private int highlightsLocation;
    private float highlights;

    public GPUImageHighlightShadowFilter(Context context) {
        this(context,0.5f, 0.5f);
    }

    public GPUImageHighlightShadowFilter(Context context,final float shadows, final float highlights) {
        super(context);
        this.highlights = highlights;
        this.shadows = shadows;
    }
    @Override
    protected String getFragmentSource() {
        return HIGHLIGHT_SHADOW_FRAGMENT_SHADER;
    }

    @Override
    public void init() {
        if (inited) return;
        super.init();
        highlightsLocation = GLES20.glGetUniformLocation(getProgram(), "highlights");
        shadowsLocation = GLES20.glGetUniformLocation(getProgram(), "shadows");
    }

    @Override
    public void update() {
        if (!isNeedUpdate) return;
        super.update();
        setFloat(highlightsLocation, this.highlights);
        setFloat(shadowsLocation, this.shadows);
    }
}
