package com.lugeek.pluginapptest;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.lugeek.plugin_base.PluginLoader;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class NormalClassPluginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normal_class_plugin);

    }

    public void buttonClick(View button) {
        try {
            // 加载插件的类(插件的包名.类名)
            Class<?> mClass = PluginLoader.getInstance().getPluginDexClassLoader().loadClass("com.lugeek.pluginapptest.plugin.ToastTest");


            // 获取类的实例
            Object toastTestObject = mClass.newInstance();

            // 然后通过反射获取对应的方法
            Method setToastMethod = mClass.getMethod("setToast", String.class);
            setToastMethod.setAccessible(true);
            Method getToastMethod = mClass.getMethod("getToast");
            getToastMethod.setAccessible(true);
            Method startToastMethod = mClass.getMethod("startToast", Context.class);
            startToastMethod.setAccessible(true);

            // 然后执行对应方法进行相关设置和获取
            setToastMethod.invoke(toastTestObject, "Plugin 调通了！");
            String toast = (String) getToastMethod.invoke(toastTestObject);
            startToastMethod.invoke(toastTestObject, this);

            // 然后本地进行一些提示等操作
//            Toast.makeText(this, toast, Toast.LENGTH_LONG).show();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

}
