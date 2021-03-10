package com.jqh.gpuimagelib.opengl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * shader 工具类
 */
public class ShaderUtils {
    private static final String TAG = "ShaderUtils";

    //  获取raw资源内容
    public static String getRawResource(Context context , int rawId) {
        InputStream inputStream = context.getResources().openRawResource(rawId);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuffer sb = new StringBuffer();
        String line ;
        try {

            while((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            reader.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    // 加载着色器
    private static int loadShader(int shaderType, String source){
        // 1 创建着色器Shader
        int shader = GLES20.glCreateShader(shaderType);
        if (shader != 0) {
            //  2 加载shader并编译
            GLES20.glShaderSource(shader, source);
            GLES20.glCompileShader(shader);

            //3  验证脚本编译代码是否有错误
            int[] compile = new int[1];
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compile, 0);
            if (compile[0] != GLES20.GL_TRUE) {
                Log.d(TAG, "shader compile error");
                GLES20.glDeleteShader(shader);
                shader = 0;
            }
            return shader;
        } else {
            return 0;
        }
    }

    public static int createProgram(String vertexSource, String fragmentSource) {
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);

        if (vertexShader !=0 && fragmentShader != 0) {
            // 4 创建一个渲染程序
            int program = GLES20.glCreateProgram();
            // 5 将着色器添加到渲染程序中
            GLES20.glAttachShader(program, vertexShader);
            GLES20.glAttachShader(program, fragmentShader);
            // 6 链接源程序
            GLES20.glLinkProgram(program);
            return program;
        }
        return 0;
    }

    // 创建文字的bitmap
    public static Bitmap createTextImage(String text, int textSize, String textColor, String bgColor, int padding) {

        Paint paint = new Paint();
        paint.setColor(Color.parseColor(textColor));
        paint.setTextSize(textSize);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);

        float width = paint.measureText(text, 0, text.length());

        float top = paint.getFontMetrics().top;
        float bottom = paint.getFontMetrics().bottom;

        Bitmap bm = Bitmap.createBitmap((int) (width + padding * 2), (int) ((bottom - top) + padding * 2), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bm);

        canvas.drawColor(Color.parseColor(bgColor));
        canvas.drawText(text, padding, - top + padding, paint);
        return bm;
    }

    // 根据bitmap加载纹理
    public static int loadBitmapTexture(Bitmap bitmap)
    {
        int[] textureIds = new int[1];
        GLES20.glGenTextures(1, textureIds, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureIds[0]);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

        ByteBuffer bitmapBuffer = ByteBuffer.allocate(bitmap.getHeight() * bitmap.getWidth() * 4);
        bitmap.copyPixelsToBuffer(bitmapBuffer);
        bitmapBuffer.flip();

        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, bitmap.getWidth(),
                bitmap.getHeight(), 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, bitmapBuffer);
        return textureIds[0];
    }

    // 根据资源id加载纹理
    public static int loadTextrue(Context context ,int src){
        // 创建和绑定纹理
        int[] textureIds = new int[1];
        GLES20.glGenTextures(1, textureIds, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureIds[0]);

        // 设置纹理环绕方式
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
        // 设置纹理过滤方式
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), src);

        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        bitmap.recycle();
        bitmap = null;

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        return textureIds[0];
    }
    // 给坐标点分配内存
    public static FloatBuffer allocateBuffer(float []data) {
        FloatBuffer buffer = ByteBuffer.allocateDirect(data.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(data);
        buffer.position(0);
        return buffer;
    }


    // 使用默认方式清理屏幕
    public static void clearScreenDefault(){
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glClearColor(1f,0f, 0f, 1f);
    }

    public static void renderTexture( int vPosition, int fPosition, FloatBuffer vertexBuffer, FloatBuffer fragmentBuffer) {


        // 12 使得顶点属性数组有效
        GLES20.glEnableVertexAttribArray(vPosition);
        // 13 为顶点属性赋值
        GLES20.glVertexAttribPointer(vPosition, 2, GLES20.GL_FLOAT, false, 8 , vertexBuffer);

        // 14 使得纹理属性数组有效
        GLES20.glEnableVertexAttribArray(fPosition);
        // 15  为纹理属性赋值
        GLES20.glVertexAttribPointer(fPosition, 2, GLES20.GL_FLOAT, false, 8 , fragmentBuffer);

        // 16 绘制图形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4); // 最后一个参数设置绘制几个点
    }


    public static void renderTexture( int vPosition, int fPosition, float[] vertexData) {


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
    }

    public static void renderTexture( int vPosition, int fPosition, float[] vertexData, int index) {


        // 12 使得顶点属性数组有效
        GLES20.glEnableVertexAttribArray(vPosition);
        // 13 为顶点属性赋值
        GLES20.glVertexAttribPointer(vPosition, 2, GLES20.GL_FLOAT, false, 8 , index * 4 * 8);



        // 14 使得纹理属性数组有效
        GLES20.glEnableVertexAttribArray(fPosition);
        // 15  为纹理属性赋值
        GLES20.glVertexAttribPointer(fPosition, 2, GLES20.GL_FLOAT, false, 8 , vertexData.length * 4);

        // 16 绘制图形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4); // 最后一个参数设置绘制几个点
    }

}
