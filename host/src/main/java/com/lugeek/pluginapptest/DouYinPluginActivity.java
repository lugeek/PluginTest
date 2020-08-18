package com.lugeek.pluginapptest;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceView;
import android.view.View;

import com.lugeek.plugin_base.FileUtil;
import com.lugeek.plugin_base.PluginLoader;
import com.ss.android.ugc.aweme.plugin_interface.player.ISyncPlayer;

public class DouYinPluginActivity extends AppCompatActivity {

    private SurfaceView mSurfaceView;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        String apkDexPath = FileUtil.getCacheDirectory(this, Environment.DIRECTORY_DOWNLOADS).getPath();
        String apkPath = FileUtil.copyFilesFromAssets(this, "douyin.zip", apkDexPath);
        PluginLoader.getInstance().setContext(this);
        PluginLoader.getInstance().loadApk(apkPath);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_douyin);
        mSurfaceView = findViewById(R.id.surface_view);
    }

    public void onButtonClick(View view) {
        ISyncPlayer syncPlayer = PluginLoader.getInstance().loadClass(
                "com.bytedance.common.aweme_lite.plugin_ijk.IjkPlayer");
        syncPlayer.setSurface(mSurfaceView.getHolder().getSurface());
        syncPlayer.prepareAsync(this, "https://www.w3school.com.cn/example/html5/mov_bbb.mp4", true);
        syncPlayer.setLooping(true);
        syncPlayer.start();
    }
}
