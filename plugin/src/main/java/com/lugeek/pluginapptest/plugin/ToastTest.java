package com.lugeek.pluginapptest.plugin;

import android.content.Context;
import android.widget.Toast;

public class ToastTest {

    private String mToast = "plugin";

    public String getToast() {
        return mToast;
    }

    public void setToast(String toast) {
        this.mToast = toast;
    }

    public void startToast(Context context) {
        Toast.makeText(context, mToast, Toast.LENGTH_LONG).show();
    }

}
