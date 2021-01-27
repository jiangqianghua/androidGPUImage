package com.jqh.gpuimagelib.opengl;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.lang.ref.WeakReference;

import javax.microedition.khronos.egl.EGLContext;

/**
 * 仿照系统自定义一个GLSurfaceView
 */
public class GLSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private Surface surface;
    private EGLContext eglContext ;

    private GLThread glThread;

    private GLRender glRender;

    public final static int RENDERMODE_WHEN_DIRTY = 0 ; // 手动刷新
    public final static int RENDERMODE_CONTINUOUSLY = 1; // 自动刷新

    private int renderMode = RENDERMODE_CONTINUOUSLY ;
    public GLSurfaceView(Context context) {
        this(context, null);
    }

    public GLSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GLSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getHolder().addCallback(this);
    }

    public void setRender(GLRender glRender) {
        this.glRender = glRender;
    }

    public void setRenderMode(int renderMode) {
        if (glRender == null) {
            throw new RuntimeException("must set redner before");

        }
        this.renderMode = renderMode;
    }

    public void setSurfaceAndEglContext(Surface surface, EGLContext eglContext) {
        this.surface = surface;
        this.eglContext = eglContext;
    }

    public EGLContext getEglContext(){
        if (glThread != null) {
            return glThread.getEglContext();
        }
        return null;
    }

    public void requestRender(){
        if (glThread != null) {
            glThread.requestRender();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (surface == null) {
            surface = holder.getSurface();
        }
        glThread = new GLThread(new WeakReference<GLSurfaceView>(this));
        glThread.isCreate = true;
        glThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        glThread.isChange = true;
        glThread.width = width;
        glThread.height = height;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        glThread.onDestory();
        eglContext = null;
        surface = null;
        eglContext = null;
    }

    public interface GLRender {
        void onSurfaceCreate();
        void onSurfaceChanged(int width, int height);
        void onDrawFrame();
    }

    static class GLThread extends Thread {
        private WeakReference<GLSurfaceView> glSurfaceViewWeakReference = null;

        private EglHelper eglHelper = null ;
        private boolean isCreate = false;
        private boolean isExit = false;
        private boolean isChange = false;
        private  boolean isStart = false;

        private int width;
        private int height;

        private Object object ;
        public GLThread(WeakReference<GLSurfaceView> glSurfaceViewWeakReference) {
            this.glSurfaceViewWeakReference = glSurfaceViewWeakReference;
        }

        @Override
        public void run() {
            super.run();
            isExit =false;
            isStart = false;
            object = new Object();
            eglHelper = new EglHelper();
            eglHelper.initEgl(glSurfaceViewWeakReference.get().surface,
                    glSurfaceViewWeakReference.get().eglContext);

            while (true) {
                if (isExit) {
                    release();
                    break;
                }
                if (isStart) {
                    // isStart是为了保证第二遍执行是否手动刷新
                    if (glSurfaceViewWeakReference.get().renderMode == RENDERMODE_WHEN_DIRTY) {
                        // 手动刷新，等待
                        synchronized (object) {
                            try{
                                object.wait();
                            }catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                        }
                    } else if (glSurfaceViewWeakReference.get().renderMode == RENDERMODE_CONTINUOUSLY) {
                        try{
                            Thread.sleep(1000/60);
                        }catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        throw  new RuntimeException("rednerMode in wrong value");
                    }
                }


                onCreate();
                onChange(width, height);
                onDraw();
                isStart = true;
            }
        }

        private void onCreate(){
            if (isCreate && glSurfaceViewWeakReference.get().glRender != null) {
                isCreate = false;
                glSurfaceViewWeakReference.get().glRender.onSurfaceCreate();
            }
        }

        private void onChange(int width, int height) {
            if (isChange && glSurfaceViewWeakReference.get().glRender != null) {
                isChange = false;
                glSurfaceViewWeakReference.get().glRender.onSurfaceChanged(width, height);
            }
        }

        private void onDraw(){
            if (glSurfaceViewWeakReference.get().glRender != null && eglHelper != null) {
                glSurfaceViewWeakReference.get().glRender.onDrawFrame();
                // 首次保证被调用两次，否则显示不出来，这是opengl的一个bug
                if (!isStart) {
                    glSurfaceViewWeakReference.get().glRender.onDrawFrame();
                }
                eglHelper.swapBuffers();
            }
        }
        // 请求刷新
        private void requestRender(){
            if (object != null) {
                synchronized (object) {
                    object.notifyAll();
                }
            }
        }

        public void onDestory(){
            isExit = true;
            // 释放object锁
            requestRender();
        }

        public void release(){
            if (eglHelper != null) {
                eglHelper.distoryEgl();
                eglHelper = null;
                object = null;
                glSurfaceViewWeakReference = null;
            }
        }

        public EGLContext getEglContext(){
            if (eglHelper != null) {
                return eglHelper.getEglContext();
            }
            return null;
        }
    }
}
