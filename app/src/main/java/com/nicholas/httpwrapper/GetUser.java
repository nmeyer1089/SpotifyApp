package com.nicholas.httpwrapper;

import android.app.Activity;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Nicholas on 9/8/2017.
 */

public class GetUser extends OkHttpWrapper {

    public static final String CURRENT_USER_URL =
            "https://api.spotify.com/v1/me";
    public GetUser (Activity call, String auth) {
        super(call, auth);
    }
    public GetUser (Activity call) { super(call); }

    public void getCurrentUser() {
        this.authAsyncGet(CURRENT_USER_URL);
    }

    @Override
    protected void onPostExecute(String response) {
        try {
            OkHttpWrapper.userJson = new JSONObject(response);
        } catch (JSONException e) {
            Log.d("GetUser", "Error parsing user Json");
        }

        // save user id in ResponseTransferHelper
        try {
            ResponseTransferHelper.getInstance().addPair("userId", OkHttpWrapper.userJson.getString("id"));
        } catch (Exception e) {

        }
        //get playlists
        GetPlaylists getPlaylists = new GetPlaylists(caller, authToken);
        getPlaylists.getCurrentUserPlaylists();
    }
}
