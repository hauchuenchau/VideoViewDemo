package com.example.videolibrary.util;


import android.util.Log;

/**
 * @Author Mr.hou
 * @time 2017/5/31
 * @Email houzhongzhou@cnlive.com
 * @Desc log工具.
 */

public class LogUtil {
    private static final String TAG = "VideoPlayer";

    public static void d(String message) {
        Log.d(TAG, message);
    }

    public static void e(String message, Throwable throwable) {
        Log.e(TAG, message, throwable);
    }
}
