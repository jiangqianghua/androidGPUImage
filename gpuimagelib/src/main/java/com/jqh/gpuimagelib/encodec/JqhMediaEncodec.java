package com.jqh.gpuimagelib.encodec;

import android.content.Context;

import com.jqh.gpuimagelib.render.filter.BaseRenderFilter;
import com.jqh.gpuimagelib.render.textrue.BaseTexture;

public class JqhMediaEncodec extends JqhBaseMediaEncoder {

    private JqhEncodecRender jqhEncodecRender;

    public JqhMediaEncodec(Context context, int textureId) {
        super(context);
        jqhEncodecRender = new JqhEncodecRender(context , textureId);

        setRender(jqhEncodecRender);
        setRenderMode(JqhBaseMediaEncoder.RENDERMODE_CONTINUOUSLY);
    }

    public void addFilter(BaseRenderFilter filter) {
        this.jqhEncodecRender.addFilter(filter);
    }

    public void addTexture(BaseTexture baseTexture) {
        if (baseTexture != null){
            this.jqhEncodecRender.addTexture(baseTexture);
        }
    }
}
