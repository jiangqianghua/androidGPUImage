package com.jqh.gpuimagelib.opengl;

import android.opengl.EGL14;
import android.view.Surface;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

public class EglHelper {

    private EGL10 mEgl;

    private EGLDisplay mEglDisplay;

    private EGLContext mEglContext;

    private EGLSurface mEglSurface;

    // 初始化egl
    public void initEgl(Surface surface, EGLContext eglContext) {
        // 1. 得到EGL实例
        mEgl = (EGL10) EGLContext.getEGL();
        // 2. 得到默认显示设备
        mEglDisplay = mEgl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
        if (mEglDisplay == EGL10.EGL_NO_DISPLAY) {
            throw new RuntimeException("eglGetDisplay failed");
        }

        // 3. 初始化默认设备
        int[] version = new int[2];
        if (!mEgl.eglInitialize(mEglDisplay, version)) {
            throw new RuntimeException("eglInitialize failed");
        }

        // 4.设置显示设备属性
        int[] attrbutes = new int[] {
                EGL10.EGL_RED_SIZE, 8,
                EGL10.EGL_GREEN_SIZE, 8,
                EGL10.EGL_BLUE_SIZE, 8,
                EGL10.EGL_ALPHA_SIZE, 8,
                EGL10.EGL_DEPTH_SIZE, 8,
                EGL10.EGL_STENCIL_SIZE, 8,
                EGL10.EGL_RENDERABLE_TYPE, 4,
                EGL10.EGL_NONE
        };

        int[] num_config = new int[1];
        if (!mEgl.eglChooseConfig(mEglDisplay, attrbutes, null, 1, num_config)) {
            throw  new IllegalArgumentException("eglChooseConfig failed");
        }

        int numConfigs = num_config[0];
        if (numConfigs <= 0) {
            throw new IllegalArgumentException("No configs match configSpec");
        }
        // 5.从系统中获取默认的配置
        EGLConfig[] configs = new EGLConfig[numConfigs];
        if (!mEgl.eglChooseConfig(mEglDisplay, attrbutes, configs, numConfigs, num_config)){
            throw new IllegalArgumentException("eglChooseConfig#2 failed");
        }

        // 6.创建EGLContext
        int[] attrib_list = {
                EGL14.EGL_CONTEXT_CLIENT_VERSION, 2,
                EGL10.EGL_NONE  // 表示结束
        };

        if (eglContext != null) {
            // 如果外部传入了EGLContext， 那么就可以设置共享
            mEglContext = mEgl.eglCreateContext(mEglDisplay, configs[0], eglContext, attrib_list);
        } else {
            mEglContext = mEgl.eglCreateContext(mEglDisplay, configs[0], EGL10.EGL_NO_CONTEXT, attrib_list);
        }

        // 7. 创建Surface
        mEglSurface = mEgl.eglCreateWindowSurface(mEglDisplay, configs[0], surface, null);

        // 8 绑定数据，渲染场景
        if (!mEgl.eglMakeCurrent(mEglDisplay, mEglSurface, mEglSurface, mEglContext)) {
            throw new RuntimeException("eglMakeCurrent fail");
        }

    }

    public boolean swapBuffers(){
        if (mEgl != null) {
            return mEgl.eglSwapBuffers(mEglDisplay, mEglSurface);
        } else {
            throw new RuntimeException("egl is null");
        }
    }

    public EGLContext getEglContext(){
        return mEglContext;
    }

    public void distoryEgl(){
        if (mEgl != null) {
            // 解绑
            mEgl.eglMakeCurrent(mEglDisplay,
                    EGL10.EGL_NO_SURFACE,
                    EGL10.EGL_NO_SURFACE,
                    EGL10.EGL_NO_CONTEXT);
            // 销毁surface
            mEgl.eglDestroySurface(mEglDisplay, mEglSurface);
            mEglSurface = null;
            //  销毁context
            mEgl.eglDestroyContext(mEglDisplay, mEglContext);
            mEglContext = null;
            mEgl.eglTerminate(mEglDisplay);
            mEglDisplay = null;
            mEgl = null;
        }
    }


}
