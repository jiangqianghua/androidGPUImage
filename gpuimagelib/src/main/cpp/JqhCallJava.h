//
// Created by Administrator on 2021/1/23 0023.
//

#include <jni.h>
#ifndef JQHOPENGLLIVEPUSHER_JQHCALLJAVA_H
#define JQHOPENGLLIVEPUSHER_JQHCALLJAVA_H

#define JQH_THREAD_MAIN 1
#define JQH_THREAD_CHILD 2

class JqhCallJava {
public:
    // 主线程回调
    JNIEnv *jniEnv = NULL;
    // 子线程回调
    JavaVM *javaVM = NULL;
    // 全局java对象
    jobject  jobj;
    jmethodID  jmid_connecting;
    jmethodID  jmid_connectsuccess;
    jmethodID  jmid_connectfail;
public:
    JqhCallJava(JavaVM *javaVM, JNIEnv *jniEnv, jobject *jobj);
    ~JqhCallJava();
    void onConnecting(int type);
    void onConnectSuccess();
    void onConnectFail(char *msg);
};


#endif //JQHOPENGLLIVEPUSHER_JQHCALLJAVA_H
