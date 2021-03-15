package com.jqh.gpuimagelib.render.textrue;

import android.content.Context;
import android.graphics.Bitmap;

import com.jqh.gpuimagelib.opengl.ShaderUtils;

public class TextTexture extends BaseTexture {


    public TextTexture(Context context, String id, String text, int fontSize, String color, float left, float top, float showScale) {

        super(context, id, null, left, top, showScale);

        bitmap = ShaderUtils.createTextImage(text, fontSize, color, "#00000000", 0);


    }
}
