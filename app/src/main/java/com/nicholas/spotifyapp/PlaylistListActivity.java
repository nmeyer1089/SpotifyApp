package com.nicholas.spotifyapp;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.nicholas.httpwrapper.GetPlaylist;
import com.nicholas.models.PlaylistModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

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
        playlists = parsePlaylistListString(response);

        // Create an empty adapter we will use to display the loaded data.
        // We pass null for the cursor, then update it in onLoadFinished()
        mAdapter =  new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, playlists);
        //listView.setAdapter(mAdapter);
        setListAdapter(mAdapter);


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
}