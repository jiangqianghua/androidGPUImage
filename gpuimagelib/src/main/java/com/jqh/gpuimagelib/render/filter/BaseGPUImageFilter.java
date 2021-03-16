package com.jqh.gpuimagelib.render.filter;

import android.content.Context;
import android.graphics.PointF;
import android.opengl.GLES20;
import android.text.TextUtils;
import android.util.Log;

import com.jqh.gpuimagelib.R;
import com.jqh.gpuimagelib.opengl.ShaderUtils;
import com.jqh.gpuimagelib.utils.LogUtils;
import com.jqh.gpuimagelib.utils.VertexUtils;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class BaseGPUImageFilter {

    protected boolean inited = false;

    protected boolean isNeedUpdate = true;

    protected  boolean isNeedChangeWH = true;


    private static final String BASE_KEY = "basekey";

    private List<VertexDataBean> vertextDataList;

    public boolean isInited() {
        return inited;
    }

    protected Context context;

    // 绘制上半部分
    private float[] vertexData ;
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

    public BaseGPUImageFilter(Context context) {
        this.context = context;
        String key = BASE_KEY;
        float[] vertex = VertexUtils.getInitData();
        VertexDataBean vertexDataBean = new VertexDataBean(key, vertex);
        vertextDataList = new CopyOnWriteArrayList<>();
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
        LogUtils.logd("init base program=" + program + " vp=" + vPosition + " fp=" + fPosition);
    }

    public void update(){
        isNeedUpdate = false;
    }

    protected String getVertexSource(){
        return  ShaderUtils.getRawResource(context,  R.raw.vertex_shader );
    }

    protected String getFragmentSource(){
        return ShaderUtils.getRawResource(context, R.raw.fragment_shader);
    }

    public void addVertexData(String key, float[] _vertexData) {
        VertexDataBean vertexDataBean = new VertexDataBean(key, _vertexData);
        vertextDataList.add(vertexDataBean);
        updateData();
        Log.d("ondraw--", "add ++vertexData size" + getVertexData().length);
    }

    public void updateVertexData(String key, float[] _vertexData) {
        VertexDataBean vertexDataBean = getVertexBean(key);
        if (vertexDataBean == null) return;
        vertexDataBean.setVertex(_vertexData);
        updateData();
    }

    public void removeVertextData(String key) {

        for (VertexDataBean vertexDataBean : vertextDataList) {
            if (TextUtils.equals(vertexDataBean.getKey(), key)){
                vertextDataList.remove(vertexDataBean);
                return;
            }
        }
        updateData();
        Log.d("ondraw--", "add --vertexData size" + getVertexData().length);
    }

    private void updateData(){
        vertexData = VertexUtils.converToVextureData(vertextDataList);
        vertexBuffer = ShaderUtils.allocateBuffer(vertexData);
        inited = false;
    }

    public void onOutputSizeChanged(int width, int height) {
        isNeedChangeWH = false;
    }

    public VertexDataBean getVertexBean(String id) {
        for (VertexDataBean vertexDataBean : vertextDataList) {
            if (TextUtils.equals(vertexDataBean.getKey(), id)){
                return vertexDataBean;
            }
        }
        return null;
    }

    public void setNeedUpdate() {
        isNeedUpdate = true;
    }

    protected void setInteger(final int location, final int intValue) {
        GLES20.glUniform1i(location, intValue);
    }

    protected void setFloat(final int location, final float floatValue) {
        GLES20.glUniform1f(location, floatValue);
    }

    protected void setFloatVec2(final int location, final float[] arrayValue) {
        GLES20.glUniform2fv(location, 1, FloatBuffer.wrap(arrayValue));
    }

    protected void setFloatVec3(final int location, final float[] arrayValue) {
        GLES20.glUniform3fv(location, 1, FloatBuffer.wrap(arrayValue));
    }

    protected void setFloatVec4(final int location, final float[] arrayValue) {
        GLES20.glUniform4fv(location, 1, FloatBuffer.wrap(arrayValue));
    }

    protected void setFloatArray(final int location, final float[] arrayValue) {
        GLES20.glUniform1fv(location, arrayValue.length, FloatBuffer.wrap(arrayValue));
    }

    protected void setPoint(final int location, final PointF point) {
        float[] vec2 = new float[2];
        vec2[0] = point.x;
        vec2[1] = point.y;
        GLES20.glUniform2fv(location, 1, vec2, 0);
    }

    protected void setUniformMatrix3f(final int location, final float[] matrix) {
        GLES20.glUniformMatrix3fv(location, 1, false, matrix, 0);
    }

    protected void setUniformMatrix4f(final int location, final float[] matrix) {
        GLES20.glUniformMatrix4fv(location, 1, false, matrix, 0);
    }


}
