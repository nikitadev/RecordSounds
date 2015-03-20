package com.nikitadev.recordsounds.models;

import java.io.ByteArrayOutputStream;

/**
 * Created by Nikita on 09.02.2015.
 */
public class WaveData {

    private final static int MINSIZE = 32767;
    private final static int BUFFERSIZE_DEFAULT = 4*MINSIZE;

    private final ByteArrayOutputStream mByteArray;

    public WaveData() {

        mByteArray = new ByteArrayOutputStream(BUFFERSIZE_DEFAULT);
    }

    public void add(byte[] data) {
        mByteArray.write(data, 0, data.length);
    }

    public byte[] getByteArray() {
        return mByteArray.toByteArray();
    }
}
