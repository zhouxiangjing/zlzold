package com.zxj.zlz;

public class Jni {

    static {
        System.loadLibrary("myhpsocket");
    }

    public static native int connectServer();
    public static native int sendData(float y, float x);
}
