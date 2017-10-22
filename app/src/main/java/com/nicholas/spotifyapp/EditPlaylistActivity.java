package com.nicholas.spotifyapp;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.nicholas.States.PlayerState;
import com.nicholas.States.UserState;
import com.nicholas.httpwrapper.ResponseTransferHelper;
import com.nicholas.managers.FileManager;
import com.nicholas.models.SongModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.nicholas.httpwrapper.GetPlaylist.PLAYLIST_JSON_KEY;

public class EditPlaylistActivity extends ListActivity {

    // This is the Adapter being used to display the list's data
    private ArrayAdapter<SongModel> mAdapter;
    // ListView listView;
    private ArrayList<SongModel> songs = new ArrayList<>();

    private String playlistId;

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
        mAdapter =  new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, songs);
        setListAdapter(mAdapter);

        // ensure entry for playlist exists
        UserState.editPlaylist(playlistId);

        // sync with userData playlist
        ArrayList<String> songIds = new ArrayList<>();
        for(SongModel song : songs) {
            songIds.add(song.id);
        }
        UserState.syncPlaylist(playlistId, songIds);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        String trackId = songs.get(position).id;
        PlayerState.player.playUri(null, "spotify:track:" + trackId, 0, 0);
        FileManager.writeFile("lastSongId.txt", trackId);

        Intent intent = new Intent(this, EditSongActivity.class);
        ResponseTransferHelper.getInstance().addPair("trackId", trackId);
        ResponseTransferHelper.getInstance().addPair("playlistId", playlistId);
        startActivity(intent);
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
                songList.add(song);
            }
        } catch ( JSONException e) {
            Log.d("GetPlaylist oPE", "while parsing response into JSON");
        }
        return songList;
    }
}
