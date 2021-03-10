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
}
