package com.lugeek.pluginapptest;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.lugeek.plugin_base.PluginLoader;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class JniPluginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jni);
    }

    public void buttonClick(View view) {
        try {
            Object jniTestObject = PluginLoader.getInstance().loadClass("com.lugeek.pluginapptest.plugin.JniTest");
            Method showToastMethod = jniTestObject.getClass().getMethod("showToast", Context.class);
            showToastMethod.invoke(jniTestObject, this);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
