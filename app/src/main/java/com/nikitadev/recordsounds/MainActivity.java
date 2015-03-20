package com.nikitadev.recordsounds;

import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.nikitadev.recordsounds.tasks.AudioPlayBackgroundTask;
import com.nikitadev.recordsounds.tasks.RecordingBackgroundTask;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.NonConfigurationInstance;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_main)
public class MainActivity extends ActionBarActivity {

    @ViewById(R.id.toggleButtonRec)
    ToggleButton mToggleButtonRec;

    @ViewById(R.id.textView)
    TextView mTextView;

    @NonConfigurationInstance
    @Bean
    RecordingBackgroundTask mTaskRecording;

    @NonConfigurationInstance
    @Bean
    AudioPlayBackgroundTask mTaskPlaying;

    @AfterViews
    void init() {

        String androidID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        mTextView.setText(androidID);

        mToggleButtonRec.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    mTaskRecording.recording();
                    mTaskPlaying.play();
                } else {
                    mTaskRecording.stop();
                    mTaskPlaying.stop();
                }
            }
        });
    }

    @UiThread
    public void writeSound(byte[] bytes) {

        mTaskPlaying.write(bytes);
    }

    @UiThread
    public void updateVisualizer(byte[] bytes) {

        //mSoundVisualizerView.updateVisualizer(bytes);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {

        if (mTaskRecording != null) {
            mTaskRecording.release();
        }

        if (mTaskPlaying != null) {
            mTaskPlaying.release();
        }

        super.onDestroy();
    }

    public boolean getIsBusy() {
        return mTaskPlaying.getIsPlaying();
    }
}
