//
// Created by Administrator on 2021/1/23 0023.
//

#ifndef JQHOPENGLLIVEPUSHER_RTMPPUSH_H
#define JQHOPENGLLIVEPUSHER_RTMPPUSH_H

#include <malloc.h>
#include <string.h>
#include "JqhQueue.h"
#include "pthread.h"
#include "JqhCallJava.h"
extern "C" {
#include "librtmp/rtmp.h"
};
class RtmpPush {
public:
    RTMP * rtmp = NULL;
    char *url = NULL;
    JqhQueue *queue = NULL;
    pthread_t  push_thread;
    JqhCallJava *jqhCallJava = NULL;
    bool startPushing = false;
    long startTime = 0 ;
public:
    RtmpPush(const char *url, JqhCallJava *jqhCallJava);
    ~RtmpPush();

    void init();

    void pushSPSPPS(char *sps, int sps_len, char *pps, int pps_len);

    void pushVideoData(char *data, int data_len , bool keyframe);

    void pushAudioData(char *data, int data_len);

    void pushStop();
};


#endif //JQHOPENGLLIVEPUSHER_RTMPPUSH_H
