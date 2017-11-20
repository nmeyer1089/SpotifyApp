package com.nicholas.spotifyapp;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.nicholas.States.PlayerState;
import com.nicholas.States.UserState;
import com.nicholas.httpwrapper.ResponseTransferHelper;
import com.nicholas.managers.FileManager;
import com.nicholas.models.SongModel;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static com.nicholas.httpwrapper.GetPlaylist.PLAYLIST_JSON_KEY;

public class EditSongActivity extends Activity implements SensorEventListener{

    private static ProgressBar currProgBar;
    private static Button currStartBut;
    private static Button currEndBut;

    private static int window = 3000;
    private String playlistId = "";
    private SeekBar startBar;
    private SeekBar endBar;

    // shake variables
    private static final float SHAKE_THRESHOLD = 3.25f;
    private static final int MIN_TIME_BETWEEN_SHAKES_MILLISECS = 3000;
    private long lastShakeTime;
    private SensorManager sensorManager;
    private static String songId = "";

    protected boolean isTest = false;

    protected String miliToTimestamp(int mili) {
        String secs = Integer.toString(mili/1000 % 60);
        String mins = Integer.toString(mili / 60000);
        if (secs.length() == 1) {
            return mins + ":0" + secs;
        }
        return mins + ":" + secs;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_song);

        // get info
        playlistId = ResponseTransferHelper.getInstance().getValue("playlistId");
        final SongModel editingSong = ResponseTransferHelper.getInstance().getEditingSong();
        songId = editingSong.id;
        final int editingPos = ResponseTransferHelper.getInstance().getEditingPosition();

        // set song name text
        TextView songName = (TextView) findViewById(R.id.song_name);
        songName.setText(ResponseTransferHelper.getInstance().getEditingSong().name);

        // set seek bar initial values
        startBar = (SeekBar) findViewById(R.id.start_seek);
        startBar.setMax(editingSong.durationMs);
        endBar = (SeekBar) findViewById(R.id.end_seek);
        endBar.setMax(editingSong.durationMs);
        final TextView startLabel = (TextView) findViewById(R.id.start_seek_label);
        final TextView endLabel = (TextView) findViewById(R.id.end_seek_label);
        startBar.setProgress(editingSong.startTime);
        endBar.setProgress(editingSong.endTime);
        startLabel.setText(miliToTimestamp(editingSong.startTime));
        endLabel.setText(miliToTimestamp(editingSong.endTime));

        //set up buttons
        final Button setStartBut = (Button) findViewById(R.id.set_start_but);
        final Button setEndBut = (Button) findViewById(R.id.set_end_but);
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.song_progress);
        currProgBar = progressBar;
        currEndBut = setEndBut;
        currStartBut = setStartBut;

        if (editingSong == PlayerState.playingSong) {
            setStartBut.setEnabled(true);
            setEndBut.setEnabled(true);
            progressBar.setEnabled(true);
        } else {
            setStartBut.setEnabled(false);
            setEndBut.setEnabled(false);
            progressBar.setEnabled(false);
        }

        // hook up seekbar listener
        final SeekBar.OnSeekBarChangeListener listener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                switch(seekBar.getId()) {
                    case R.id.start_seek:
                        startLabel.setText(miliToTimestamp(progress));
                        break;

                    case R.id.end_seek:
                        endLabel.setText(miliToTimestamp(progress));
                        break;
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                switch(seekBar.getId()) {
                    case R.id.start_seek:
                        if (startBar.getProgress() > endBar.getProgress()) { startBar.setProgress(endBar.getProgress()); }
                        editingSong.startTime = startBar.getProgress();
                        PlayerState.playSong(editingPos);
                        break;
                    case R.id.end_seek:
                        if (endBar.getProgress() < startBar.getProgress()) { endBar.setProgress(startBar.getProgress()); }
                        editingSong.endTime = endBar.getProgress();
                        if (editingSong.endTime - window > -1) {
                            PlayerState.playSong(editingSong.endTime - window, editingPos);
                        }
                        break;
                }
            }
        };
        startBar.setOnSeekBarChangeListener(listener);
        endBar.setOnSeekBarChangeListener(listener);

        setStartBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startBar.setProgress((int) PlayerState.getPositionMs());
                listener.onStopTrackingTouch(startBar);
            }
        });
        setEndBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endBar.setProgress((int) PlayerState.getPositionMs());
                listener.onStopTrackingTouch(endBar);
            }
        });

        // register SensorManager
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        final Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if(accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        // save song details
        if(!isTest) {
            UserState.editSong(playlistId, ResponseTransferHelper.getInstance().getEditingSong(), String.valueOf(startBar.getProgress()), String.valueOf(endBar.getProgress()));
        }
        sensorManager.unregisterListener(this);
        super.onPause();
    }

    @Override
    protected void onResume() {
        sensorManager.unregisterListener(this);
        final Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if(accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
        super.onResume();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            try {
                TimeUnit.MILLISECONDS.sleep(10);
            } catch (Exception e) {

            }
            long currTime = System.currentTimeMillis();
            if ((currTime - lastShakeTime) > MIN_TIME_BETWEEN_SHAKES_MILLISECS) {

                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                double acceleration = Math.sqrt(Math.pow(x, 2) +
                        Math.pow(y, 2) +
                        Math.pow(z, 2)) - SensorManager.GRAVITY_EARTH;

                if (acceleration > SHAKE_THRESHOLD) {
                    lastShakeTime = currTime;
                    PlayerState.togglePlaying();
                    Log.d("EditSong", "PAUSE");
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    public static void setProgressBar(int percent) {
        if(songId.equals(PlayerState.playingSong.id)) {
            if (currProgBar != null) {
                currProgBar.setProgress(percent);
            }
        } else {
            if(currProgBar != null) {
                currProgBar.setProgress(0);
            }
        }
    }
    public static void setButtonsEnabled(boolean b) {
        if (currEndBut != null && currStartBut != null && currProgBar != null) {
            currStartBut.setEnabled(b);
            currEndBut.setEnabled(b);
            currProgBar.setEnabled(b);
        }
    }
}
