package com.nicholas.httpwrapper;

import com.nicholas.models.SongModel;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Drew Relyea on 10/17/2017.
 */

public class ResponseTransferHelper {
    private static final ResponseTransferHelper ourInstance = new ResponseTransferHelper();
    private static Map<String, String> myMap = new HashMap<>();
    private static SongModel editingSong;
    private static int editingPosition;

    public static ResponseTransferHelper getInstance() {
        return ourInstance;
    }

    private ResponseTransferHelper() {
        editingSong = new SongModel();
        editingSong.name = "testName";
        editingSong.album = "testAlbum";
        editingSong.artist = "testArtist";
        editingSong.durationMs = 10000;
        editingSong.id = "testSongId";
    }

    public void addPair(String key, String response) {
        myMap.put(key, response);
    }

    public String getValue(String key) {
        return myMap.get(key);
    }

    public void setEditingSong(SongModel song, int pos) {
        editingSong = song;
        editingPosition = pos;
    }
    public SongModel getEditingSong() {
        return editingSong;
    }
    public int getEditingPosition() { return editingPosition; }
}
