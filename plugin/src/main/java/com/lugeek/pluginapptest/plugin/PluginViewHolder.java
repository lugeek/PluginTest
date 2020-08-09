package com.lugeek.pluginapptest.plugin;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lugeek.plugin_base.PluginLoader;
import com.lugeek.plugin_base.PluginViewHolderInterface;
import com.lugeek.pluginapptest.R;

public class PluginViewHolder extends RecyclerView.ViewHolder implements PluginViewHolderInterface {

    private TextView indexView;
    private ImageView imageView;

    public PluginViewHolder(Context context, ViewGroup parent) {
        super(LayoutInflater.from(PluginLoader.getInstance().getPluginContextWrapper(context)).inflate(R.layout.view_holder, parent, false));
        indexView = itemView.findViewById(R.id.index);
        imageView = itemView.findViewById(R.id.img);
    }

    @Override
    public void refreshData(int index) {
        indexView.setText("" + index);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.setImageResource(R.drawable.plugin);
            }
        });
    }
}