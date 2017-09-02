package com.nicholas.httpwrapper;

import android.util.Log;

import com.nicholas.models.PlaylistModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nicholas on 9/2/2017.
 */

public class GetPlaylists extends OkHttpWrapper {

    public static final String CURRENT_USER_PLAYLISTS_URL =
            "https://api.spotify.com/v1/me/playlists";

    public GetPlaylists(String auth) {
        super(auth);
    }

    public void getCurrentUserPlaylists() {
        this.authAsyncGet(CURRENT_USER_PLAYLISTS_URL);
    }

    @Override
    protected void onPostExecute(String response) {
        List<PlaylistModel> playlistList = new ArrayList<>();
        try {
            JSONObject jsonResponse = new JSONObject(response);

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
        return;
    }
}
