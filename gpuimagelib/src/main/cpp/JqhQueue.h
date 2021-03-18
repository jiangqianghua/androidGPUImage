//
// Created by Administrator on 2021/1/23 0023.
//

#ifndef JQHOPENGLLIVEPUSHER_JQHQUEUE_H
#define JQHOPENGLLIVEPUSHER_JQHQUEUE_H

#include "queue"
#include "pthread.h"
#include "AndroidLog.h"

extern "C" {
#include "librtmp/rtmp.h"
};
class JqhQueue {
public:
    std::queue<RTMPPacket *> queuePacket;
    pthread_mutex_t mutexPacket;
    pthread_cond_t condPacket;
public:
    JqhQueue();
    ~JqhQueue();

    int putRtmpPacket(RTMPPacket *packet);
    RTMPPacket* getRtmpPacket();
    void clearQueue();
    void notifyQueue();
};


#endif //JQHOPENGLLIVEPUSHER_JQHQUEUE_H
