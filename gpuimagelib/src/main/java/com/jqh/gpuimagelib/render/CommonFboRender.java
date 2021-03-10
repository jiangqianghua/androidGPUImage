package com.jqh.gpuimagelib.render;

import android.content.Context;
import android.opengl.GLES20;

import com.jqh.gpuimagelib.opengl.ShaderUtils;
import com.jqh.gpuimagelib.render.filter.BaseRenderFilter;
import com.jqh.gpuimagelib.render.textrue.BaseTexture;
import com.jqh.gpuimagelib.utils.RenderUtils;

import java.util.ArrayList;
import java.util.List;

// 通用渲染器
public class CommonFboRender {
    private Context context;

    private int vboId = 0;

    private BaseRenderFilter baseRenderFilter;

    private List<BaseTexture> baseTextureList = new ArrayList<>();

    public void init(Context context){
        this.context = context;
        baseRenderFilter = new BaseRenderFilter(context);
    }

    public void onCreate(){
        vboId = RenderUtils.createVBOId(baseRenderFilter.getVertexData(), baseRenderFilter.getFragmentData(),
                baseRenderFilter.getVertexBuffer(), baseRenderFilter.getFragmentBuffer());

        baseRenderFilter.init();
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

        // 12 使得顶点属性数组有效
        GLES20.glEnableVertexAttribArray(baseRenderFilter.getvPosition());
        // 13 为顶点属性赋值
        GLES20.glVertexAttribPointer(baseRenderFilter.getvPosition(), 2, GLES20.GL_FLOAT, false, 8 , 0);


        // 14 使得纹理属性数组有效
        GLES20.glEnableVertexAttribArray(baseRenderFilter.getfPosition());
        // 15  为纹理属性赋值
        GLES20.glVertexAttribPointer(baseRenderFilter.getfPosition(), 2, GLES20.GL_FLOAT, false, 8 , baseRenderFilter.getVertexData().length * 4);

        // 16 绘制图形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4); // 最后一个参数设置绘制几个点


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

    public void setFilter(BaseRenderFilter baseRenderFilter) {
        this.baseRenderFilter = baseRenderFilter;
    }

    public void addTexture(BaseTexture baseTexture) {
        baseTextureList.add(baseTexture);
        baseRenderFilter.addVertexData(baseTexture.getId(), baseTexture.getVertexData());
    }
}
