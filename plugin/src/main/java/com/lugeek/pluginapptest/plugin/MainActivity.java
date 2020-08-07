package com.lugeek.pluginapptest.plugin;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.lugeek.plugin_base.PluginActivityInterface;
import com.lugeek.pluginapptest.R;

public class MainActivity extends AppCompatActivity implements PluginActivityInterface {

    private AppCompatActivity mHostContext;

    @SuppressLint("MissingSuperCall")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        mHostContext.setContentView(R.layout.activity_main);
        mHostContext.findViewById(R.id.zan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mHostContext, "点赞👍", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void attachContext(AppCompatActivity context) {
        mHostContext = context;
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
