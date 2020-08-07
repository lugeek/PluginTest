package com.lugeek.pluginapptest.plugin;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lugeek.pluginapptest.R;

public class PluginViewHolder extends RecyclerView.ViewHolder {

    private TextView indexView;

    public PluginViewHolder(Context context, ViewGroup parent) {
        super(LayoutInflater.from(context).inflate(R.layout.view_holder, parent, false));
        indexView = itemView.findViewById(R.id.index);
    }

    public void setIndex(int index) {
        indexView.setText("" + index);
    }

}