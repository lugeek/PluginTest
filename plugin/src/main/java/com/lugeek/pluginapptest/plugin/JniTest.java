package com.lugeek.pluginapptest.plugin;

import android.content.Context;
import android.widget.Toast;

public class JniTest {

    static {
        System.loadLibrary("hello-jni");
    }

    public native String stringFromJNI();

    public void showToast(Context context) {
        String jni = stringFromJNI();
        Toast.makeText(context, jni, Toast.LENGTH_SHORT).show();
    }
}
