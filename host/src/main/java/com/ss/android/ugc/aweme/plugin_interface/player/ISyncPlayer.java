package com.ss.android.ugc.aweme.plugin_interface.player;

import android.content.Context;
import android.view.Surface;

public interface ISyncPlayer {

    void setSurface(Surface surface);

    void setVolume(float f, float f2);

    long getCurrentPosition();

    long getDuration();

    void stop();

    void pause();

    void release();

    void reset();

    void start();

    void markResume(long j);

    boolean isPlaying();

    void setLooping(boolean z);

    void prepareAsync(Context context, String str, boolean z);

    int mapCode(int i);

    void setStartOnPrepared();

    void setLifecycleListener(ISyncPlayer.EmptyLifecycleListener emptyLifecycleListener);

    void setFastPrepared();

    public static class EmptyLifecycleListener implements LifecycleListener {

        @Override
        public void onRender() {

        }

        @Override
        public void onBuffering(boolean isBuffering) {

        }

        @Override
        public void onError(int a, int b, int c) {

        }

        @Override
        public void onCompletion() {

        }

        @Override
        public void onPrepared() {

        }
    }

    public static interface LifecycleListener {
        public abstract void onRender();
        public abstract void onBuffering(boolean isBuffering);
        public abstract void onError(int a, int b, int c);
        public abstract void onCompletion();
        public abstract void onPrepared();

    }

}
