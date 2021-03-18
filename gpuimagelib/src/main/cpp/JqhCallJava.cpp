//
// Created by Administrator on 2021/1/23 0023.
//

#include "JqhCallJava.h"

JqhCallJava::JqhCallJava(JavaVM *javaVM, JNIEnv *jniEnv, jobject *jobj) {
        this->javaVM = javaVM;
        this->jniEnv = jniEnv;
        this->jobj = jniEnv->NewGlobalRef(*jobj);
        // 获取当前实例的class
        jclass jlz = jniEnv->GetObjectClass(this->jobj);

        jmid_connecting = jniEnv->GetMethodID(jlz, "onConnecting", "()V");
        jmid_connectsuccess = jniEnv->GetMethodID(jlz, "onConnectSuccess", "()V");
        jmid_connectfail = jniEnv->GetMethodID(jlz, "onConnectFail", "(Ljava/lang/String;)V");
}

JqhCallJava::~JqhCallJava() {

        jniEnv->DeleteLocalRef(jobj);
        javaVM = NULL;
        jniEnv = NULL;
}

void JqhCallJava::onConnecting(int type) {
        if (type == JQH_THREAD_CHILD) {
                JNIEnv *jniEnv;
                // 局部的jniEnv
                if(javaVM->AttachCurrentThread(&jniEnv, 0) != JNI_OK) {
                        return;
                }
                jniEnv->CallVoidMethod(jobj, jmid_connecting);
                javaVM->DetachCurrentThread();

        }else {
                // 全局的jniEnv，回调到主线程
                jniEnv->CallVoidMethod(jobj, jmid_connecting);
        }
}

void JqhCallJava::onConnectSuccess() {
        JNIEnv *jniEnv;
        // 局部的jniEnv
        if(javaVM->AttachCurrentThread(&jniEnv, 0) != JNI_OK) {
                return;
        }
        jniEnv->CallVoidMethod(jobj, jmid_connectsuccess);
        javaVM->DetachCurrentThread();
}

void JqhCallJava::onConnectFail(char *msg) {
        JNIEnv *jniEnv;
        // 局部的jniEnv
        if(javaVM->AttachCurrentThread(&jniEnv, 0) != JNI_OK) {
                return;
        }
        jstring jmsg = jniEnv->NewStringUTF(msg);
        jniEnv->CallVoidMethod(jobj, jmid_connectfail, jmsg);
        jniEnv->DeleteLocalRef(jmsg);
        javaVM->DetachCurrentThread();
}
