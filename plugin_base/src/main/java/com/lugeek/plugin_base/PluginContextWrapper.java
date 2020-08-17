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
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;

import java.lang.reflect.Constructor;
import java.util.HashMap;

public class PluginContextWrapper extends ContextThemeWrapper {
    private Resources mResources;
    private LayoutInflater mInflater;
    private ClassLoader mClassLoader;
    private String TAG= PluginContextWrapper.class.getSimpleName();
    private HashMap<String, Constructor<?>> mConstructors = new HashMap<String, Constructor<?>>();
    LayoutInflater.Factory mFactory = new LayoutInflater.Factory() {

        @Override
        public View onCreateView(String name, Context context, AttributeSet attrs) {
            return handleCreateView(name, context, attrs);
        }
    };
 
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
                inflater.setFactory(mFactory);
                mInflater = inflater.cloneInContext(this);
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

    private final View handleCreateView(String name, Context context, AttributeSet attrs) {

        // 构造器缓存
        Constructor<?> construct = mConstructors.get(name);

        // 缓存失败
        if (construct == null) {
            // 找类
            Class<?> c = null;
            boolean found = false;
            do {
                try {
                    c = mClassLoader.loadClass(name);
                    if (c == null) {
                        // 没找到，不管
                        break;
                    }
                    if (c == ViewStub.class) {
                        // 系统特殊类，不管
                        break;
                    }
                    if (c.getClassLoader() != mClassLoader) {
                        // 不是插件类，不管
                        break;
                    }
                    // 找到
                    found = true;
                } catch (ClassNotFoundException e) {
                    // 失败，不管
                    break;
                }
            } while (false);
            if (!found) {
                Log.e(TAG, "layout not found name=" + name);
                return null;
            }
            // 找构造器
            try {
                construct = c.getConstructor(Context.class, AttributeSet.class);
                mConstructors.put(name, construct);
            } catch (Exception e) {
                InflateException ie = new InflateException(attrs.getPositionDescription() + ": Error inflating mobilesafe class " + name, e);
                throw ie;
            }
        }

        // 构造
        try {
            View v = (View) construct.newInstance(context, attrs);
            return v;
        } catch (Exception e) {
            InflateException ie = new InflateException(attrs.getPositionDescription() + ": Error inflating mobilesafe class " + name, e);
            throw ie;
        }
    }
}