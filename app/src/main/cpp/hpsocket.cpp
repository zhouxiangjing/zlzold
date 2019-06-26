//
// Created by zxj on 2019/6/25.
//

#include <jni.h>
#include <android/log.h>

#include <string>

#include <hpsocket/HPSocket.h>

#define LOG_OPEN
#define LOG_TAG    "ZXJZLZ"

#ifdef LOG_OPEN
#define LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)
#else
#define LOGI(...) NULL
#define LOGE(...) NULL
#endif

class MyTcpClientListener : public CTcpClientListener
{
public:
    MyTcpClientListener() {}
    ~MyTcpClientListener() {}

private:
    virtual EnHandleResult OnSend(ITcpClient* pSender, CONNID dwConnID, const BYTE* pData, int iLength){

        LOGI("CTcpClientListener OnSend dwConnID=%d iLength=%d", dwConnID, iLength);
        return HR_OK;
    }
    virtual EnHandleResult OnReceive(ITcpClient* pSender, CONNID dwConnID, const BYTE* pData, int iLength){

        LOGI("CTcpClientListener OnReceive dwConnID=%d iLength=%d pData=%s", dwConnID, iLength, pData);
        return HR_OK;
    }
    virtual EnHandleResult OnClose(ITcpClient* pSender, CONNID dwConnID, EnSocketOperation enOperation, int iErrorCode){

        LOGI("CTcpClientListener OnClose dwConnID=%d iErrorCode=%d", dwConnID, iErrorCode);
        return HR_OK;
    }
    virtual EnHandleResult OnConnect(ITcpClient* pSender, CONNID dwConnID)
    {
        LOGI("CTcpClientListener OnConnect dwConnID=%d", dwConnID);
        return HR_OK;
    }
};

static MyTcpClientListener s_listener;
static CTcpPackClientPtr s_client(&s_listener);

#ifdef __cplusplus
extern "C" {
#endif

jint Java_com_zxj_zlz_Jni_connectServer(JNIEnv *env, jobject obj) {

    s_client->SetMaxPackSize(0x01FFF);
    s_client->SetPackHeaderFlag(0x169);

    LPCTSTR address = "10.33.93.79";
    int portt = 5555;
    if(!s_client->Start(address, portt, false)) {
        EnSocketError ret = s_client->GetLastError();
        LPCTSTR desc = s_client->GetLastErrorDesc();

        LOGE("faild to connecte %s:%d %d %s", address, portt, ret, desc);
        return -1;
    }

    return 0;
}

jint Java_com_zxj_zlz_Jni_sendData(JNIEnv *env, jobject obj, jfloat y, jfloat x) {

    std::string t = "helloworld";
    const char* data = t.c_str();
    if(!s_client->Send((const unsigned char*)data, 11)) {
        LOGE("sendData faild %s", s_client->GetLastErrorDesc());
        return -1;
    }

    return 0;
}

#ifdef __cplusplus
}
#endif