package com.jqh.gpuimagelib.utils;

import android.opengl.GLES20;
import android.util.Log;

import java.nio.FloatBuffer;

public class RenderUtils {

    public static int createVBOId(float[] vertexData, float[] fragmentData,
                                  FloatBuffer vertexBuffer,FloatBuffer fragmentBuffer ){
        int [] vbos = new int[1];
        GLES20.glGenBuffers(1, vbos, 0);
        int vboId = vbos[0];
        // 绑定vbo的id
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboId);
        // 分配缓存大小
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertexData.length * 4 + fragmentData.length * 4, null, GLES20.GL_STATIC_DRAW);
        // 给分配的空间填充数据
        GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER, 0, vertexData.length * 4, vertexBuffer);
        GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER, vertexData.length * 4, fragmentData.length * 4, fragmentBuffer);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        return vboId;
    }

    public static int[] createFBO(int w, int h){

        // 利用fbo缓存帧数据
        int[] fbos = new int[1];
        GLES20.glGenBuffers(1, fbos, 0);
        int fboId = fbos[0];
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fboId);

        // 创建和绑定纹理
        int[] textureIds = new int[1];
        GLES20.glGenTextures(1, textureIds, 0);
        int fboTextureId = textureIds[0];
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, fboTextureId);

        // 设置纹理环绕方式
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
        // 设置纹理过滤方式
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);


        //  设置fbo大小
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, w, h, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);

        // 把纹理绑定到帧缓存上, 后面对fbo的操作就是对纹理的操作
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, fboTextureId, 0);

        if(GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER) != GLES20.GL_FRAMEBUFFER_COMPLETE){
            Log.e("JqhTextureRender", "fbo wrong");
        } else {
            Log.e("JqhTextureRender", "fbo success");
        }
        // 解绑纹理
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        // 解绑帧缓存
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        return new int[]{fboId, fboTextureId};
    }


}
