package com.nicholas.httpwrapper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.nicholas.models.PlaylistModel;
import com.nicholas.spotifyapp.PlaylistListActivity;

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
    public static final String PLAYLISTS_JSON_KEY =
            "GetPlaylists.JsonKey";

    public GetPlaylists(Activity call, String auth) {
        super(call, auth);
    }

    public void getCurrentUserPlaylists() {
        this.authAsyncGet(CURRENT_USER_PLAYLISTS_URL);
    }

    @Override
    protected void onPostExecute(String response) {
        //start playlist list activity
        Intent intent = new Intent(caller, PlaylistListActivity.class);
        intent.putExtra(PLAYLISTS_JSON_KEY, response);
        caller.startActivity(intent);
        return;
    }
}
