package com.jqh.gpuimagelib.render;

import android.content.Context;
import android.opengl.GLES20;
import android.text.TextUtils;
import android.util.Log;

import com.jqh.gpuimagelib.opengl.ShaderUtils;
import com.jqh.gpuimagelib.render.filter.BaseGPUImageFilter;
import com.jqh.gpuimagelib.render.textrue.BaseTexture;
import com.jqh.gpuimagelib.utils.RenderUtils;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

// 通用渲染器
public class CommonFboRender {
    private Context context;

    private int vboId = 0;

    private BaseGPUImageFilter baseRenderFilter;

    private List<BaseTexture> baseTextureList = new CopyOnWriteArrayList<>();

    private int width = 0, height = 0;

    public void init(Context context){
        this.context = context;
        baseRenderFilter = new BaseGPUImageFilter(context);
    }

    public void onCreate(){
        baseRenderFilter.init();
        vboId = RenderUtils.createVBOId(baseRenderFilter.getVertexData(), baseRenderFilter.getFragmentData(),
                baseRenderFilter.getVertexBuffer(), baseRenderFilter.getFragmentBuffer());
    }

    public void onDraw(int textureId){

        if (!baseRenderFilter.isInited()) {
            onCreate();
        }
//        // 11 使用源程序
        GLES20.glUseProgram(baseRenderFilter.getProgram());
        // 绑定vbo
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboId);

        //  绘制FBO纹理
        // 重新绑定纹理, 绑定到imgTextureId，然帧缓存来处理
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        baseRenderFilter.update();

        baseRenderFilter.onOutputSizeChanged(width, height);

        // 16 绘制图形
//        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4); // 最后一个参数设置绘制几个点
        ShaderUtils.renderTexture(baseRenderFilter.getvPosition(), baseRenderFilter.getfPosition(), baseRenderFilter.getVertexData(), 0);
        Log.d("ondraw--", "vertexData size" + baseRenderFilter.getVertexData().length);
        for (int i = 0 ; i < baseTextureList.size(); i++) {
            BaseTexture baseTexture = baseTextureList.get(i);
            baseTexture.init();
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, baseTexture.getTextureId());
            ShaderUtils.renderTexture(baseRenderFilter.getvPosition(), baseRenderFilter.getfPosition(), baseRenderFilter.getVertexData(), i + 1);
        }

        //  解除绑定纹理
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

        // 解除vbo绑定
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
    }

    public void setFilter(BaseGPUImageFilter baseRenderFilter) {
        this.baseRenderFilter = baseRenderFilter;
        this.baseRenderFilter.addVertextDataList(baseTextureList);
    }

    public void addTexture(BaseTexture baseTexture) {
        baseTextureList.add(baseTexture);
        baseTexture.updateScreenWH(width, height);
        baseRenderFilter.addVertexData(baseTexture.getId(), baseTexture.getVertexData());
    }

    public void removeTexture(String id) {
        for (BaseTexture baseTexture : baseTextureList) {
            if (TextUtils.equals(id, baseTexture.getId())) {
                baseTextureList.remove(baseTexture);
                baseRenderFilter.removeVertextData(id);
                return;
            }
        }
    }

    public void setWH(int w , int h) {
        width = w;
        height = h;
    }

    public void updateTexture(String id, float left, float top, float scale) {
        BaseTexture texture = getTexture(id);
        if (texture == null) return;
        texture.updateTexture(left, top, scale);
        texture.updateSize();
        baseRenderFilter.updateVertexData(id, texture.getVertexData());
    }

    public BaseTexture getTexture(String id) {
        for (BaseTexture baseTexture : baseTextureList) {
            if (TextUtils.equals(id, baseTexture.getId())) {
                return baseTexture;
            }
        }
        return null;
    }

    public void isDetectorFace(boolean isDetecotr) {

    }
}
