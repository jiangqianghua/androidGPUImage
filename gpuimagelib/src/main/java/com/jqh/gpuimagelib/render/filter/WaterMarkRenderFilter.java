package com.jqh.gpuimagelib.render.filter;

import android.content.Context;

public class WaterMarkRenderFilter extends BaseRenderFilter{

    private String text;
    public WaterMarkRenderFilter(Context context, String text) {
        super(context);
        this.text = text;



    }
}
