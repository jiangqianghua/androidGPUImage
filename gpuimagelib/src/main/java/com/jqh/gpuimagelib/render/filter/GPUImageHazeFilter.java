package com.jqh.gpuimagelib.render.filter;

import android.content.Context;
import android.opengl.GLES20;

public class GPUImageHazeFilter extends BaseGPUImageFilter {
    public static final String HAZE_FRAGMENT_SHADER = "" +
            "varying highp vec2 ft_Position;\n" +
            "\n" +
            "uniform sampler2D inputImageTexture;\n" +
            "\n" +
            "uniform lowp float distance;\n" +
            "uniform highp float slope;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "	//todo reconsider precision modifiers	 \n" +
            "	 highp vec4 color = vec4(1.0);//todo reimplement as a parameter\n" +
            "\n" +
            "	 highp float  d = ft_Position.y * slope  +  distance; \n" +
            "\n" +
            "	 highp vec4 c = texture2D(inputImageTexture, ft_Position) ; // consider using unpremultiply\n" +
            "\n" +
            "	 c = (c - d * color) / (1.0 -d);\n" +
            "\n" +
            "	 gl_FragColor = c; //consider using premultiply(c);\n" +
            "}\n";

    private float distance;
    private int distanceLocation;
    private float slope;
    private int slopeLocation;

    public GPUImageHazeFilter(Context context) {
        this(context,0.2f, 0.0f);
    }

    public GPUImageHazeFilter(Context context,float distance, float slope) {
        super(context);
        this.distance = distance;
        this.slope = slope;
    }
    @Override
    protected String getFragmentSource() {
        return HAZE_FRAGMENT_SHADER;
    }

    @Override
    public void init() {
        if (inited) return;
        super.init();
        distanceLocation = GLES20.glGetUniformLocation(getProgram(), "distance");
        slopeLocation = GLES20.glGetUniformLocation(getProgram(), "slope");
    }

    @Override
    public void update() {
        if (!isNeedUpdate) return;
        super.update();

        /**
         * Strength of the color applied. Default 0. Values between -.3 and .3 are best.
         *
         * @param distance -0.3 to 0.3 are best, default 0
         */
        setFloat(distanceLocation, distance);

        /**
         * Amount of color change. Default 0. Values between -.3 and .3 are best.
         *
         * @param slope -0.3 to 0.3 are best, default 0
         */
        setFloat(slopeLocation, slope);
    }
}
