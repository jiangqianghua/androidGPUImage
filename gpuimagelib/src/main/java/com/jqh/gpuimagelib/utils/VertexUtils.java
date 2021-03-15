package com.jqh.gpuimagelib.utils;

import com.jqh.gpuimagelib.render.filter.VertexDataBean;

import java.util.List;

public class VertexUtils {

    public static float[] converToVextureData(List<VertexDataBean> list) {
        float[] vertextData = new float[list.size() * 8];
        int i = 0 ;
        for (VertexDataBean vertexDataBean : list) {
            for (float val: vertexDataBean.getVertex()) {
                vertextData[i] = val;
                i++;
            }
        }
        return vertextData;
    }

    public static float[] getInitData(){
        float[] vertex = {
                -1f, -1f,
                1f, -1f,
                -1f, 1f,
                1f, 1f,
        };
        return vertex;
    }

    public static float[] getInitData1(){
        float[] vertex = {
                0f, 0f,
                -0f, 0f,
                0f, -0f,
                -0f, -0f,
        };
        return vertex;
    }

    public static float[] createData(float left, float top, float w, float h, int screenW, int screenH) {
        float[] vertex = {
                -1f, -1f,
                1f, -1f,
                -1f, 1f,
                1f, 1f,
        };
        // 左上
        vertex[4] = getX(left, screenW);
        vertex[5] = getY(top, screenH);

        vertex[6] = getX(left + w, screenW);
        vertex[7] = getY(top, screenH);

        vertex[0] = getX(left, screenW);
        vertex[1] = getY(top + h, screenH);

        vertex[2] = getX(left + w, screenW);
        vertex[3] = getY(top + h, screenH);

        return vertex;
    }

    public static float getX(float left, int screenW) {
        float x = 0f;
        if (left <= screenW/2) {
            x = -(1 - (left/(screenW/2.0f)));
        } else {
            x = (left - screenW/2.0f)/(screenW/2.0f);
        }
        return x;
    }

    public static float getY(float top, int screenH) {
        float y = 0f;
        if (top <= screenH/2) {
            y = 1 - top / (screenH/2.0f);
        } else {
            y = - (top - screenH/2.0f)/(screenH/2.0f);
        }
        return y;
    }

}
