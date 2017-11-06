package com.nicholas.spotifyapp;

import android.app.ListActivity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.nicholas.States.PlayerState;
import com.nicholas.States.UserState;
import com.nicholas.httpwrapper.GetPlaylist;
import com.nicholas.httpwrapper.ResponseTransferHelper;
import com.nicholas.models.PlaylistModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static com.nicholas.httpwrapper.GetPlaylists.PLAYLISTS_JSON_KEY;

/**
 * Created by Nicholas on 9/6/2017.
 */

public class PlaylistsActivity extends ListActivity implements SensorEventListener {

    // This is the Adapter being used to display the list's data
    private ArrayAdapter<PlaylistModel> mAdapter;
   // ListView listView;
   private ArrayList<PlaylistModel> playlists = new ArrayList<>();

    // shake variables
    private static final float SHAKE_THRESHOLD = 3.25f;
    private static final int MIN_TIME_BETWEEN_SHAKES_MILLISECS = 3000;
    private long lastShakeTime;
    private SensorManager sensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // Get ListView object from xml
        setContentView(R.layout.activity_playlists);

        // load saved info into UserState
        UserState.loadUser(ResponseTransferHelper.getInstance().getValue("userId"));

        Intent intent = getIntent();
        String response = ResponseTransferHelper.getInstance().getValue(PLAYLISTS_JSON_KEY);
        playlists = parsePlaylistListString(response);

        // Create an empty adapter we will use to display the loaded data.
        // We pass null for the cursor, then update it in onLoadFinished()
        mAdapter =  new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, playlists);
        setListAdapter(mAdapter);

        // sync with userData
        ArrayList<String> playlistIds = new ArrayList<>();
        for(PlaylistModel playlist : playlists) {
            playlistIds.add(playlist.id);
        }
        UserState.syncUser(playlistIds);

        // register SensorManager
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        final Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if(accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        PlaylistModel selected = playlists.get(position);
        GetPlaylist getPlaylist = new GetPlaylist(this);
        getPlaylist.getCurrentUserPlaylist(selected);
    }

    private ArrayList<PlaylistModel> parsePlaylistListString(String jsonString) {
        ArrayList<PlaylistModel> playlistList = new ArrayList<>();
        try {
            JSONObject jsonResponse = new JSONObject(jsonString);

            int total = jsonResponse.getInt("total");
            for (int i = 0; i < total; i++) {
                PlaylistModel playlist = new PlaylistModel();
                JSONObject playlistJson = jsonResponse.getJSONArray("items")
                        .getJSONObject(i);
                playlist.name = playlistJson.getString("name");
                playlist.id = playlistJson.getString("id");
                playlist.ownerId = playlistJson.getJSONObject("owner").getString("id");
                playlistList.add(playlist);
            }
        } catch ( JSONException e) {
            Log.d("GetPlaylists oPE", "while parsing response into JSON");
        }
        return playlistList;
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
                    Log.d("Playlists", "PAUSE");
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}