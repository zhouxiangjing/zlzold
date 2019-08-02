package com.zxj.utils;

public class Jni {

    static {
        System.loadLibrary("myhpsocket");
    }

    public static native int test(float y, float x);
    public static native int connectServer();
    public static native int sendData(float y, float x);
}
