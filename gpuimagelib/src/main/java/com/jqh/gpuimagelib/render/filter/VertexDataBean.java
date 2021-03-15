package com.jqh.gpuimagelib.render.filter;

public class VertexDataBean {
    private String key;
    private float[] vertex = {
            -1f, -1f,
            1f, -1f,
            -1f, 1f,
            1f, 1f,
    };

    public VertexDataBean(String key, float[] vertex) {
        this.key = key;
        this.vertex = vertex;
    }

    public String getKey() {
        return key;
    }

    public float[] getVertex() {
        return vertex;
    }

    public void setVertex(float[] vertex) {
        this.vertex = vertex;
    }
}
