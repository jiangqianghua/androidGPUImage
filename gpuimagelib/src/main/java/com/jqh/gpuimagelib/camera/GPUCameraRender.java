package com.jqh.gpuimagelib.camera;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import com.jqh.gpuimagelib.listener.OnDetectorFaceListener;
import com.jqh.gpuimagelib.opengl.GLSurfaceView;
import com.jqh.gpuimagelib.opengl.ShaderUtils;
import com.jqh.gpuimagelib.render.filter.BaseGPUImageFilter;
import com.jqh.gpuimagelib.render.textrue.BaseTexture;
import com.jqh.gpuimagelib.utils.CachedThreadPool;
import com.jqh.gpuimagelib.utils.DisplayUtil;
import com.jqh.gpuimagelib.utils.FaceUtils;
import com.jqh.gpuimagelib.utils.ImageUtils;
import com.jqh.gpuimagelib.utils.LogUtils;
import com.jqh.gpuimagelib.utils.RenderUtils;

import javax.microedition.khronos.opengles.GL10;

public class GPUCameraRender implements GLSurfaceView.GLRender, SurfaceTexture.OnFrameAvailableListener {
    private Context context;


    private int cameraTextureId; //摄像头扩展纹理
    private SurfaceTexture surfaceTexture;


    private float[] matrix = new float[16];

    private int screenWidth ,screenHeight;

    private int width, height; // 实际surface大小


    private GL10 gl;
    private int vboId;
    private int fboId;
    private int fboTextureId; //离屏渲染纹理
    private GPUCameraFboRender cameraFboRender;

    private MediaGPUImageFilter baseGPUImageFilter;

    private boolean isTakePicture = false;

    private boolean isDetectorFace = false;

    private int detectorInterval = 1;
    private long lastTime = 0;
    private boolean isLastDetecotrDone = true;

    private Handler mBackgroundHandler;

    private OnDetectorFaceListener onDetectorFaceListener;

    public void setOnDetectorFaceListener(OnDetectorFaceListener onDetectorFaceListener) {
        this.onDetectorFaceListener = onDetectorFaceListener;
    }

    public void setGl(GL10 gl) {
        this.gl = gl;
    }

    public GPUCameraRender(Context context) {
        this.context = context;
        cameraFboRender = new GPUCameraFboRender(context);
        baseGPUImageFilter = new MediaGPUImageFilter(context);
        baseGPUImageFilter.setIsMedia(true);
        screenHeight = DisplayUtil.getScreenHeight(context);
        screenWidth = DisplayUtil.getScreenWidth(context);

    }

    private void initRender(){

        baseGPUImageFilter.init();

        baseGPUImageFilter.setFilterChange(false);
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

        initRender();

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
        initRender();

        GLES20.glUseProgram(baseGPUImageFilter.program);

        //使用矩阵变换
        GLES20.glUniformMatrix4fv(baseGPUImageFilter.getUmatrix(), 1, false, matrix, 0);
        // 绑定fbo
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fboId);

        // 绑定vbo
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboId);


        baseGPUImageFilter.update();

        ShaderUtils.renderTexture(baseGPUImageFilter.getvPosition(), baseGPUImageFilter.getfPosition(), baseGPUImageFilter.vertexData);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        // 解除vbo绑定
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

        // fbo使用代码
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

        cameraFboRender.onChange(width, height);
        cameraFboRender.onDraw(fboTextureId);



        //  解除绑定纹理
//        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

        if (isTakePicture) {
            Bitmap bmp = RenderUtils.createBitmapFromGLSurface(0, 0, width,
                    height, gl);
            isTakePicture = false;
            if (onSurfaceCreateListener != null) onSurfaceCreateListener.onCreateBitmap(bmp);
        }
//        LogUtils.logd("开始检测人脸 isLastDetecotrDone=" + isLastDetecotrDone);
        if (isDetectorFace && isLastDetecotrDone) {
            boolean isNeedDetector = false;
            if (lastTime == 0) {
                lastTime = System.currentTimeMillis();
                isNeedDetector = true;
            } else {
                long now = System.currentTimeMillis();
                if (now - lastTime > detectorInterval) {
                    isNeedDetector = true;
                    lastTime = now;
                }
            }
            if (isNeedDetector) {
                detectorFace();

            }
        }
    }

    private synchronized Handler getBackgroundHandler() {
        if (mBackgroundHandler == null) {
            HandlerThread thread = new HandlerThread("background");
            thread.start();
            mBackgroundHandler = new Handler(thread.getLooper());
        }
        return mBackgroundHandler;
    }

    private void detectorFace() {
//        LogUtils.logd("开始检测人脸");
        final Bitmap bmp = RenderUtils.createBitmapFromGLSurface(0, 0, width,
                height, gl);
        getBackgroundHandler().post(new Runnable() {
            @Override
            public void run() {
                isLastDetecotrDone = false;
                LogUtils.logd("开始检测人脸- start");
                Bitmap bitmap = ImageUtils.matrix(bmp);
//                bitmap = ImageUtils.zeroAndOne(bitmap);
                LogUtils.logd("开始检测人脸 donging");
                RectF[] faces = FaceUtils.detecotrFace(bitmap);
                LogUtils.logd("开始检测人脸- end");
                for (int i = 0 ; i < faces.length; i++) {
                    RectF rectF = faces[i];
                    if (rectF == null) continue;
                    if (onDetectorFaceListener != null) onDetectorFaceListener.onDetectorRect(rectF, bitmap.getWidth(), bitmap.getHeight());
                    LogUtils.logd("开始检测人脸 left=" + rectF.left + " top=" + rectF.top + " righ=" + rectF.right + " bottom=" + rectF.bottom + " w=" + width + " h=" + height);
                }
                bmp.recycle();
                bitmap.recycle();
                isLastDetecotrDone = true;
            }
        });
//        CachedThreadPool.executorService.execute(new Runnable() {
//            @Override
//            public void run() {
//                isLastDetecotrDone = false;
//                RectF[] faces = FaceUtils.detecotrFace(bmp);
//                bmp.recycle();
//                for (int i = 0 ; i < faces.length; i++) {
//                    RectF rectF = faces[i];
//                    if (rectF == null) continue;
//                    LogUtils.logd("开始检测人脸 left=" + rectF.left + " top=" + rectF.top + " righ=" + rectF.right + " bottom=" + rectF.bottom);
//                }
//                isLastDetecotrDone = true;
//            }
//        });

    }

    public void takePhoto(){
        isTakePicture = true;
    }



    private OnSurfaceCreateListener onSurfaceCreateListener;

    public void setOnSurfaceCreateListener(OnSurfaceCreateListener onSurfaceCreateListener) {
        this.onSurfaceCreateListener = onSurfaceCreateListener;
    }

    public interface  OnSurfaceCreateListener{
        void onSurfaceCreate(SurfaceTexture surfaceTexture, int textureId);
        void onCreateBitmap(Bitmap bitmap);
    }

    public void addFilter(BaseGPUImageFilter filter) {
//        filterList.add(filter);
//        filter.setIsMedia(true);

        cameraFboRender.addFilter(filter);
    }

    public void addTexture(BaseTexture baseTexture) {
        if (baseTexture != null){
            cameraFboRender.addTexture(baseTexture);
        }
    }

    public void removeTexture(String key) {
        cameraFboRender.removeTexture(key);
    }

    public void updateTexture(String id, float left, float top, float scale) {
        cameraFboRender.updateTexture(id, left, top, scale);
    }

    public void isDetectorFace(boolean isDetecotr) {
        this.isDetectorFace = isDetecotr;
    }
}
