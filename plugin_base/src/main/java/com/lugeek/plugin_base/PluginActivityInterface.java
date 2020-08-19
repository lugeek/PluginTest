package com.lugeek.plugin_base;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public interface PluginActivityInterface {

    void onCreate(Bundle saveInstance);

    void attachContext(Activity context);

    Resources getResources();

    void onStart();

    void onResume();

    void onRestart();

    void onDestroy();

    void onStop();

    void onPause();

}
