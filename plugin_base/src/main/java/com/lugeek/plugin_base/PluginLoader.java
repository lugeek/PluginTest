package com.lugeek.plugin_base;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;

public class PluginLoader {

    private static PluginLoader instance = new PluginLoader();

    private Context context;
    private DexClassLoader pluginDexClassLoader;
    private Resources pluginResources;
    private PackageInfo pluginPackageArchiveInfo;

    public static PluginLoader getInstance() {
        return instance;
    }

    private PluginLoader() {}

    public void setContext(Context context) {
        this.context = context.getApplicationContext();
    }

    public void loadApk(String dexPath) {
        pluginDexClassLoader = new DexClassLoader(dexPath, context.getDir("dex", Context.MODE_PRIVATE).getAbsolutePath(), null, context.getClassLoader());

        pluginPackageArchiveInfo = context.getPackageManager().getPackageArchiveInfo(dexPath, PackageManager.GET_ACTIVITIES);

        createResources(dexPath);
    }

    /**
     * 从插件apk获取Resources，参考了Shadow的实现，没有用到反射。
     * 在小米手机上，Resources是MiuiResources，不过不影响使用。
     * @param dexPath 插件apk路径
     */
    private void createResources(String dexPath) {
        PackageManager packageManager = context.getPackageManager();
        pluginPackageArchiveInfo.applicationInfo.publicSourceDir = dexPath;
        pluginPackageArchiveInfo.applicationInfo.sourceDir = dexPath;
        pluginPackageArchiveInfo.applicationInfo.sharedLibraryFiles = context.getApplicationInfo().sharedLibraryFiles;
        try {
            pluginResources = packageManager.getResourcesForApplication(pluginPackageArchiveInfo.applicationInfo);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 从插件apk获取Resources，这种方式是网上大部分的用法，但是用到了反射，在版本更新后，可能会有兼容性的风险，作为备选。
     * @param dexPath 插件apk路径
     */
    private void createResourcesByReflect(String dexPath) {
        AssetManager assets = null;
        try {
            assets = AssetManager.class.newInstance();
            Method addAssetPath = AssetManager.class.getMethod("addAssetPath", String.class);
            addAssetPath.setAccessible(true);
            addAssetPath.invoke(assets, dexPath);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        pluginResources = new Resources(assets, context.getResources().getDisplayMetrics(), context.getResources().getConfiguration());
    }

    public PluginActivityInterface loadActivity(String activityName) {
        PluginActivityInterface pluginInterface = null;
        try {
            //加载该Activity的字节码对象
            Class<?> aClass = getPluginDexClassLoader().loadClass(activityName);
            //创建该Activity的实例对象
            Object newInstance = aClass.newInstance();
            if (newInstance instanceof PluginActivityInterface) {
                pluginInterface = (PluginActivityInterface) newInstance;
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return pluginInterface;
    }

    public DexClassLoader getPluginDexClassLoader() {
        return pluginDexClassLoader;
    }

    public Resources getPluginResources() {
        return pluginResources;
    }

    public PackageInfo getPluginPackageArchiveInfo() {
        return pluginPackageArchiveInfo;
    }
}
