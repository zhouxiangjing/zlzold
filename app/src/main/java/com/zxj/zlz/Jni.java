package com.zxj.zlz;

public class Jni {

    static {
        System.loadLibrary("myhpsocket");
    }

    public static native int test();
}
