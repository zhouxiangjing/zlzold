//
// Created by zxj on 2019/6/25.
//

#include <jni.h>
#include <hpsocket/HPSocket.h>

class MyTcpClientListener : public CTcpClientListener
{
public:
    MyTcpClientListener() {}
    ~MyTcpClientListener() {}

private:
    virtual EnHandleResult OnSend(ITcpClient* pSender, CONNID dwConnID, const BYTE* pData, int iLength)
    {
        return HR_OK;
    }
    virtual EnHandleResult OnReceive(ITcpClient* pSender, CONNID dwConnID, const BYTE* pData, int iLength)
    {
        return HR_OK;
    }
    virtual EnHandleResult OnClose(ITcpClient* pSender, CONNID dwConnID, EnSocketOperation enOperation, int iErrorCode)
    {
        return HR_OK;
    }
    virtual EnHandleResult OnConnect(ITcpClient* pSender, CONNID dwConnID)
    {
        return HR_OK;
    }
};

static MyTcpClientListener s_listener;
static CTcpPackClientPtr s_client(&s_listener);

#ifdef __cplusplus
extern "C" {
#endif

jint Java_com_zxj_zlz_Jni_test(JNIEnv *env, jobject obj) {

    s_client->SetMaxPackSize(0x01FFF);
    s_client->SetPackHeaderFlag(0x169);

    LPCTSTR address = "10.33.93.55";
    int portt = 10001;
    bool r = s_client->Start(address, portt, false);
    EnSocketError ret = s_client->GetLastError();
    LPCTSTR desc = s_client->GetLastErrorDesc();

    return 1;
}

#ifdef __cplusplus
}
#endif