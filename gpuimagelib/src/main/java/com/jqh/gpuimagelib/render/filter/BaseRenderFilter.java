package com.jqh.gpuimagelib.render.filter;

import android.content.Context;
import android.opengl.GLES20;

import com.jqh.gpuimagelib.R;
import com.jqh.gpuimagelib.opengl.ShaderUtils;

import java.nio.FloatBuffer;

public class BaseRenderFilter {

    private Context context;

    // 绘制上半部分
    private float[] vertexData = {
            -1f, -1f,
            1f, -1f,
            -1f, 1f,
            1f, 1f,
    };

    private float[] fragmentData = {
            0f, 1f,
            1f, 1f,
            0f, 0f,
            1f, 0f
    };

    private FloatBuffer vertexBuffer ;
    private FloatBuffer fragmentBuffer;

    private int program ;
    private int vPosition; //  顶点
    private int fPosition; //  纹理

    public float[] getVertexData() {
        return vertexData;
    }

    public float[] getFragmentData() {
        return fragmentData;
    }

    public FloatBuffer getVertexBuffer() {
        return vertexBuffer;
    }

    public FloatBuffer getFragmentBuffer() {
        return fragmentBuffer;
    }

    public int getProgram() {
        return program;
    }

    public int getvPosition() {
        return vPosition;
    }

    public int getfPosition() {
        return fPosition;
    }

    public BaseRenderFilter(Context context) {
        this.context = context;
        vertexBuffer = ShaderUtils.allocateBuffer(vertexData);

        fragmentBuffer = ShaderUtils.allocateBuffer(fragmentData);
    }

    public void init(){
        program = ShaderUtils.createProgram(getVertexSource(), getFragmentSource());
        vPosition = GLES20.glGetAttribLocation(program, "v_Position");
        fPosition = GLES20.glGetAttribLocation(program, "f_Position");
    }

    public void update(){

    }


    private String getVertexSource(){
        return  ShaderUtils.getRawResource(context,  R.raw.vertex_shader );
    }

    private String getFragmentSource(){
        return ShaderUtils.getRawResource(context, R.raw.fragment_shader);
    }

}
