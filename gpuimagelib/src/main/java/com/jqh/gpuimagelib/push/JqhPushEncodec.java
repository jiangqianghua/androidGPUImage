package com.jqh.gpuimagelib.push;

import android.content.Context;

import com.jqh.gpuimagelib.camera.GPUCameraRender;
import com.jqh.gpuimagelib.encodec.JqhEncodecRender;
import com.jqh.gpuimagelib.encodec.JqhMediaEncodec;
import com.jqh.gpuimagelib.render.filter.BaseGPUImageFilter;
import com.jqh.gpuimagelib.render.textrue.BaseTexture;

public class JqhPushEncodec extends JqhBasePushEncoder {

    private JqhEncodecRender render;

    public JqhPushEncodec(Context context, int textureId) {
        super(context);
        render = new JqhEncodecRender(context, textureId);
        setRender(render);
        setmRenderMode(JqhBasePushEncoder.RENDERMODE_CONTINUOUSLY);
    }

    public void addFilter(BaseGPUImageFilter filter) {
        this.render.addFilter(filter);
    }

    public void addTexture(BaseTexture baseTexture) {
        if (baseTexture != null){
            this.render.addTexture(baseTexture);
        }
    }

    public void removeTexture(String key) {
        render.removeTexture(key);
    }

    public void updateTexture(String id, float left, float top, float scale) {
        render.updateTexture(id, left, top, scale);
    }
}
