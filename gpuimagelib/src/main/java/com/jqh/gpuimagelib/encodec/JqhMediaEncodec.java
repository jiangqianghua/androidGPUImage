package com.jqh.gpuimagelib.encodec;

import android.content.Context;

import com.jqh.gpuimagelib.render.filter.BaseGPUImageFilter;
import com.jqh.gpuimagelib.render.textrue.BaseTexture;

public class JqhMediaEncodec extends JqhBaseMediaEncoder {

    private JqhEncodecRender jqhEncodecRender;

    public JqhMediaEncodec(Context context, int textureId) {
        super(context);
        jqhEncodecRender = new JqhEncodecRender(context , textureId);

        setRender(jqhEncodecRender);
        setRenderMode(JqhBaseMediaEncoder.RENDERMODE_CONTINUOUSLY);
    }

    public void addFilter(BaseGPUImageFilter filter) {
        this.jqhEncodecRender.addFilter(filter);
    }

    public void addTexture(BaseTexture baseTexture) {
        if (baseTexture != null){
            this.jqhEncodecRender.addTexture(baseTexture);
        }
    }

    public void removeTexture(String key) {
        jqhEncodecRender.removeTexture(key);
    }

    public void updateTexture(String id, float left, float top, float scale) {
        jqhEncodecRender.updateTexture(id, left, top, scale);
    }
}
