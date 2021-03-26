package com.jqh.gpuimagelib.render.filter;

import android.content.Context;
import android.opengl.GLES20;

import com.jqh.gpuimagelib.R;
import com.jqh.gpuimagelib.opengl.ShaderUtils;
import com.jqh.gpuimagelib.utils.LogUtils;

public class GPUImageOpacityFilter extends BaseGPUImageFilter {

    public static final String OPACITY_FRAGMENT_SHADER = "" +
            "  varying highp vec2 ft_Position;\n" +
            "  \n" +
            "  uniform sampler2D inputImageTexture;\n" +
            "  uniform lowp float opacity;\n" +
            "  \n" +
            "  void main()\n" +
            "  {\n" +
            "      lowp vec4 textureColor = texture2D(inputImageTexture, ft_Position);\n" +
            "      \n" +
            "      gl_FragColor = vec4(textureColor.rgb, textureColor.a * opacity);\n" +
            "  }\n";
    private int opacityLocation;
    private float opacity;
    public GPUImageOpacityFilter(Context context, float opacity) {
        super(context);
        this.opacity = opacity;
    }

    @Override
    public void init() {
        if (inited) return;
        super.init();
        opacityLocation = GLES20.glGetUniformLocation(getProgram(), "opacity");
        LogUtils.logd("init Opacity opacityLocation = " + opacityLocation);
    }

    @Override
    protected String getFragmentSource() {
        return ShaderUtils.getRawResource(context, R.raw.fragement_opacity_shader);
    }

    @Override
    public void update() {
        if (!isNeedUpdate) return;
        super.update();
        GLES20.glUniform1f(opacityLocation, opacity);
    }

    public void setOpacity(float opacity) {
        this.opacity = opacity;
        setNeedUpdate();
    }
}
