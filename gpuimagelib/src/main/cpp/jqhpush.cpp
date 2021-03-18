#include <jni.h>
#include <string>
#include <SLES/OpenSLES.h>
#include <SLES/OpenSLES_Android.h>
#include "RtmpPush.h"
#include "JqhCallJava.h"
RtmpPush *rtmpPush = NULL;
JqhCallJava *jqhCallJava = NULL;
JavaVM *javaVM = NULL;
bool _exit = true;
SLObjectItf  slObjectEngine = NULL;
SLEngineItf engineItf = NULL;
SLObjectItf  recordObj = NULL;
SLRecordItf  recordItf = NULL;
extern "C"
JNIEXPORT void JNICALL
Java_com_jqh_gpuimagelib_push_JqhPushVideo_initPush(
        JNIEnv *env,
        jobject instance, jstring pushUrl_) {
    const char *pushUrl = env->GetStringUTFChars(pushUrl_, 0);
    if(jqhCallJava == NULL) {
        _exit = false;
        JqhCallJava *jqhCallJava = new JqhCallJava(javaVM, env, &instance);
        rtmpPush = new RtmpPush(pushUrl, jqhCallJava);
        rtmpPush->init();
    }
    env->ReleaseStringUTFChars(pushUrl_, pushUrl);
}

extern  "C" {
    // 在java调用loadLibrary系统会自动调用
    JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved) {
        javaVM = vm;
        JNIEnv *env;
        if (vm -> GetEnv((void **)&env,JNI_VERSION_1_4) != JNI_OK){
            if (LOG_SHOW) {
                LOGE("GetEnv failed!")
            }
            return -1;
        }
        LOGD("GetEnv success!");
        return JNI_VERSION_1_4;
    }
}

 extern "C" {
 JNIEXPORT void JNI_OnUnload(JavaVM* vm, void* reserved) {
     javaVM = NULL;
 }
}
extern "C"
JNIEXPORT void JNICALL
Java_com_jqh_gpuimagelib_push_JqhPushVideo_pushSPSPPS(JNIEnv *env, jobject instance,
                                                              jbyteArray sps_, jint sps_len,
                                                              jbyteArray pps_, jint pps_len) {
    jbyte *sps = env->GetByteArrayElements(sps_, NULL);
    jbyte *pps = env->GetByteArrayElements(pps_, NULL);

    if (rtmpPush != NULL && !_exit) {
        rtmpPush->pushSPSPPS(reinterpret_cast<char *>(sps), sps_len, reinterpret_cast<char *>(pps), pps_len);
    }

    env->ReleaseByteArrayElements(sps_, sps, 0);
    env->ReleaseByteArrayElements(pps_, pps, 0);
}extern "C"
JNIEXPORT void JNICALL
Java_com_jqh_gpuimagelib_push_JqhPushVideo_pushVideoData(JNIEnv *env,
                                                                       jobject instance,
                                                                       jbyteArray data_,
                                                                       jint data_len,
                                                                       jboolean keyframe) {
    jbyte *data = env->GetByteArrayElements(data_, NULL);

    if (rtmpPush != NULL && !_exit) {
        rtmpPush->pushVideoData(reinterpret_cast<char *>(data), data_len, keyframe);
    }

    env->ReleaseByteArrayElements(data_, data, 0);
}extern "C"
JNIEXPORT void JNICALL
Java_com_jqh_gpuimagelib_push_JqhPushVideo_pushAudioData(JNIEnv *env,
                                                                       jobject instance,
                                                                       jbyteArray data_,
                                                                       jint data_len) {
    jbyte *data = env->GetByteArrayElements(data_, NULL);

    if (rtmpPush != NULL && !_exit) {
        rtmpPush->pushAudioData(reinterpret_cast<char *>(data), data_len);
    }


    env->ReleaseByteArrayElements(data_, data, 0);
}extern "C"
JNIEXPORT void JNICALL
Java_com_jqh_gpuimagelib_push_JqhPushVideo_pushStop(JNIEnv *env, jobject instance) {

    if (rtmpPush != NULL) {
        _exit = true;
        rtmpPush->pushStop();
        delete(rtmpPush);
        delete(jqhCallJava);
        rtmpPush = NULL;
        jqhCallJava = NULL;
    }

}extern "C"
JNIEXPORT void JNICALL
Java_com_jqh_gpuimagelib_push_JqhPushVideo_startRecordAudio(JNIEnv *env, jobject instance,
                                                               jstring path_) {
    const char *path = env->GetStringUTFChars(path_, 0);

    //创建引擎
    slCreateEngine(&slObjectEngine, 0, NULL, 0, NULL, NULL);
    (*slObjectEngine)->Realize(slObjectEngine, SL_BOOLEAN_FALSE);
    (*slObjectEngine)->GetInterface(slObjectEngine, SL_IID_ENGINE, &engineItf);

    // 获取输入设备
    SLDataLocator_IODevice loc_dev = {
            SL_DATALOCATOR_IODEVICE,
            SL_IODEVICE_AUDIOINPUT,
            SL_DEFAULTDEVICEID_AUDIOINPUT,
            NULL
    };
    SLDataSource audioSrc = {&loc_dev, NULL};

    SLDataLocator_AndroidSimpleBufferQueue loc_bq = {
            SL_DATALOCATOR_ANDROIDBUFFERQUEUE,
            2
    };

    SLDataFormat_PCM format_pcm {

    };

    SLDataSink audioSink = {&loc_bq};

    env->ReleaseStringUTFChars(path_, path);
}extern "C"
JNIEXPORT void JNICALL
Java_com_jqh_gpuimagelib_push_JqhPushVideo_stopRecordAudio(JNIEnv *env, jobject instance) {

    // TODO

}