package com.nicholas.spotifyapp;

import android.app.ActionBar;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;

import com.nicholas.models.PlaylistModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.nicholas.httpwrapper.GetPlaylists.PLAYLISTS_JSON_KEY;

/**
 * Created by Nicholas on 9/6/2017.
 */

public class PlaylistListActivity extends ListActivity {

    // This is the Adapter being used to display the list's data
    private ArrayAdapter<PlaylistModel> mAdapter;
   // ListView listView;
   private ArrayList<PlaylistModel> playlists = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get ListView object from xml
        //listView = findViewById(R.id.list);
        setContentView(R.layout.activity_playlist_list);

        Intent intent = getIntent();
        String response = intent.getStringExtra(PLAYLISTS_JSON_KEY);
        playlists = parsePlaylistString(response);

        // Create an empty adapter we will use to display the loaded data.
        // We pass null for the cursor, then update it in onLoadFinished()
        mAdapter =  new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, playlists);
        //listView.setAdapter(mAdapter);
        setListAdapter(mAdapter);


    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // Do something when a list item is clicked
    }

    private ArrayList<PlaylistModel> parsePlaylistString(String jsonString) {
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
                playlistList.add(playlist);
            }
        } catch ( JSONException e) {
            Log.d("GetPlaylists oPE", "while parsing response into JSON");
        }
        return playlistList;
    }
}