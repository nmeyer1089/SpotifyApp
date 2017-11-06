package com.nicholas.spotifyapp;

import android.app.ListActivity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.nicholas.States.PlayerState;
import com.nicholas.States.UserState;
import com.nicholas.adapters.PlaylistAdapter;
import com.nicholas.httpwrapper.ResponseTransferHelper;
import com.nicholas.models.SongModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static com.nicholas.httpwrapper.GetPlaylist.PLAYLIST_JSON_KEY;

public class EditPlaylistActivity extends ListActivity implements SensorEventListener {

    // This is the Adapter being used to display the list's data
    private ArrayAdapter<SongModel> mAdapter;
    // ListView listView;
    private ArrayList<SongModel> songs = new ArrayList<>();

    private String playlistId;

    // shake variables
    private static final float SHAKE_THRESHOLD = 3.25f;
    private static final int MIN_TIME_BETWEEN_SHAKES_MILLISECS = 3000;
    private long lastShakeTime;
    private SensorManager sensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_playlist);

        Intent intent = getIntent();
        String response = ResponseTransferHelper.getInstance().getValue(PLAYLIST_JSON_KEY);
        songs = parsePlaylistString(response);
        playlistId = ResponseTransferHelper.getInstance().getValue("playlistId");

        // Create an empty adapter we will use to display the loaded data.
        // We pass null for the cursor, then update it in onLoadFinished()
        mAdapter =  new PlaylistAdapter(this, songs);
        setListAdapter(mAdapter);

        // ensure entry for playlist exists
        UserState.editPlaylist(playlistId);

        // sync with userData playlist
        UserState.syncPlaylist(playlistId, songs);

        PlayerState.setSongs(songs);

        // this used to be handled on click
        ResponseTransferHelper.getInstance().addPair("playlistId", playlistId);

        // register SensorManager
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        final Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if(accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }

    }

    private ArrayList<SongModel> parsePlaylistString(String jsonString) {
        ArrayList<SongModel> songList = new ArrayList<>();
        try {
            JSONObject jsonResponse = new JSONObject(jsonString);

            int numSongs = Math.min(
                    jsonResponse.getInt("total"), jsonResponse.getInt("limit"));
            for (int i = 0; i < numSongs; i++) {
                SongModel song = new SongModel();
                JSONObject trackJson = jsonResponse.getJSONArray("items")
                        .getJSONObject(i).getJSONObject("track");
                song.name = trackJson.getString("name");
                song.id = trackJson.getString("id");
                song.album = trackJson.getJSONObject("album").getString("name");
                song.artist = trackJson.getJSONArray("artists").getJSONObject(0).getString("name");
                song.durationMs = trackJson.getInt("duration_ms");
                song.endTime = song.durationMs;
                song.startTime = 0;
                songList.add(song);
            }
        } catch ( JSONException e) {
            Log.d("GetPlaylist oPE", "while parsing response into JSON");
        }
        return songList;
    }

    @Override
    protected void onPause() {
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
                    Log.d("EditPlaylist", "PAUSE");
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}
