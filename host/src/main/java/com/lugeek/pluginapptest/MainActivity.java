package com.lugeek.pluginapptest;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;

public class MainActivity extends AppCompatActivity {

    private String pluginAppFileName = "plugin-debug.apk";
    private DexClassLoader dexClassLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String path = getLocalCachePath(this);
        String apkPath = copyFilesFromAssets(this, pluginAppFileName, path);
        this.dexClassLoader = new DexClassLoader(apkPath, path, null, this.getClassLoader());

    }

    public void buttonClick(View button) {
        try {
            // 加载插件的类(插件的包名.类名)
            Class<?> mClass = dexClassLoader.loadClass("com.lugeek.pluginapptest.plugin.ToastTest");


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

    public static String getLocalCachePath(Context context) {
        File sdcache = context.getExternalCacheDir();
        if (sdcache != null && !sdcache.exists()) {
            sdcache.mkdirs();
        }
        return sdcache != null ? sdcache.getAbsolutePath() : null;
    }

    public static String copyFilesFromAssets(Context context, String assetsPath, String savePath) {
        try {
            if (savePath == null) {
                return null;
            }
            InputStream is = context.getAssets().open(assetsPath);
            if (!savePath.endsWith("/")){
                savePath += "/";
            }
            FileOutputStream fos = new FileOutputStream(new File(savePath + assetsPath));
            byte[] buffer = new byte[1024];
            int byteCount = 0;
            while ((byteCount = is.read(buffer)) != -1) {// 循环从输入流读取
                // buffer字节
                fos.write(buffer, 0, byteCount);// 将读取的输入流写入到输出流
            }
            fos.flush();// 刷新缓冲区
            is.close();
            fos.close();
        } catch (Exception e) {
            return null;
        }
        return savePath + assetsPath;
    }
}
