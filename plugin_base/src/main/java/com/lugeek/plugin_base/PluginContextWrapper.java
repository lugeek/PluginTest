package com.lugeek.plugin_base;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;

public class PluginContextWrapper extends ContextThemeWrapper {
    private Resources mResources;
    private LayoutInflater mInflater;
    private ClassLoader mClassLoader;
    private String TAG= PluginContextWrapper.class.getSimpleName();
 
    public PluginContextWrapper(Context base, Resources resources, ClassLoader classLoader) {
        super(base, android.R.style.Theme);
        this.mResources = resources;
        this.mClassLoader=classLoader;
    }
 
 
 
    @Override
    public Object getSystemService(String name) {
        if (LAYOUT_INFLATER_SERVICE.equals(name)) {
            if (mInflater == null) {
                LayoutInflater inflater = (LayoutInflater) super.getSystemService(name);
//                // 新建一个，设置其工厂
                mInflater = inflater.cloneInContext(this);
//                mInflater = new MyLayoutInflater(this);
            }
            return mInflater;
        }
        return super.getSystemService(name);
 
    }
 
    @Override
    public AssetManager getAssets() {
        if (mResources != null) {
            return mResources.getAssets();
        }
        return super.getAssets();
    }
 
 
    @Override
    public Resources getResources() {
        if (mResources != null) {
            return mResources;
        }
 
        return super.getResources();
    }
 
 
    /**
     *  重写getClassLoader()方法，
     *  布局渲染时加载插件包里面的class文件
     *  避免自定义布局 ClassNotFoundException 问题
     * @return 插件包的classloader
     */
    @Override
    public ClassLoader getClassLoader() {
        if (mClassLoader!=null){
            return mClassLoader;
        }
        Log.d(TAG,"getClassLoader()");
        return super.getClassLoader();
    }


    static class MyLayoutInflater extends LayoutInflater {

        protected MyLayoutInflater(Context context) {
            super(context);
        }

        protected MyLayoutInflater(LayoutInflater original, Context newContext) {
            super(original, newContext);
        }

        @Override
        public LayoutInflater cloneInContext(Context newContext) {
            return LayoutInflater.from(PluginLoader.getInstance().getPluginContextWrapper(newContext));
        }

        @Override
        protected View onCreateView(String name, AttributeSet attrs) throws ClassNotFoundException {
            return super.onCreateView(name, attrs);
        }

        @Override
        protected View onCreateView(View parent, String name, AttributeSet attrs) throws ClassNotFoundException {
            return super.onCreateView(parent, name, attrs);
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull Context viewContext, @Nullable View parent, @NonNull String name, @Nullable AttributeSet attrs) throws ClassNotFoundException {
            return super.onCreateView(viewContext, parent, name, attrs);
        }
    }
}