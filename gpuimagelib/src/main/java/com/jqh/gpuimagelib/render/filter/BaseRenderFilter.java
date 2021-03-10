package com.jqh.gpuimagelib.render.filter;

import android.content.Context;
import android.opengl.GLES20;

import com.jqh.gpuimagelib.R;
import com.jqh.gpuimagelib.opengl.ShaderUtils;
import com.jqh.gpuimagelib.utils.VertexUtils;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

public class BaseRenderFilter {

    private boolean inited = false;


    private static final String BASE_KEY = "basekey";

    private List<VertexDataBean> vertextDataList;

    public boolean isInited() {
        return inited;
    }

    protected Context context;

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
        String key = BASE_KEY;
        float[] vertex = VertexUtils.getInitData();
        VertexDataBean vertexDataBean = new VertexDataBean(key, vertex);
        vertextDataList = new ArrayList<>();
        vertextDataList.add(vertexDataBean);
        vertexData = VertexUtils.converToVextureData(vertextDataList);
        vertexBuffer = ShaderUtils.allocateBuffer(vertexData);
        fragmentBuffer = ShaderUtils.allocateBuffer(fragmentData);
        inited = false;
    }

    public void init(){
        if (inited) return;
        program = ShaderUtils.createProgram(getVertexSource(), getFragmentSource());
        vPosition = GLES20.glGetAttribLocation(program, "v_Position");
        fPosition = GLES20.glGetAttribLocation(program, "f_Position");
        inited = true;
    }

    public void update(){

    }


    protected String getVertexSource(){
        return  ShaderUtils.getRawResource(context,  R.raw.vertex_shader );
    }

    protected String getFragmentSource(){
        return ShaderUtils.getRawResource(context, R.raw.fragment_shader);
    }

    public void addVertexData(String key, float[] vertexData) {
        VertexDataBean vertexDataBean = new VertexDataBean(key, vertexData);
        vertextDataList.add(vertexDataBean);
        vertexData = VertexUtils.converToVextureData(vertextDataList);
        vertexBuffer = ShaderUtils.allocateBuffer(vertexData);
        inited = false;
    }

}
