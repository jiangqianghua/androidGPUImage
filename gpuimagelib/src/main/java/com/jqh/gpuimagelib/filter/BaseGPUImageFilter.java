package com.jqh.gpuimagelib.filter;

import android.content.Context;
import android.opengl.GLES20;

import com.jqh.gpuimagelib.R;
import com.jqh.gpuimagelib.opengl.ShaderUtils;

import java.nio.FloatBuffer;

public class BaseGPUImageFilter {
    protected Context context;

    private boolean filterChange = true;
    private int vPosition; //  顶点
    private int fPosition; //  纹理
    private boolean isMedia = false;
    public float[] vertexData = {
            -1f, -1f,
            1f, -1f,
            -1f, 1f,
            1f, 1f
    };


    public float[] fragmentData = {

            0f, 1f,
            1f, 1f,
            0f, 0f,
            1f, 0f
    };

    private int umatrix;
    protected FloatBuffer vertexBuffer ;

    protected FloatBuffer fragmentBuffer;

    public FloatBuffer getVertexBuffer() {
        return vertexBuffer;
    }

    public FloatBuffer getFragmentBuffer() {
        return fragmentBuffer;
    }

    public void setFilterChange(boolean filterChange) {
        this.filterChange = filterChange;
    }

    public boolean isFilterChange() {
        return filterChange;
    }

    public int getvPosition() {
        return vPosition;
    }

    public int getfPosition() {
        return fPosition;
    }

    public int getUmatrix() {
        return umatrix;
    }

    public boolean isMedia() {
        return isMedia;
    }

    public int program ;

    public void setProgram(int program) {
        this.program = program;
    }

    public void setIsMedia(boolean is) {
        isMedia = is;
    }

    public void init(){
        if (isFilterChange()) return;
        String vertexSource = getVertexSource();
        String fragmentSource = getFragmentSource();
        program = ShaderUtils.createProgram(vertexSource, fragmentSource);
        vPosition = GLES20.glGetAttribLocation(program, "v_Position");
        fPosition = GLES20.glGetAttribLocation(program, "f_Position");
        if (isMedia) {
            umatrix = GLES20.glGetUniformLocation(program, "u_Matrix");
        }
    }

    public void update(){

    }


    public int getProgram() {
        return program;
    }

    public BaseGPUImageFilter(Context context) {
        this.context = context;
        // 获取顶点和纹理buffer
        vertexBuffer = ShaderUtils.allocateBuffer(vertexData);

        fragmentBuffer = ShaderUtils.allocateBuffer(fragmentData);
    }

    public String getVertexSource(){
        return  ShaderUtils.getRawResource(context, isMedia ? R.raw.vertex_shader_camera : R.raw.vertex_shader );
    }

    public String getFragmentSource(){
        return ShaderUtils.getRawResource(context, isMedia ? R.raw.fragment_shader_camera : R.raw.fragment_shader);
    }

    public int getTexture(){
        return -1;
    }
}
