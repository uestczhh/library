package com.uestczhh.commlib.utils;

import android.util.Log;

/**
 * 输出日志
 * Created by zhanghao on 2017/6/6.
 */

public class LogUtil {
    //debug开关
    public static boolean flag = true;
    //统一的TAG
    public static String TAG = "log";

    public static void d(String tag, String msg) {
        if (flag)
            Log.d(tag, msg);
    }

    public static void e(String tag, String msg) {
        if (flag)
            Log.e(tag, msg);
    }

    public static void i(String tag, String msg) {
        if (flag)
            Log.i(tag, msg);
    }

    public static void w(String tag, String msg) {
        if (flag)
            Log.w(tag, msg);
    }

    public static void v(String tag, String msg) {
        if (flag)
            Log.v(tag, msg);
    }

    public static void d(String msg) {
        if (flag)
            Log.d(TAG, msg);
    }

    public static void e(String msg) {
        if (flag)
            Log.e(TAG, msg);
    }

    public static void i(String msg) {
        if (flag)
            Log.i(TAG, msg);
    }

    public static void w(String msg) {
        if (flag)
            Log.w(TAG, msg);
    }

    public static void v(String msg) {
        if (flag)
            Log.v(TAG, msg);
    }
}
