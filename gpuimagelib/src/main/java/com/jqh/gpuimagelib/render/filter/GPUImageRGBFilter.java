package com.jqh.gpuimagelib.render.filter;

import android.content.Context;
import android.opengl.GLES20;

public class GPUImageRGBFilter extends BaseGPUImageFilter {
    public static final String RGB_FRAGMENT_SHADER = "" +
            "  varying highp vec2 ft_Position;\n" +
            "  \n" +
            "  uniform sampler2D inputImageTexture;\n" +
            "  uniform highp float red;\n" +
            "  uniform highp float green;\n" +
            "  uniform highp float blue;\n" +
            "  \n" +
            "  void main()\n" +
            "  {\n" +
            "      highp vec4 textureColor = texture2D(inputImageTexture, ft_Position);\n" +
            "      \n" +
            "      gl_FragColor = vec4(textureColor.r * red, textureColor.g * green, textureColor.b * blue, 1.0);\n" +
            "  }\n";

    private int redLocation;
    private float red;
    private int greenLocation;
    private float green;
    private int blueLocation;
    private float blue;

    public GPUImageRGBFilter(Context context) {
        this(context,1.0f, 1.0f, 1.0f);
    }

    public GPUImageRGBFilter(Context context, final float red, final float green, final float blue) {
        super(context);
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public void setRGB(final float red, final float green, final float blue){
        this.red = red;
        this.green = green;
        this.blue = blue;
        isNeedUpdate = true;
    }

    @Override
    public void init() {
        if (inited) return;
        super.init();
        redLocation = GLES20.glGetUniformLocation(getProgram(), "red");
        greenLocation = GLES20.glGetUniformLocation(getProgram(), "green");
        blueLocation = GLES20.glGetUniformLocation(getProgram(), "blue");
    }

    @Override
    protected String getFragmentSource() {
        return RGB_FRAGMENT_SHADER;
    }

    @Override
    public void update() {
        if (!isNeedUpdate) return;
        super.update();
        setFloat(blueLocation, this.blue);
        setFloat(greenLocation, this.green);
        setFloat(redLocation, this.red);
    }

}
