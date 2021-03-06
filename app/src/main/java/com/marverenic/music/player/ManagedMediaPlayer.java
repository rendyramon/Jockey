package com.marverenic.music.player;

import android.content.Context;
import android.media.MediaDataSource;
import android.media.MediaPlayer;
import android.net.Uri;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.Map;

import timber.log.Timber;

/**
 * An extension of {@link MediaPlayer} that keeps track of its state and does not throw errors
 * (and therefore end playback) when a method is called in an invalid state.
 */
public class ManagedMediaPlayer extends MediaPlayer implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {

    public enum Status {
        IDLE, INITIALIZED, PREPARING, PREPARED, STARTED, PAUSED, STOPPED, COMPLETED
    }

    private Status state;
    private OnPreparedListener onPreparedListener;
    private OnCompletionListener onCompletionListener;
    private OnErrorListener onErrorListener;

    public ManagedMediaPlayer() {
        super();
        super.setOnCompletionListener(this);
        super.setOnPreparedListener(this);
        super.setOnErrorListener(this);
        state = Status.IDLE;
    }

    @Override
    public void reset() {
        super.reset();
        state = Status.IDLE;
    }

    @Override
    public void setDataSource(Context context, Uri uri) throws IOException {
        Timber.i("setDataSource(Context, %s) called", uri);

        if (state == Status.IDLE) {
            super.setDataSource(context, uri);
            state = Status.INITIALIZED;
        } else {
            Timber.i("Attempted to set data source, but media player was in state %s", state);
        }
    }

    @Override
    public void setDataSource(String path) throws IOException {
        Timber.i("setDataSource(%s) called", path);

        if (state == Status.IDLE) {
            super.setDataSource(path);
            state = Status.INITIALIZED;
        } else {
            Timber.i("Attempted to set data source, but media player was in state %s", state);
        }
    }

    @Override
    public void setDataSource(Context context, Uri uri, Map<String, String> headers)
            throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {

        Timber.i("setDataSource(Context, %s, Map<>) called", uri);

        if (state == Status.IDLE) {
            super.setDataSource(context, uri, headers);
            state = Status.INITIALIZED;
        } else {
            Timber.i("Attempted to set data source, but media player was in state %s", state);
        }
    }

    @Override
    public void setDataSource(FileDescriptor fd) throws IOException, IllegalArgumentException,
            IllegalStateException {

        Timber.i("setDataSource(%s) called", fd);

        if (state == Status.IDLE) {
            super.setDataSource(fd);
            state = Status.INITIALIZED;
        } else {
            Timber.i("Attempted to set data source, but media player was in state %s", state);
        }
    }

    @Override
    public void setDataSource(FileDescriptor fd, long offset, long length) throws IOException,
            IllegalArgumentException, IllegalStateException {

        Timber.i("setDataSource(%s, long, long) called", fd);

        if (state == Status.IDLE) {
            super.setDataSource(fd, offset, length);
            state = Status.INITIALIZED;
        } else {
            Timber.i("Attempted to set data source, but media player was in state %s", state);
        }
    }

    @Override
    public void setDataSource(MediaDataSource dataSource) throws IllegalArgumentException,
            IllegalStateException {

        Timber.i("setDataSource(%s) called", dataSource);

        if (state == Status.IDLE) {
            super.setDataSource(dataSource);
            state = Status.INITIALIZED;
        } else {
            Timber.i("Attempted to set data source, but media player was in state %s", state);
        }
    }

    @Override
    public void prepareAsync() {
        Timber.i("prepareAsync() called");

        if (state == Status.INITIALIZED || state == Status.STOPPED) {
            super.prepareAsync();
            state = Status.PREPARING;
        } else {
            Timber.i("Attempted to prepare async, but media player was in state %s", state);
        }
    }

    @Override
    public void prepare() throws IOException {
        Timber.i("prepare() called");

        if (state == Status.INITIALIZED) {
            super.prepare();
            state = Status.PREPARING;
        } else {
            Timber.i("Attempted to prepare, but media player was in state %s", state);
        }
    }

    @Override
    public void setOnPreparedListener(OnPreparedListener listener) {
        this.onPreparedListener = listener;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        state = Status.PREPARED;
        if (onPreparedListener != null) {
            onPreparedListener.onPrepared(mp);
        }
    }

    @Override
    public void start() {
        Timber.i("start() called");

        if (state == Status.PREPARED || state == Status.STARTED || state == Status.PAUSED
                || state == Status.COMPLETED) {
            super.start();
            state = Status.STARTED;
        } else {
            Timber.i("Attempted to start, but media player was in state %s", state);
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        state = Status.COMPLETED;
        if (onCompletionListener != null) {
            onCompletionListener.onCompletion(mp);
        }
    }

    @Override
    public void setOnCompletionListener(OnCompletionListener listener) {
        this.onCompletionListener = listener;
    }

    @Override
    public void setOnErrorListener(OnErrorListener listener) {
        onErrorListener = listener;
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        if (onErrorListener != null && !onErrorListener.onError(mp, what, extra)) {
            Timber.i("An error occurred and the player was reset");
            reset();
        }
        return true;
    }

    @Override
    public void seekTo(int mSec) {
        Timber.i("seekTo(%d) called", mSec);

        if (state == Status.PREPARED || state == Status.STARTED || state == Status.PAUSED) {
            super.seekTo(mSec);
        } else if (state == Status.COMPLETED) {
            start();
            pause();
            super.seekTo(mSec);
        } else {
            Timber.i("Attempted to set seek, but media player was in state %s", state);
        }
    }

    @Override
    public void stop() {
        Timber.i("stop() called");

        if (state == Status.STARTED || state == Status.PAUSED || state == Status.COMPLETED) {
            super.stop();
            state = Status.STOPPED;
        } else {
            Timber.i("Attempted to stop, but media player was in state %s", state);
        }
    }

    @Override
    public void pause() {
        Timber.i("pause() called");

        if (state == Status.STARTED || state == Status.PAUSED) {
            super.pause();
            state = Status.PAUSED;
        } else {
            Timber.i("Attempted to pause, but media player was in state %s", state);
        }
    }

    @Override
    public int getCurrentPosition() {
        if (state == Status.COMPLETED) {
            return getDuration();
        } else if (state == Status.PREPARED || state == Status.STARTED || state == Status.PAUSED) {
            return super.getCurrentPosition();
        } else {
            return 0;
        }
    }

    @Override
    public int getDuration() {
        if (state == Status.PREPARED || state == Status.STARTED || state == Status.PAUSED
                || state == Status.COMPLETED) {
            return super.getDuration();
        } else {
            return 0;
        }
    }

    /**
     * @return The current state of this ManagedMediaPlayer.
     */
    public Status getState() {
        return state;
    }

    public boolean isComplete() {
        return state == Status.COMPLETED;
    }

    public boolean isPrepared() {
        return state == Status.PREPARED;
    }
}
