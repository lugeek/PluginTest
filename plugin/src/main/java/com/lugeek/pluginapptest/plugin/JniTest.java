package com.lugeek.pluginapptest.plugin;

import android.content.Context;
import android.widget.Toast;

public class JniTest {

    // 不需要再load了，因为插件已经加载过了
//    static {
//        System.loadLibrary("hello-jni");
//    }

    public native String stringFromJNI();

    public void showToast(Context context) {
        String jni = stringFromJNI();
        Toast.makeText(context, jni, Toast.LENGTH_SHORT).show();
    }
}
