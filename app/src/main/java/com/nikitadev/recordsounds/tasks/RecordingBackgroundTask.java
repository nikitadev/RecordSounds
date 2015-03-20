package com.nikitadev.recordsounds.tasks;

import android.media.AudioRecord;
import android.media.MediaRecorder;

import com.nikitadev.recordsounds.MainActivity;
import com.nikitadev.recordsounds.models.WaveData;
import com.nikitadev.recordsounds.settings.RecordSettings;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.UiThread;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Nikita on 14.02.2015.
 */
@EBean
public class RecordingBackgroundTask {

    private int mSize;

    private WaveData mWaveData;

    private AtomicBoolean mIsReading;

    private final int mBufferSize;
    private final AudioRecord mAudioRecord;

    @RootContext
    MainActivity mMainActivity;

    public RecordingBackgroundTask() {

        mSize = 0;

        mBufferSize = RecordSettings.getBufferSize(1, RecordSettings.CHANNEL_REC_CONFIG);

        mWaveData = new WaveData();

        mIsReading = new AtomicBoolean(false);

        mAudioRecord = new AudioRecord(
                MediaRecorder.AudioSource.MIC,
                RecordSettings.SAMPLE_RATE,
                RecordSettings.CHANNEL_REC_CONFIG,
                RecordSettings.AUDIO_ENCODING,
                mBufferSize);
    }

    @Background
    public void recording() {

        mAudioRecord.startRecording();

        mIsReading.set(true);

        byte[] audioBuffer = new byte[mBufferSize];

        while (mIsReading.get()) {

            if (!mMainActivity.getIsBusy()) {

                int length = mAudioRecord.read(audioBuffer, 0, mBufferSize);

                writeSound(audioBuffer);

                updateUI(audioBuffer);

                if (length > 0) {
                    mWaveData.add(audioBuffer);
                }

                mSize += length;
            }

            updateUI(null);
        }
    }

    // Notice that we manipulate the activity ref only from the UI thread
    @UiThread
    void updateUI(byte[] buffer) {
        mMainActivity.updateVisualizer(buffer);
    }

    @UiThread(propagation = UiThread.Propagation.REUSE)
    void writeSound(byte[] buffer) {
        mMainActivity.writeSound(buffer);
    }

    public void stop() {
        mAudioRecord.stop();
        mIsReading.lazySet(false);
    }

    public void release() {
        stop();

        mAudioRecord.release();
    }

    public byte[] getAllBytes() {

        if (mSize == 0)
            return null;

        return mWaveData.getByteArray();
    }
}