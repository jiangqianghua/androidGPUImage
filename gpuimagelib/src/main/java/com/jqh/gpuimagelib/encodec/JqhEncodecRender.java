package com.jqh.gpuimagelib.encodec;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;

import com.jqh.gpuimagelib.R;
import com.jqh.gpuimagelib.opengl.GLSurfaceView;
import com.jqh.gpuimagelib.opengl.ShaderUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class JqhEncodecRender implements GLSurfaceView.GLRender {

    private Context context;
    // 绘制上半部分
    private float[] vertexData = {
            -1f, -1f,
            1f, -1f,
            -1f, 1f,
            1f, 1f,

            // 先占空位置，后面计算
            0f, 0f,
            0f, 0f,
            0f, 0f,
            0f, 0f
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

    private int textureId; // 纹理id


    // 顶点缓存数据
    private int vboId;

    // 水印的bitmap
    private Bitmap bitmap;

    // 水印纹理
    private int bitmapTextureid;

    public JqhEncodecRender(Context context, int textureid) {
        this.context = context;
        this.textureId = textureid;

        // 加载文字纹理
        bitmap = ShaderUtils.createTextImage("我爱熊毛毛", 50, "#ff0000", "#00000000", 0);
        float r = 1.0f * bitmap.getWidth() / bitmap.getHeight();
        float h = 0.1f;
        float w = r * h;
        // 确定0.8位置
        // 开始填充坐标
        vertexData[8] = 0.8f - w;
        vertexData[9] = -0.8f;

        vertexData[10] = 0.8f;
        vertexData[11] = -0.8f;

        vertexData[12] = 0.8f - w;
        vertexData[13] = -0.7f;

        vertexData[14] = 0.8f;
        vertexData[15] = -0.7f;

        // 分配本地内存
        vertexBuffer = ByteBuffer.allocateDirect(vertexData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertexData);
        vertexBuffer.position(0);

        fragmentBuffer = ByteBuffer.allocateDirect(fragmentData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(fragmentData);
        fragmentBuffer.position(0);

    }

    @Override
    public void onSurfaceCreate() {
        GLES20.glEnable (GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        String vertexSource = ShaderUtils.getRawResource(context, R.raw.vertex_shader_camera);
        String fragmentSource = ShaderUtils.getRawResource(context, R.raw.fragment_shader_camera);

        program = ShaderUtils.createProgram(vertexSource, fragmentSource);

        // 10 得到着色器中的属性
        vPosition = GLES20.glGetAttribLocation(program, "v_Position");
        fPosition = GLES20.glGetAttribLocation(program, "f_Position");


        // 利用vbo缓存顶点数据
        int [] vbos = new int[1];
        GLES20.glGenBuffers(1, vbos, 0);
        vboId = vbos[0];
        // 绑定vbo的id
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboId);
        // 分配缓存大小
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertexData.length * 4 + fragmentData.length * 4, null, GLES20.GL_STATIC_DRAW);
        // 给分配的空间填充数据
        GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER, 0, vertexData.length * 4, vertexBuffer);
        GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER, vertexData.length * 4, fragmentData.length * 4, fragmentBuffer);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

        bitmapTextureid = ShaderUtils.loadBitmapTexture(bitmap);
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        GLES20.glViewport(0,0, width, height);
    }

    @Override
    public void onDrawFrame() {
        // 用颜色刷新
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glClearColor(1f, 0f, 0f,1f);
        // 11 使用源程序
        GLES20.glUseProgram(program);

        // 绑定vbo
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboId);
        // 重新绑定纹理, 绑定到imgTextureId，然帧缓存来处理
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);


        // 12 使得顶点属性数组有效
        GLES20.glEnableVertexAttribArray(vPosition);
        // 13 为顶点属性赋值
        GLES20.glVertexAttribPointer(vPosition, 2, GLES20.GL_FLOAT, false, 8 , 0);


        // 14 使得纹理属性数组有效
        GLES20.glEnableVertexAttribArray(fPosition);
        // 15  为纹理属性赋值
        GLES20.glVertexAttribPointer(fPosition, 2, GLES20.GL_FLOAT, false, 8 , vertexData.length * 4);

        // 16 绘制图形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4); // 最后一个参数设置绘制几个点


        // 绘制水印纹理
        // 重新绑定纹理, 绑定到imgTextureId，然帧缓存来处理
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, bitmapTextureid);

        // 12 使得顶点属性数组有效
        GLES20.glEnableVertexAttribArray(vPosition);
        // 13 为顶点属性赋值
        GLES20.glVertexAttribPointer(vPosition, 2, GLES20.GL_FLOAT, false, 8 , 32); //4 * 8
        // 14 使得纹理属性数组有效
        GLES20.glEnableVertexAttribArray(fPosition);
        // 15  为纹理属性赋值
        GLES20.glVertexAttribPointer(fPosition, 2, GLES20.GL_FLOAT, false, 8 , vertexData.length * 4);
        // 16 绘制图形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4); // 最后一个参数设置绘制几个点




        //  解除绑定纹理
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

        // 解除vbo绑定
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
    }
}
