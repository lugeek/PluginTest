package com.lugeek.pluginapptest;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.lugeek.plugin_base.PluginActivityInterface;
import com.lugeek.plugin_base.PluginLoader;

public class ProxyActivity extends Activity {

    private PluginActivityInterface mPluginActivity;
    private boolean isBeforeOnCreate = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        isBeforeOnCreate = false;
        mHostTheme = null;//释放资源
        super.onCreate(savedInstanceState);
        String activityName = getIntent().getStringExtra("activityName");
        mPluginActivity = PluginLoader.getInstance().loadActivity(activityName);
        mPluginActivity.attachContext(this);
        Bundle bundle = new Bundle();
        mPluginActivity.onCreate(bundle);
    }

    @Override
    public Resources getResources() {
        return mPluginActivity != null ? mPluginActivity.getResources() : super.getResources();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPluginActivity.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPluginActivity.onResume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mPluginActivity.onRestart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPluginActivity.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPluginActivity.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPluginActivity.onPause();
    }

    /**
     * Theme一旦设置了就不能更换Theme所在的Resouces了，见{@link Resources.Theme#setTo(Resources.Theme)}
     * 而Activity在OnCreate之前需要设置Theme和使用Theme。我们需要在Activity OnCreate之后才能注入插件资源。
     * 这就需要在Activity OnCreate之前不要调用Activity的setTheme方法，同时在getTheme时返回宿主的Theme资源。
     * 注：{@link Activity#setTheme(int)}会触发初始化Theme，因此不能调用。
     */
    private Resources.Theme mHostTheme;

    @Override
    public Resources.Theme getTheme() {
        if (isBeforeOnCreate) {
            if (mHostTheme == null) {
                mHostTheme = super.getResources().newTheme();
            }
            return mHostTheme;
        } else {
            return super.getTheme();
        }
    }

    @Override
    public void setTheme(int resid) {
        if (!isBeforeOnCreate) {
            super.setTheme(resid);
        }
    }
}