//
// Created by Administrator on 2021/1/23 0023.
//

#include "RtmpPush.h"

RtmpPush::RtmpPush(const char *url, JqhCallJava *jqhCallJava) {
    this->url = static_cast<char *> (malloc(512));
    // 拷贝一份
    strcpy(this->url, url);
    this->queue = new JqhQueue();
    this->jqhCallJava = jqhCallJava;
}

RtmpPush::~RtmpPush() {
    queue->notifyQueue();
    queue->clearQueue();
    free(url);
}


void *callBackPush(void *data) {
    RtmpPush *rtmpPush = static_cast<RtmpPush *>(data);
    rtmpPush->startPushing = false;
    rtmpPush->jqhCallJava->onConnecting(JQH_THREAD_CHILD);
    // 开始初始化rtmp
    rtmpPush->rtmp = RTMP_Alloc();
    RTMP_Init(rtmpPush->rtmp);
    rtmpPush->rtmp->Link.timeout = 10;
    rtmpPush->rtmp->Link.lFlags |= RTMP_LF_LIVE;
    RTMP_SetupURL(rtmpPush->rtmp, rtmpPush->url);
    RTMP_EnableWrite(rtmpPush->rtmp);

    if (!RTMP_Connect(rtmpPush->rtmp, NULL)) {
        LOGE("can not connect the url");
        rtmpPush->jqhCallJava->onConnectFail("can not connect the url");
        goto end;
    }
    if (!RTMP_ConnectStream(rtmpPush->rtmp, 0)) {
        LOGE("can not connect the stream of service");
        rtmpPush->jqhCallJava->onConnectFail("can not connect the stream of service");
        goto end;
    }

    LOGE("连接成功, 开始推流");
    rtmpPush->jqhCallJava->onConnectSuccess();
    rtmpPush->startPushing = true;
    // 记录推流开始时间
    rtmpPush->startTime = RTMP_GetTime();

    // 循环推流
    while (true) {
        if (!rtmpPush->startPushing) {
            break;
        }

        RTMPPacket *packet = NULL;
        packet = rtmpPush->queue->getRtmpPacket();
        if (packet != NULL) {
            int result = RTMP_SendPacket(rtmpPush->rtmp, packet, 1); //1 是缓存大小
            LOGD("RTMP_SendPacket result is %d", result);
            RTMPPacket_Free(packet);
            free(packet);
            packet = NULL;
        }

    }


    end:
        RTMP_Close(rtmpPush->rtmp);
        RTMP_Free(rtmpPush->rtmp);
        rtmpPush->rtmp = NULL;

    pthread_exit(&rtmpPush->push_thread);

}

void RtmpPush::init() {

//    jqhCallJava->onConnecting(JQH_THREAD_MAIN);
    pthread_create(&push_thread, NULL, callBackPush, this);
}

void RtmpPush::pushSPSPPS(char *sps, int sps_len, char *pps, int pps_len) {
    // 16 用来放头部信息的
    int bodysize = sps_len + pps_len + 16;
    RTMPPacket *packet = static_cast<RTMPPacket *>(malloc(sizeof(RTMPPacket)));
    RTMPPacket_Alloc(packet, bodysize);
    RTMPPacket_Reset(packet); //初始化数据

    char *body = packet->m_body;

    // 开始存放前16头部信息
    int i = 0 ;

    body[i++] = 0x17;  // 第一位1 表示关键帧， 2 表示非关键帧， 第二位7 表示codecID，我们是AVC

    body[i++] = 0x00;  //  下面四个是保留的 fixed
    body[i++] = 0x00;
    body[i++] = 0x00;
    body[i++] = 0x00;

    body[i++] = 0x01; //版本

    body[i++] = sps[1];  //  profile
    body[i++] = sps[2];  // 兼容性
    body[i++] = sps[3]; // profile level

    body[i++] = 0xff;  // 包长度

    body[i++] = 0xe1; // sps个数

    // 以下两个是存放sps的长度，要存放到两个字节里面，第一个字节存放高八位，第二个存放底八位
    body[i++] = (sps_len >> 8) & 0xff;
    body[i++] = sps_len &0xff;

    memcpy(&body[i], sps, sps_len);  // sps实际内容
    i+= sps_len;

    body[i++] = 0x01; //pps个数
    body[i++] = (pps_len >> 8) & 0xff;
    body[i++] = pps_len &0xff;
    memcpy(&body[i], pps, pps_len);  // pps实际内容

    // 开始给packet赋值
    packet->m_packetType = RTMP_PACKET_TYPE_VIDEO;
    packet->m_nBodySize = bodysize;
    packet->m_nTimeStamp = 0 ;
    packet->m_hasAbsTimestamp = 0 ;
    packet->m_nChannel= 0x04;
    packet->m_headerType = RTMP_PACKET_SIZE_MEDIUM;
    packet->m_nInfoField2 = rtmp->m_stream_id;

    // 放入队列
    queue->putRtmpPacket(packet);



}

void RtmpPush::pushVideoData(char *data, int data_len, bool keyframe) {
    // 9 用来放头部信息的
    int bodysize = data_len + 9;
    RTMPPacket *packet = static_cast<RTMPPacket *>(malloc(sizeof(RTMPPacket)));
    RTMPPacket_Alloc(packet, bodysize);
    RTMPPacket_Reset(packet); //初始化数据

    char *body = packet->m_body;

    // 开始存放前16头部信息
    int i = 0 ;

    if (keyframe) {
        body[i++] = 0x17;  // 第一位1 表示关键帧， 2 表示非关键帧， 第二位7 表示codecID，我们是AVC
    } else {
        body[i++] = 0x27;
    }

    body[i++] = 0x01;  //  下面四个是保留的 fixed. 0x01 是NALU单元
    body[i++] = 0x00;
    body[i++] = 0x00;
    body[i++] = 0x00;

    //存放长度，存放到4个字节中
    body[i++] = (data_len >> 24) &0xff;
    body[i++] = (data_len >> 16) &0xff;
    body[i++] = (data_len >> 8) &0xff;
    body[i++] = (data_len) &0xff;

    // 存放h264裸流
    memcpy(&body[i], data, data_len);

    // 开始给packet赋值
    packet->m_packetType = RTMP_PACKET_TYPE_VIDEO;
    packet->m_nBodySize = bodysize;
    packet->m_nTimeStamp = RTMP_GetTime() - startTime ;
    packet->m_hasAbsTimestamp = 0 ;
    packet->m_nChannel= 0x04;
    packet->m_headerType = RTMP_PACKET_SIZE_LARGE;
    packet->m_nInfoField2 = rtmp->m_stream_id;

    // 放入队列
    queue->putRtmpPacket(packet);

}

void RtmpPush::pushAudioData(char *data, int data_len) {
    // 9 用来放头部信息的
    int bodysize = data_len + 2;
    RTMPPacket *packet = static_cast<RTMPPacket *>(malloc(sizeof(RTMPPacket)));
    RTMPPacket_Alloc(packet, bodysize);
    RTMPPacket_Reset(packet); //初始化数据

    char *body = packet->m_body;
    body[0] = 0xAF;
    body[1] = 0x01;
    // aac
    memcpy(&body[2], data, data_len);

    // 开始给packet赋值
    packet->m_packetType = RTMP_PACKET_TYPE_AUDIO;
    packet->m_nBodySize = bodysize;
    packet->m_nTimeStamp = RTMP_GetTime() - startTime ;
    packet->m_hasAbsTimestamp = 0 ;
    packet->m_nChannel= 0x04;
    packet->m_headerType = RTMP_PACKET_SIZE_LARGE;
    packet->m_nInfoField2 = rtmp->m_stream_id;

    // 放入队列
    queue->putRtmpPacket(packet);


}

void RtmpPush::pushStop() {
    startPushing = false;
    queue->notifyQueue();
    // 会等待线程退出才会往下执行
    pthread_join(push_thread, NULL);
}
