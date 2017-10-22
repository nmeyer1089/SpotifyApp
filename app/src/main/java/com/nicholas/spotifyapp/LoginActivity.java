package com.nicholas.spotifyapp;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.nicholas.States.PlayerState;
import com.nicholas.httpwrapper.GetUser;
import com.nicholas.httpwrapper.OkHttpWrapper;
import com.nicholas.managers.FileManager;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

public class LoginActivity extends Activity implements
        SpotifyPlayer.NotificationCallback, ConnectionStateCallback
{


    private static final String CLIENT_ID = "24826f28c02c4c47aede12dcd3933607";
    private static final String CLIENT_SECRET = "";
    private static final String REDIRECT_URI = "http://localhost:8888/callback/";
    private static final String CODED_REDIRECT_URI = "https%3A%2F%2Flocalhost%3A8888%2Fcallback%2F";

    private static final String GET_AUTH =
            "https://accounts.spotify.com/authorize/?client_id=" + CLIENT_ID + "&response_type=code" +
                    "&redirect_uri=" + CODED_REDIRECT_URI; // + "&scope=user-read-private%20user-read-email&state=34fFs29kd09";

    // Request code that will be used to verify if the result comes from correct activity
    // Can be any integer
    private static final int REQUEST_CODE = 1337;

    private Player mPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FileManager.context = this;

        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID,
                AuthenticationResponse.Type.TOKEN,
                REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-private", "streaming"});
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);


        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            if (response.getType() == AuthenticationResponse.Type.TOKEN) {
                Config playerConfig = new Config(this, response.getAccessToken(), CLIENT_ID);

                OkHttpWrapper.authToken = response.getAccessToken();
                GetUser getUser = new GetUser(this);
                getUser.getCurrentUser();

                Spotify.getPlayer(playerConfig, this, new SpotifyPlayer.InitializationObserver() {
                    @Override
                    public void onInitialized(SpotifyPlayer spotifyPlayer) {
                        mPlayer = spotifyPlayer;
                        mPlayer.addConnectionStateCallback(LoginActivity.this);
                        mPlayer.addNotificationCallback(LoginActivity.this);

                        PlayerState.player = mPlayer;
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.e("LoginActivity", "Could not initialize player: " + throwable.getMessage());
                    }
                });
            }
        }
    }

    @Override
    protected void onDestroy() {
        // VERY IMPORTANT! This must always be called or else you will leak resources
        Spotify.destroyPlayer(this);
        super.onDestroy();
    }

    @Override
    public void onPlaybackEvent(PlayerEvent playerEvent) {
        Log.d("LoginActivity", "Playback event received: " + playerEvent.name());
        switch (playerEvent) {
            // Handle event type as necessary
            default:
                break;
        }
    }

    @Override
    public void onPlaybackError(Error error) {
        Log.d("LoginActivity", "Playback error received: " + error.name());
        switch (error) {
            // Handle error type as necessary
            default:
                break;
        }
    }

    @Override
    public void onLoggedIn() {
        Log.d("LoginActivity", "User logged in");

        String trackId = "2TpxZ7JUBn3uw46aR7qd6V";
        String temp = FileManager.readFile("lastSongId.txt");
        if (temp != null && temp.length() > 0) {
            trackId = temp;
        }

        //mPlayer.playUri(null, "spotify:track:" + trackId, 0, 0);
    }

    @Override
    public void onLoggedOut() {
        Log.d("LoginActivity", "User logged out");
    }

    @Override
    public void onLoginFailed(Error error) {
        Log.d("LoginActivity", "Login failed");
    }

    @Override
    public void onTemporaryError() {
        Log.d("LoginActivity", "Temporary error occurred");
    }

    @Override
    public void onConnectionMessage(String message) {
        Log.d("LoginActivity", "Received connection message: " + message);
    }
}