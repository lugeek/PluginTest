package com.lugeek.pluginapptest;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.lugeek.plugin_base.PluginActivityInterface;
import com.lugeek.plugin_base.PluginLoader;

public class ProxyActivity extends AppCompatActivity {

    private PluginActivityInterface mPluginActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        TypedArray a = this.obtainStyledAttributes(android.support.v7.appcompat.R.styleable.AppCompatTheme);
        if (!a.hasValue(android.support.v7.appcompat.R.styleable.AppCompatTheme_windowActionBar)) {
            a.recycle();
            Log.i("crash", "crash");
        }
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
}