package com.nikitadev.recordsounds.tasks;

import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.util.Log;
import android.widget.ProgressBar;

import com.nikitadev.recordsounds.MainActivity;
import com.nikitadev.recordsounds.settings.RecordSettings;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.SupposeBackground;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Nikita on 01.03.2015.
 */
@EBean
public class AudioPlayBackgroundTask {

    private AtomicBoolean mIsPlaying;

    private final int mBufferSize;
    private final AudioTrack mAudioTrack;

    public AudioPlayBackgroundTask() {

        mBufferSize = RecordSettings.getBufferSize(4, RecordSettings.CHANNEL_PLAY_CONFIG);

        mIsPlaying = new AtomicBoolean(false);

        mAudioTrack = new AudioTrack(
                AudioManager.STREAM_MUSIC,
                RecordSettings.SAMPLE_RATE,
                RecordSettings.CHANNEL_PLAY_CONFIG,
                RecordSettings.AUDIO_ENCODING,
                mBufferSize,
                AudioTrack.MODE_STREAM);
    }

    public void play() {
        mAudioTrack.play();
    }

    @Background
    public void write(byte[] buffer) {

        if (buffer == null)
            return;

        InputStream inputStream = new ByteArrayInputStream(buffer);
        byte[] bytes = new byte[mBufferSize];

        try {

            mIsPlaying.lazySet(true);

            int length = 0;
            while ((length = inputStream.read(bytes)) != -1) {
                mAudioTrack.write(bytes, 0, length);
            }

            mIsPlaying.lazySet(false);

        } catch (IOException ex) {
            Log.e("AudioPlayBackgroundTask", "Fail to read input data.", ex);
        } finally {
            mIsPlaying.set(false);
        }
    }

    public void stop() {
        mAudioTrack.stop();
    }

    public void release() {
        stop();
        mAudioTrack.release();
    }

    public boolean getIsPlaying() {
        return mIsPlaying.get();
    }
}
