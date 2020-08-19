package com.lugeek.plugin_base;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import com.lugeek.plugin_base.replugin.PluginNativeLibsHelper;
import java.io.File;
import java.lang.reflect.Constructor;
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
        pluginPackageArchiveInfo = context.getPackageManager().getPackageArchiveInfo(dexPath,
                PackageManager.GET_ACTIVITIES | PackageManager.GET_SERVICES
                        | PackageManager.GET_PROVIDERS | PackageManager.GET_RECEIVERS
                        | PackageManager.GET_META_DATA);
        File libPath = context.getDir("libs", Context.MODE_PRIVATE);
        pluginPackageArchiveInfo.applicationInfo.publicSourceDir = dexPath;
        pluginPackageArchiveInfo.applicationInfo.sourceDir = dexPath;
        pluginPackageArchiveInfo.applicationInfo.nativeLibraryDir = libPath.getAbsolutePath();

        createMergedResourcesByReflect(dexPath);

        PluginNativeLibsHelper.install(dexPath, libPath);

        pluginDexClassLoader = new DexClassLoader(dexPath,
                context.getDir("dex", Context.MODE_PRIVATE).getAbsolutePath(),
                libPath.getAbsolutePath(),
                context.getClassLoader());
    }

    /**
     * 从插件apk获取Resources，参考了Shadow的实现，没有用到反射。
     * 在小米手机上，Resources是MiuiResources，不过不影响使用。
     * @param dexPath 插件apk路径
     */
    private void createResources(String dexPath) {
        PackageManager packageManager = context.getPackageManager();
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

    /**
     * 创建插件和宿主资源合并的resource
     * @param dexPath 插件apk路径
     */
    private void createMergedResourcesByReflect(String dexPath) {
        AssetManager assetManager = null;
        try {
            assetManager = AssetManager.class.newInstance();
            Method addAssetPath = AssetManager.class.getMethod("addAssetPath", String.class);
            addAssetPath.setAccessible(true);
            addAssetPath.invoke(assetManager, context.getPackageResourcePath()); // 添加宿主资源路径
            addAssetPath.invoke(assetManager, dexPath); // 添加插件资源路径


        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        pluginResources = new Resources(assetManager, context.getResources().getDisplayMetrics(),
                context.getResources().getConfiguration());
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

    public <T> T loadClass(String className) {
        T instance = null;
        try {
            Class<?> aClass = getPluginDexClassLoader().loadClass(className);
            instance = (T)aClass.newInstance();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return instance;
    }

    public <T> T loadClass(String className, Object... params) {
        T instance = null;
        try {
            Class<?>[] paramsClasses = new Class<?>[params.length];
            for (int i = 0; i < params.length; i++) {
                paramsClasses[i] = params[i].getClass();
            }
            Class<?> aClass = getPluginDexClassLoader().loadClass(className);
//            Constructor aConstructor=aClass.getDeclaredConstructor(paramsClasses);
            Constructor aConstructor = findConstructor(aClass, params);
            aConstructor.setAccessible(true);
            instance = (T)aConstructor.newInstance(params);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return instance;
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

    public PluginContextWrapper getPluginContextWrapper(Context context) {
        return new PluginContextWrapper(context, getPluginResources(), getPluginDexClassLoader());
    }

    public static <T> Constructor<T> findConstructor(Class<T> clazz, Object... args) throws NoSuchMethodException
    {
        Constructor<T> matchingConstructor = null;
        Constructor<T>[] constructors = (Constructor<T>[]) clazz.getConstructors();
        for (Constructor<T> constructor : constructors)
        {
//            if (constructor.getParameterCount() != args.length)
//            {
//                continue;
//            }
            Class<?>[] formalTypes = constructor.getParameterTypes();
            if (formalTypes.length != args.length) {
                continue;
            }
            for (int i = 0; i < formalTypes.length; i++)
            {
                if (!formalTypes[i].isInstance(args[i]))
                {
                    continue;
                }
            }
            if (matchingConstructor != null) // already found one ... so there is more than one ...
            {
                throw new NoSuchMethodException("Multiple constructors found: " + printArgs(clazz, args) + " --> " + matchingConstructor + " --> " + constructor);
            }
            matchingConstructor = constructor;
        }
        if (matchingConstructor == null)
        {
            throw new NoSuchMethodException("No constructor found for: " + printArgs(clazz, args));
        }
        return matchingConstructor;
    }

    private static String printArgs(Class<?> clazz, Object... args)
    {
        StringBuilder msg = new StringBuilder();
        msg.append("new ");
        msg.append(clazz.getName());
        msg.append("(");
        for (int i = 0; i < args.length; i++)
        {
            if (i > 0)
            {
                msg.append(", ");
            }
            msg.append(args[i] == null ? "null" : args[i].getClass().getName());
        }
        msg.append(")");
        return msg.toString();
    }
}
