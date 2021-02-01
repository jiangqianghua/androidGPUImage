package com.jqh.gpuimagelib.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;

import com.jqh.gpuimagelib.filter.BaseGPUImageFilter;
import com.jqh.gpuimagelib.opengl.GLSurfaceView;

public class GPUImageView extends GLSurfaceView {

    private GPUImageRender render;
    public GPUImageView(Context context) {
        this(context, null);
    }

    public GPUImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GPUImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        render = new GPUImageRender(context);
        setRender(render);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    public GPUImageView setImage(Bitmap bitmap) {
        if (bitmap != null) {
            render.setImage(bitmap);
        }
        return this;
    }

    public GPUImageView setFilter(BaseGPUImageFilter filter) {
        if (filter != null && render != null) {
            render.setFilter(filter);
            flush();
        }
        return this;
    }

    public void flush() {
        requestRender();
    }



}
