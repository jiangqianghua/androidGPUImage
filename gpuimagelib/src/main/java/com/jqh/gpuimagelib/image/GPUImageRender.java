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

    List<BaseGPUImageFilter> filterList = new ArrayList<>();
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

    private float[] fragmentData2 = {
            0f, 0.5f,
            0.5f, 0.5f,
            0.5f, 0f,
            0.5f, 0f

    };

    private FloatBuffer vertexBuffer, vertexBuffer2, fragmentBuffer, fragmentBuffer2;


    private int program ;
    private int vPosition; //  顶点
    private int fPosition; //  纹理

    private int textureId; // 纹理id
    private int textureId2; // 纹理id
    private Bitmap bitmap;


    public GPUImageRender(Context context) {
        this.context = context;

        // 获取顶点和纹理buffer
        vertexBuffer = ShaderUtils.allocateBuffer(vertexData);
        vertexBuffer2 = ShaderUtils.allocateBuffer(vertexData2);
        fragmentBuffer = ShaderUtils.allocateBuffer(fragmentData);
        fragmentBuffer2 = ShaderUtils.allocateBuffer(fragmentData2);

    }

    @Override
    public void onSurfaceCreate() {

        //  加载顶点源码
//        String vertexSource = ShaderUtils.getRawResource(context, R.raw.vertex_shader);
//        String fragmentSource = ShaderUtils.getRawResource(context, R.raw.fragment_shader);
//
//        program = ShaderUtils.createProgram(vertexSource, fragmentSource);
//
//
//        // 10 得到着色器中的属性
//        vPosition = GLES20.glGetAttribLocation(program, "v_Position");
//        fPosition = GLES20.glGetAttribLocation(program, "f_Position");

    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        GLES20.glViewport(0,0, width, height);
    }

    @Override
    public void onDrawFrame() {
        textureId = ShaderUtils.loadBitmapTexture(bitmap);
        textureId2 = ShaderUtils.loadTextrue(context, R.drawable.c);
        ShaderUtils.clearScreenDefault();

        for (int i = 0 ;i < filterList.size(); i++) {
            if (i == 0) {
                // 重新绑定纹理
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
                BaseGPUImageFilter filter = filterList.get(i);
                String vertexSource = filter.getVertexSource();
                String fragmentSource = filter.getFragmentSource();
                program = ShaderUtils.createProgram(vertexSource, fragmentSource);
                GLES20.glUseProgram(program);
                vPosition = GLES20.glGetAttribLocation(program, "v_Position");
                fPosition = GLES20.glGetAttribLocation(program, "f_Position");
                ShaderUtils.renderTexture(vPosition, fPosition, vertexBuffer, fragmentBuffer);
                //  解除绑定纹理
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
            } else {
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId2);
                BaseGPUImageFilter filter = filterList.get(i);
                String vertexSource = filter.getVertexSource();
                String fragmentSource = filter.getFragmentSource();
                program = ShaderUtils.createProgram(vertexSource, fragmentSource);
                GLES20.glUseProgram(program);
                vPosition = GLES20.glGetAttribLocation(program, "v_Position");
                fPosition = GLES20.glGetAttribLocation(program, "f_Position");
                ShaderUtils.renderTexture(vPosition, fPosition, vertexBuffer2, fragmentBuffer);
                //  解除绑定纹理
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
            }

        }


    }

    public void setImage(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public void addFilter(BaseGPUImageFilter filter) {
        filterList.add(filter);
    }
}
