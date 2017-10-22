package com.nicholas.States;

import android.util.Log;
import android.util.Pair;

import com.nicholas.managers.FileManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;


/**
 * Created by Drew Relyea on 10/22/2017.
 */

public class UserState {

    private static final UserState ourInstance = new UserState();
    public static UserState getInstance() {
        return ourInstance;
    }

    private static JSONObject userData;
    private static String userId;

    private UserState() {
    }

    public static void loadUser(String id) {
        userId = id;
        String file = FileManager.readFile(userId+".txt");
        if(!file.equals("")) {
            try {
                userData = new JSONObject(file);
            } catch (Exception e) {
                Log.e("UserState", "loadUser: Error loading JSON from file");
            }
        } else {
            userData = new JSONObject();
        }
    }

    public static void saveUserData() {

        FileManager.writeFile(userId+".txt", userData.toString());
    }

    public static void syncUser(ArrayList<String> playlistIds) {

        // remove from JSON if old
        try {
            ArrayList<String> playlistsToRemove = new ArrayList<>();
            for (Iterator<String> it = userData.keys(); it.hasNext(); ) {
                String playlistId = it.next();
                if(!playlistIds.contains(playlistId)) {
                    playlistsToRemove.add(playlistId);
                }
            }
            for (String playlistId : playlistsToRemove) {
                userData.remove(playlistId);
            }
        } catch(Exception e) {
            Log.e("UserState", "syncUser: Error syncing playlists for user");
        }
    }

    public static void syncPlaylist(String playlistId, ArrayList<String> songIds) {

        // remove from JSON if old
        try {
            ArrayList<String> songsToRemove = new ArrayList<>();
            for (Iterator<String> it = userData.getJSONObject(playlistId).keys(); it.hasNext(); ) {
                String songId = it.next();
                if(!songIds.contains(songId)) {
                    songsToRemove.add(songId);
                }
            }
            for (String songId : songsToRemove) {
                userData.getJSONObject(playlistId).remove(songId);
            }

        } catch(Exception e) {
            Log.e("UserState", "syncPlaylist: Error syncing songs in playlist");
        }
    }

    public static void editPlaylist(String playlistId) {

        try {
            // create new JSON object for this playlist if it does not exist
            if(!userData.has(playlistId)) {
                JSONObject playlist = new JSONObject();
                userData.put(playlistId, playlist);
            }
        } catch(Exception e) {
            Log.e("UserState", "editPlaylist: Error editing playlist JSON");
        }
    }

    public static void editSong(String playlistId, String songId, String start, String end) {

        try {
            // change values in JSON for start and stop if song entry exists
            if(userData.getJSONObject(playlistId).has(songId)) {
                JSONObject song = userData.getJSONObject(playlistId).getJSONObject(songId);
                song.put("start", start);
                song.put("end", end);
            } else {
                // create new JSON object for this song
                JSONObject song = new JSONObject();
                song.put("start", start);
                song.put("end", end);
                userData.getJSONObject(playlistId).put(songId, song);
            }

        } catch(Exception e) {
            Log.e("UserState", "editSong: Error editing song JSON");
        }

        saveUserData();
    }

    public static Pair<Integer, Integer> getSongTimes(String playlistId, String songId) {
        int start = 0;
        int end = 0;
        try {
            if(userData.getJSONObject(playlistId).has(songId)) {
                start = Integer.valueOf(userData.getJSONObject(playlistId).getJSONObject(songId).get("start").toString());
                end = Integer.valueOf(userData.getJSONObject(playlistId).getJSONObject(songId).get("end").toString());
            }
        } catch(Exception e) {

        }
        return new Pair<>(start, end);
    }
}
