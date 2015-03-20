package com.nikitadev.recordsounds.settings;

import android.media.AudioFormat;
import android.media.AudioRecord;

/**
 * Created by Nikita on 09.02.2015.
 */
public class RecordSettings {
    public static final int SAMPLE_RATE = 44100;
    public static final int CHANNEL_REC_CONFIG = AudioFormat.CHANNEL_IN_STEREO;
    public static final int CHANNEL_PLAY_CONFIG = AudioFormat.CHANNEL_OUT_STEREO;
    public static final int AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;

    public static int getBufferSize(int size, int config) {
        return size * AudioRecord.getMinBufferSize(SAMPLE_RATE, config, AUDIO_ENCODING);
    }
}
