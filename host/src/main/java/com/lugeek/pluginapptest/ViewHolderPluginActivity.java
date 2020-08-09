package com.lugeek.pluginapptest;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.lugeek.plugin_base.PluginLoader;
import com.lugeek.plugin_base.PluginViewHolderInterface;

public class ViewHolderPluginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_holder_plugin);

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        MyAdapter adapter = new MyAdapter(this);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

    public static class MyAdapter extends RecyclerView.Adapter {

        private Context context;

        public MyAdapter(Context context) {
            super();
            this.context = context;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            RecyclerView.ViewHolder vh = PluginLoader.getInstance().loadClass(
                    "com.lugeek.pluginapptest.plugin.PluginViewHolder", (Context)context, (ViewGroup)viewGroup);
            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            if (viewHolder instanceof PluginViewHolderInterface) {
                ((PluginViewHolderInterface) viewHolder).refreshData(i);
            }
        }

        @Override
        public int getItemCount() {
            return 20;
        }
    }
}
