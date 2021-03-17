package com.jqh.gpuimagelib.render.filter;

import android.content.Context;
import android.opengl.GLES20;

public class GPUImageBeautyFilter extends BaseGPUImageFilter {

    public static final String BILATERAL_FRAGMENT_SHADER = "" +
            "precision highp float;\n"+
            "   varying highp vec2 ft_Position;\n" +
            "\n" +
            "    uniform sampler2D vTexture;\n" +
            "\n" +
            "    uniform highp vec2 singleStepOffset;\n" +
            "    uniform highp vec4 params;\n" +
            "    uniform highp float brightness;\n" +
            "    uniform float texelWidthOffset;\n"+
            "    uniform float texelHeightOffset;\n"+
            "\n" +
            "    const highp vec3 W = vec3(0.299, 0.587, 0.114);\n" +
            "    const highp mat3 saturateMatrix = mat3(\n" +
            "        1.1102, -0.0598, -0.061,\n" +
            "        -0.0774, 1.0826, -0.1186,\n" +
            "        -0.0228, -0.0228, 1.1772);\n" +
            "    highp vec2 blurCoordinates[24];\n" +
            "\n" +
            "    highp float hardLight(highp float color) {\n" +
            "    if (color <= 0.5)\n" +
            "        color = color * color * 2.0;\n" +
            "    else\n" +
            "        color = 1.0 - ((1.0 - color)*(1.0 - color) * 2.0);\n" +
            "    return color;\n" +
            "}\n" +
            "\n" +
            "    void main(){\n" +
            "    highp vec3 centralColor = texture2D(vTexture, ft_Position).rgb;\n" +
            "    vec2 singleStepOffset=vec2(texelWidthOffset,texelHeightOffset);\n"+
            "    blurCoordinates[0] = ft_Position.xy + singleStepOffset * vec2(0.0, -10.0);\n" +
            "    blurCoordinates[1] = ft_Position.xy + singleStepOffset * vec2(0.0, 10.0);\n" +
            "    blurCoordinates[2] = ft_Position.xy + singleStepOffset * vec2(-10.0, 0.0);\n" +
            "    blurCoordinates[3] = ft_Position.xy + singleStepOffset * vec2(10.0, 0.0);\n" +
            "    blurCoordinates[4] = ft_Position.xy + singleStepOffset * vec2(5.0, -8.0);\n" +
            "    blurCoordinates[5] = ft_Position.xy + singleStepOffset * vec2(5.0, 8.0);\n" +
            "    blurCoordinates[6] = ft_Position.xy + singleStepOffset * vec2(-5.0, 8.0);\n" +
            "    blurCoordinates[7] = ft_Position.xy + singleStepOffset * vec2(-5.0, -8.0);\n" +
            "    blurCoordinates[8] = ft_Position.xy + singleStepOffset * vec2(8.0, -5.0);\n" +
            "    blurCoordinates[9] = ft_Position.xy + singleStepOffset * vec2(8.0, 5.0);\n" +
            "    blurCoordinates[10] = ft_Position.xy + singleStepOffset * vec2(-8.0, 5.0);\n" +
            "    blurCoordinates[11] = ft_Position.xy + singleStepOffset * vec2(-8.0, -5.0);\n" +
            "    blurCoordinates[12] = ft_Position.xy + singleStepOffset * vec2(0.0, -6.0);\n" +
            "    blurCoordinates[13] = ft_Position.xy + singleStepOffset * vec2(0.0, 6.0);\n" +
            "    blurCoordinates[14] = ft_Position.xy + singleStepOffset * vec2(6.0, 0.0);\n" +
            "    blurCoordinates[15] = ft_Position.xy + singleStepOffset * vec2(-6.0, 0.0);\n" +
            "    blurCoordinates[16] = ft_Position.xy + singleStepOffset * vec2(-4.0, -4.0);\n" +
            "    blurCoordinates[17] = ft_Position.xy + singleStepOffset * vec2(-4.0, 4.0);\n" +
            "    blurCoordinates[18] = ft_Position.xy + singleStepOffset * vec2(4.0, -4.0);\n" +
            "    blurCoordinates[19] = ft_Position.xy + singleStepOffset * vec2(4.0, 4.0);\n" +
            "    blurCoordinates[20] = ft_Position.xy + singleStepOffset * vec2(-2.0, -2.0);\n" +
            "    blurCoordinates[21] = ft_Position.xy + singleStepOffset * vec2(-2.0, 2.0);\n" +
            "    blurCoordinates[22] = ft_Position.xy + singleStepOffset * vec2(2.0, -2.0);\n" +
            "    blurCoordinates[23] = ft_Position.xy + singleStepOffset * vec2(2.0, 2.0);\n" +
            "\n" +
            "    highp float sampleColor = centralColor.g * 22.0;\n" +
            "    sampleColor += texture2D(vTexture, blurCoordinates[0]).g;\n" +
            "    sampleColor += texture2D(vTexture, blurCoordinates[1]).g;\n" +
            "    sampleColor += texture2D(vTexture, blurCoordinates[2]).g;\n" +
            "    sampleColor += texture2D(vTexture, blurCoordinates[3]).g;\n" +
            "    sampleColor += texture2D(vTexture, blurCoordinates[4]).g;\n" +
            "    sampleColor += texture2D(vTexture, blurCoordinates[5]).g;\n" +
            "    sampleColor += texture2D(vTexture, blurCoordinates[6]).g;\n" +
            "    sampleColor += texture2D(vTexture, blurCoordinates[7]).g;\n" +
            "    sampleColor += texture2D(vTexture, blurCoordinates[8]).g;\n" +
            "    sampleColor += texture2D(vTexture, blurCoordinates[9]).g;\n" +
            "    sampleColor += texture2D(vTexture, blurCoordinates[10]).g;\n" +
            "    sampleColor += texture2D(vTexture, blurCoordinates[11]).g;\n" +
            "    sampleColor += texture2D(vTexture, blurCoordinates[12]).g * 2.0;\n" +
            "    sampleColor += texture2D(vTexture, blurCoordinates[13]).g * 2.0;\n" +
            "    sampleColor += texture2D(vTexture, blurCoordinates[14]).g * 2.0;\n" +
            "    sampleColor += texture2D(vTexture, blurCoordinates[15]).g * 2.0;\n" +
            "    sampleColor += texture2D(vTexture, blurCoordinates[16]).g * 2.0;\n" +
            "    sampleColor += texture2D(vTexture, blurCoordinates[17]).g * 2.0;\n" +
            "    sampleColor += texture2D(vTexture, blurCoordinates[18]).g * 2.0;\n" +
            "    sampleColor += texture2D(vTexture, blurCoordinates[19]).g * 2.0;\n" +
            "    sampleColor += texture2D(vTexture, blurCoordinates[20]).g * 3.0;\n" +
            "    sampleColor += texture2D(vTexture, blurCoordinates[21]).g * 3.0;\n" +
            "    sampleColor += texture2D(vTexture, blurCoordinates[22]).g * 3.0;\n" +
            "    sampleColor += texture2D(vTexture, blurCoordinates[23]).g * 3.0;\n" +
            "\n" +
            "    sampleColor = sampleColor / 62.0;\n" +
            "\n" +
            "    highp float highPass = centralColor.g - sampleColor + 0.5;\n" +
            "\n" +
            "    for (int i = 0; i < 5; i++) {\n" +
            "        highPass = hardLight(highPass);\n" +
            "    }\n" +
            "    highp float lumance = dot(centralColor, W);\n" +
            "\n" +
            "    highp float alpha = pow(lumance, params.r);\n" +
            "\n" +
            "    highp vec3 smoothColor = centralColor + (centralColor-vec3(highPass))*alpha*0.1;\n" +
            "\n" +
            "    smoothColor.r = clamp(pow(smoothColor.r, params.g), 0.0, 1.0);\n" +
            "    smoothColor.g = clamp(pow(smoothColor.g, params.g), 0.0, 1.0);\n" +
            "    smoothColor.b = clamp(pow(smoothColor.b, params.g), 0.0, 1.0);\n" +
            "\n" +
            "    highp vec3 lvse = vec3(1.0)-(vec3(1.0)-smoothColor)*(vec3(1.0)-centralColor);\n" +
            "    highp vec3 bianliang = max(smoothColor, centralColor);\n" +
            "    highp vec3 rouguang = 2.0*centralColor*smoothColor + centralColor*centralColor - 2.0*centralColor*centralColor*smoothColor;\n" +
            "\n" +
            "    gl_FragColor = vec4(mix(centralColor, lvse, alpha), 1.0);\n" +
            "    gl_FragColor.rgb = mix(gl_FragColor.rgb, bianliang, alpha);\n" +
            "    gl_FragColor.rgb = mix(gl_FragColor.rgb, rouguang, params.b);\n" +
            "\n" +
            "    highp vec3 satcolor = gl_FragColor.rgb * saturateMatrix;\n" +
            "    gl_FragColor.rgb = mix(gl_FragColor.rgb, satcolor, params.a);\n" +
            "    gl_FragColor.rgb = vec3(gl_FragColor.rgb + vec3(brightness));\n" +
            "}";

    private int paramsLocation;
    private int brightnessLocation;
    private int singleStepOffsetLocation;
    private int texelWidthLocation;
    private int texelHeightLocation;

    private float toneLevel;
    private float beautyLevel;
    private float brightLevel;
    private float texelWidthOffset;
    private float texelHeightOffset;
    public GPUImageBeautyFilter(Context context) {
        super(context);
        toneLevel   = 1f;
        beautyLevel = 0f;
        brightLevel = 0.47f;
        texelWidthOffset=texelHeightOffset=2;
    }

    @Override
    public void init() {
        if (inited) return;
        super.init();
        paramsLocation = GLES20.glGetUniformLocation(getProgram(), "params");
        brightnessLocation = GLES20.glGetUniformLocation(getProgram(), "brightness");
        singleStepOffsetLocation = GLES20.glGetUniformLocation(getProgram(), "singleStepOffset");
        texelWidthLocation = GLES20.glGetUniformLocation(getProgram(), "texelWidthOffset");
        texelHeightLocation = GLES20.glGetUniformLocation(getProgram(), "texelHeightOffset");
    }


    @Override
    public void update() {
        if (!isNeedUpdate) return;
        super.update();
        float[] vector = new float[4];
        vector[0] = 1.0f - 0.6f * beautyLevel;
        vector[1] = 1.0f - 0.3f * beautyLevel;
        vector[2] = 0.1f + 0.3f * toneLevel;
        vector[3] = 0.1f + 0.3f * toneLevel;
        setFloatVec4(paramsLocation, vector);
        setFloat(brightnessLocation, 0.6f * (-0.5f + brightLevel));
    }

    public void setParams(float beauty, float tone) {
        this.beautyLevel=beauty;
        this.toneLevel = tone;
        isNeedUpdate = false;
    }


    public void setBrightLevel(float brightLevel) {
        this.brightLevel = brightLevel;
        isNeedUpdate = false;
    }

    @Override
    public void onOutputSizeChanged(int width, int height) {
        super.onOutputSizeChanged(width, height);
        float texelOffset = 2;
        texelWidthOffset=texelHeightOffset= texelOffset;
        setFloat(texelWidthLocation, texelOffset/width);
        setFloat(texelHeightLocation, texelOffset/height);

        setFloatVec2(singleStepOffsetLocation, new float[] {2.0f / width, 2.0f / height});
    }

    @Override
    public String getFragmentSource() {
        return BILATERAL_FRAGMENT_SHADER;
    }

}
