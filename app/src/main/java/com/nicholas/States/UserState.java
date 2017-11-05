package com.nicholas.States;

import android.util.Log;
import android.util.Pair;

import com.nicholas.managers.FileManager;
import com.nicholas.models.SongModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


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

        Log.e("UserState", "saveUserData: Saved new JSON to file system");
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

    public static void syncPlaylist(String playlistId, ArrayList<SongModel> songs) {
        Map<String, SongModel> idsToSongs = new HashMap<>();
        for(SongModel song : songs) {
            idsToSongs.put(song.id, song);
        }

        // remove from JSON if old
        try {
            ArrayList<String> songsToRemove = new ArrayList<>();
            for (Iterator<String> it = userData.getJSONObject(playlistId).keys(); it.hasNext(); ) {
                String songId = it.next();
                if(!idsToSongs.keySet().contains(songId)) {
                    songsToRemove.add(songId);
                } else {
                    if (userData.getJSONObject(playlistId).has(songId)) {
                        idsToSongs.get(songId).startTime = Integer.parseInt(userData.getJSONObject(playlistId).getJSONObject(songId).getString("start"));
                        idsToSongs.get(songId).endTime = Integer.parseInt(userData.getJSONObject(playlistId).getJSONObject(songId).getString("end"));
                    }
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

    public static void editSong(String playlistId, SongModel song, String start, String end) {

        try {
            // change values in JSON for start and stop if song entry exists
            if(userData.getJSONObject(playlistId).has(song.id)) {
                JSONObject jSong = userData.getJSONObject(playlistId).getJSONObject(song.id);
                jSong.put("start", start);
                jSong.put("end", end);
            } else {
                // create new JSON object for this song
                JSONObject jSong = new JSONObject();
                jSong.put("start", start);
                jSong.put("end", end);
                userData.getJSONObject(playlistId).put(song.id, jSong);
            }

        } catch(Exception e) {
            Log.e("UserState", "editSong: Error editing song JSON");
        }

        saveUserData();
    }

    public static Pair<Integer, Integer> getSongTimes(String playlistId, SongModel song) {
        int start = 0;
        int end = song.durationMs;
        try {
            if(userData.getJSONObject(playlistId).has(song.id)) {
                start = Integer.valueOf(userData.getJSONObject(playlistId).getJSONObject(song.id).get("start").toString());
                end = Integer.valueOf(userData.getJSONObject(playlistId).getJSONObject(song.id).get("end").toString());
            }
        } catch(Exception e) {

        }
        return new Pair<>(start, end);
    }
}
