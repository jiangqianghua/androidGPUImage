package com.jqh.gpuimagelib.camera;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.jqh.gpuimagelib.R;
import com.jqh.gpuimagelib.opengl.GLSurfaceView;
import com.jqh.gpuimagelib.opengl.ShaderUtils;
import com.jqh.gpuimagelib.utils.DisplayUtil;

import java.nio.FloatBuffer;

public class GPUCameraRender  implements GLSurfaceView.GLRender, SurfaceTexture.OnFrameAvailableListener {
    private Context context;


    // 多绘制，填入两次坐标
    private float[] vertexData = {
            -1f, -1f,
            1f, -1f,
            -1f, 1f,
            1f, 1f
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

    private int cameraTextureId; //摄像头扩展纹理
    private SurfaceTexture surfaceTexture;

    private int umatrix;
    private float[] matrix = new float[16];

    private int screenWidth ,screenHeight;

    private int width, height; // 实际surface大小


    public GPUCameraRender(Context context) {
        this.context = context;

        // 获取顶点和纹理buffer
        vertexBuffer = ShaderUtils.allocateBuffer(vertexData);
        fragmentBuffer = ShaderUtils.allocateBuffer(fragmentData);

        screenHeight = DisplayUtil.getScreenHeight(context);
        screenWidth = DisplayUtil.getScreenWidth(context);

    }

    private void initRender(){
        String vertexSource = ShaderUtils.getRawResource(context, R.raw.vertex_shader_camera);
        String fragmentSource = ShaderUtils.getRawResource(context, R.raw.vertex_shader_camera);
        program = ShaderUtils.createProgram(vertexSource, fragmentSource);
        vPosition = GLES20.glGetAttribLocation(program, "v_Position");
        fPosition = GLES20.glGetAttribLocation(program, "f_Position");
        umatrix = GLES20.glGetUniformLocation(program, "u_Matrix");
    }
    public void reSetMatrix(){
        Matrix.setIdentityM(matrix, 0);
    }

    public void setAngle(float angle, float x, float y ,float z){
        Matrix.rotateM(matrix, 0 ,angle, x, y ,z);
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {

    }

    @Override
    public void onSurfaceCreate() {
        initRender();
        // 创建摄像头扩展纹理
        int[] textureidseos =new int[1];
        GLES20.glGenTextures(1, textureidseos, 0);
        cameraTextureId = textureidseos[0];

        // 绑定扩展纹理
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, cameraTextureId);
        // 设置纹理环绕方式
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
        // 设置纹理过滤方式
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        // 把纹理设置到surface中
        surfaceTexture = new SurfaceTexture(cameraTextureId);
        //当有数据后，会回调监听onFrameAvailable
        surfaceTexture.setOnFrameAvailableListener(this);
        // 把surfaceTexture 回调给外层
        if (onSurfaceCreateListener != null) onSurfaceCreateListener.onSurfaceCreate(surfaceTexture, 0);
        //  解除cameraTextureId绑定
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public void onDrawFrame() {
        surfaceTexture.updateTexImage();
        ShaderUtils.clearScreenDefault();
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, cameraTextureId);

        GLES20.glUseProgram(program);
        GLES20.glViewport(0,0, screenWidth, screenHeight);
        //使用矩阵变换
        GLES20.glUniformMatrix4fv(umatrix, 1, false, matrix, 0);
        ShaderUtils.renderTexture(vPosition, fPosition, vertexBuffer, fragmentBuffer);
        //  解除绑定纹理
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
    }



    private OnSurfaceCreateListener onSurfaceCreateListener;

    public void setOnSurfaceCreateListener(OnSurfaceCreateListener onSurfaceCreateListener) {
        this.onSurfaceCreateListener = onSurfaceCreateListener;
    }

    public interface  OnSurfaceCreateListener{
        void onSurfaceCreate(SurfaceTexture surfaceTexture, int textureId);
    }
}
