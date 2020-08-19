package com.lugeek.pluginapptest.plugin;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.lugeek.plugin_base.PluginActivityInterface;
import com.lugeek.plugin_base.PluginContextWrapper;
import com.lugeek.plugin_base.PluginLoader;

public class MainActivity extends Activity implements PluginActivityInterface {

    private Activity mHostContext;
    private PluginContextWrapper mPluginContextWrapper;

    @SuppressLint("MissingSuperCall")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        mHostContext.setContentView(LayoutInflater.from(mPluginContextWrapper).inflate(R.layout.plugin_activity_main, null, false));
        mHostContext.findViewById(R.id.zan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mHostContext, "点赞👍", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void attachContext(Activity context) {
        mHostContext = context;
//        Resources.Theme theme = PluginLoader.getInstance().getPluginResources().newTheme();
//        theme.setTo(context.getTheme());
        mPluginContextWrapper = new PluginContextWrapper(context,
                PluginLoader.getInstance().getPluginResources(),
                PluginLoader.getInstance().getPluginDexClassLoader());
    }

    @Override
    public Resources getResources() {
        return PluginLoader.getInstance().getPluginResources();
    }

    @SuppressLint("MissingSuperCall")
    public void onStart() {
    }

    @SuppressLint("MissingSuperCall")
    public void onResume() {

    }

    @SuppressLint("MissingSuperCall")
    public void onRestart() {

    }

    @SuppressLint("MissingSuperCall")
    public void onDestroy() {

    }

    @SuppressLint("MissingSuperCall")
    public void onStop() {

    }

    @SuppressLint("MissingSuperCall")
    public void onPause() {

    }
}
