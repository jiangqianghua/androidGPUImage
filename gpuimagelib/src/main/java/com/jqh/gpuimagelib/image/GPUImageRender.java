package com.jqh.gpuimagelib.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;

import com.jqh.gpuimagelib.R;
import com.jqh.gpuimagelib.filter.BaseGPUImageFilter;
import com.jqh.gpuimagelib.opengl.GLSurfaceView;
import com.jqh.gpuimagelib.opengl.ShaderUtils;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;


public class GPUImageRender implements GLSurfaceView.GLRender {

    BaseGPUImageFilter filter;
    private Context context;

    private float[] vertexData = {
            -1f, -1f,
            1f, -1f,
            -1f, 1f,
            1f, 1f
    };

    private float[] vertexData2 = {
            -0.5f, -0.5f,
            0.5f, -0.5f,
            -0.5f, 0.5f,
            0.5f, 0.5f
    };

    private float[] fragmentData = {
            0f, 1f,
            1f, 1f,
            0f, 0f,
            1f, 0f

    };
    private FloatBuffer vertexBuffer, fragmentBuffer;


    private int program ;
    private int vPosition; //  顶点
    private int fPosition; //  纹理

    private int textureId; // 纹理id
    private Bitmap bitmap;


    public GPUImageRender(Context context) {
        this.context = context;

        // 获取顶点和纹理buffer
        vertexBuffer = ShaderUtils.allocateBuffer(vertexData);
        fragmentBuffer = ShaderUtils.allocateBuffer(fragmentData);

    }

    @Override
    public void onSurfaceCreate() {

    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        GLES20.glViewport(0,0, width, height);
    }

    @Override
    public void onDrawFrame() {
        textureId = ShaderUtils.loadBitmapTexture(bitmap);
        ShaderUtils.clearScreenDefault();
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        String vertexSource = filter.getVertexSource();
        String fragmentSource = filter.getFragmentSource();
        program = ShaderUtils.createProgram(vertexSource, fragmentSource);
        GLES20.glUseProgram(program);
        vPosition = GLES20.glGetAttribLocation(program, "v_Position");
        fPosition = GLES20.glGetAttribLocation(program, "f_Position");
        ShaderUtils.renderTexture(vPosition, fPosition, vertexBuffer, fragmentBuffer);
        //  解除绑定纹理
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);


    }

    public void setImage(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public void setFilter(BaseGPUImageFilter filter) {
        this.filter = filter;
    }
}
