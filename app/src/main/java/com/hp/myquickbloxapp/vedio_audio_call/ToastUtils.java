package com.hp.myquickbloxapp.vedio_audio_call;

import android.widget.Toast;

import androidx.annotation.StringRes;

import com.hp.myquickbloxapp.MyApplication;


public class ToastUtils {

    private ToastUtils() {
        //empty
    }

    public static void shortToast(String message) {
        show(message, Toast.LENGTH_LONG);
    }

    public static void shortToast(@StringRes int resource) {
        show(MyApplication.getInstance().getString(resource), Toast.LENGTH_SHORT);
    }

    public static void longToast(String message) {
        show(message, Toast.LENGTH_LONG);
    }

    public static void longToast(@StringRes int resource) {
        show(MyApplication.getInstance().getString(resource), Toast.LENGTH_LONG);
    }

    private static void show(String message, int length) {
        Toast.makeText(MyApplication.getInstance(), message, length).show();
    }
}