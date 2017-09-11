package com.nicholas.httpwrapper;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.nicholas.models.PlaylistModel;
import com.nicholas.spotifyapp.PlaylistActivity;
import com.nicholas.spotifyapp.PlaylistListActivity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Nicholas on 9/8/2017.
 */

public class GetPlaylist extends OkHttpWrapper{
    public static final String USERS_FRAG =
            "https://api.spotify.com/v1/users/";
    public static final String PLAYLISTS_FRAG =
            "/playlists/";
    public static final String TRACKS_FRAG =
            "/tracks";
    public static final String PLAYLIST_JSON_KEY =
            "GetPlaylist.JsonKey";

    public GetPlaylist(Activity call) {
        super(call);
    }

    public void getCurrentUserPlaylist(PlaylistModel playlist) {
        String userId = "";
        try {
            userId = userJson.getString("id");
        } catch (JSONException e) {
            Log.d("getPlaylist", "");
        }

        this.authAsyncGet(USERS_FRAG + playlist.ownerId
                + PLAYLISTS_FRAG + playlist.id + TRACKS_FRAG);
    }

    @Override
    protected void onPostExecute(String response) {
        //start playlist activity
        Intent intent = new Intent(caller, PlaylistActivity.class);
        intent.putExtra(PLAYLIST_JSON_KEY, response);
        caller.startActivity(intent);
        return;
    }
}
