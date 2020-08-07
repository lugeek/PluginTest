package com.lugeek.pluginapptest;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.lugeek.plugin_base.FileUtil;
import com.lugeek.plugin_base.PluginLoader;

public class MainActivity extends AppCompatActivity {

    private String pluginAppFileName = "plugin-debug.apk";

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);

        // 初始化，将assets中的apk拷贝到本地，再通过DexClassLoader加载本地apk
        String apkDexPath = FileUtil.getCacheDirectory(this, Environment.DIRECTORY_DOWNLOADS).getPath();
        String apkPath = FileUtil.copyFilesFromAssets(this, pluginAppFileName, apkDexPath);
        PluginLoader.getInstance().setContext(this);
        PluginLoader.getInstance().loadApk(apkPath);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void normalClassPluginClick(View button) {
        Intent intent = new Intent(this, NormalClassPluginActivity.class);
        this.startActivity(intent);
    }

    public void activityClassPluginClick(View button) {
        Intent intent = new Intent(this, ProxyActivity.class);
        String activityName = PluginLoader.getInstance().getPluginPackageArchiveInfo().activities[0].name;
        intent.putExtra("activityName", activityName);
        this.startActivity(intent);
    }
}
