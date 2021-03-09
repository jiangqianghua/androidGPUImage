package com.jqh.gpuimagelib.camera;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.jqh.gpuimagelib.filter.BaseGPUImageFilter;
import com.jqh.gpuimagelib.opengl.GLSurfaceView;
import com.jqh.gpuimagelib.opengl.ShaderUtils;
import com.jqh.gpuimagelib.utils.DisplayUtil;
import com.jqh.gpuimagelib.utils.RenderUtils;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class GPUCameraRender1 implements GLSurfaceView.GLRender, SurfaceTexture.OnFrameAvailableListener {
    private Context context;

    private List<BaseGPUImageFilter> filterList;


    private int cameraTextureId; //摄像头扩展纹理
    private SurfaceTexture surfaceTexture;


    private float[] matrix = new float[16];

    private int screenWidth ,screenHeight;

    private int width, height; // 实际surface大小

    private BaseGPUImageFilter baseGPUImageFilter;

    private int vboId;
    private int fboId;
    private int fboTextureId; //离屏渲染纹理
    private GPUCameraFboRender cameraFboRender;

    public GPUCameraRender1(Context context) {
        this.context = context;
        cameraFboRender = new GPUCameraFboRender(context);

        filterList = new CopyOnWriteArrayList();
        baseGPUImageFilter = new BaseGPUImageFilter(context);
        baseGPUImageFilter.setIsMedia(true);
        filterList.add(baseGPUImageFilter);
        screenHeight = DisplayUtil.getScreenHeight(context);
        screenWidth = DisplayUtil.getScreenWidth(context);

    }

    private void initRender(BaseGPUImageFilter filter){

        filter.init();

        filter.setFilterChange(false);
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
        cameraFboRender.onCreate();
        for (BaseGPUImageFilter filter : filterList) {
            initRender(filter);
        }

        vboId = RenderUtils.createVBOId(baseGPUImageFilter.vertexData, baseGPUImageFilter.fragmentData,
                baseGPUImageFilter.getVertexBuffer(), baseGPUImageFilter.getFragmentBuffer());

        int[] fboData = RenderUtils.createFBO(screenWidth, screenHeight);
        fboId = fboData[0];
        fboTextureId = fboData[1];

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
        if (onSurfaceCreateListener != null) onSurfaceCreateListener.onSurfaceCreate(surfaceTexture, fboTextureId);
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
//        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, cameraTextureId);

        GLES20.glViewport(0,0, screenWidth, screenHeight);
        for (BaseGPUImageFilter filter : filterList) {
            if (filter.isFilterChange()){
                initRender(filter);
            }

            GLES20.glUseProgram(filter.program);

            //使用矩阵变换
            GLES20.glUniformMatrix4fv(filter.getUmatrix(), 1, false, matrix, 0);
            // 绑定fbo
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fboId);

            // 绑定vbo
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboId);


            filter.update();

            ShaderUtils.renderTexture(filter.getvPosition(), filter.getfPosition(), filter.vertexData);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
            // 解除vbo绑定
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

            // fbo使用代码
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

            cameraFboRender.onChange(width, height);
            cameraFboRender.onDraw(fboTextureId);
        }



        //  解除绑定纹理
//        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
    }



    private OnSurfaceCreateListener onSurfaceCreateListener;

    public void setOnSurfaceCreateListener(OnSurfaceCreateListener onSurfaceCreateListener) {
        this.onSurfaceCreateListener = onSurfaceCreateListener;
    }

    public interface  OnSurfaceCreateListener{
        void onSurfaceCreate(SurfaceTexture surfaceTexture, int textureId);
    }

    public void addFilter(BaseGPUImageFilter filter) {
//        filterList.add(filter);
//        filter.setIsMedia(true);

        cameraFboRender.addFilter(filter);
    }
}
