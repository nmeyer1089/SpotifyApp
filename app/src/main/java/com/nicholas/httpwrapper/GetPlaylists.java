package com.nicholas.httpwrapper;

import android.app.Activity;
import android.content.Intent;

import com.nicholas.spotifyapp.PlaylistsActivity;

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
        Intent intent = new Intent(caller, PlaylistsActivity.class);
        ResponseTransferHelper.getInstance().addPair(PLAYLISTS_JSON_KEY, response);
        caller.startActivity(intent);
        return;
    }
}
