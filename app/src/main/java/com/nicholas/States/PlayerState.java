package com.nicholas.States;

import com.nicholas.Actions.PlayNextAction;
import com.nicholas.models.SongModel;
import com.spotify.sdk.android.player.SpotifyPlayer;

import java.util.List;

/**
 * Created by Nicholas on 9/9/2017.
 */

public class PlayerState {
    private static SpotifyPlayer player;
    private static SongModel playingSong;
    private static List<SongModel> songs;
    private static int songIndex;

    public static  void setSongs(List<SongModel> q) {
        songs = q;
    }
    public static void setPlayer(SpotifyPlayer p) {
        player = p;
    }

    public static void playNextSong() {
        playSong(songIndex+1);
    }
    public static void playSong(int pos) {
        playSong(songs.get(pos).startTime, pos);
    }
    public static void playSong(int start, int pos) {
        playSong(start, songs.get(pos).endTime, pos);
    }
    public static void playSong(int start, int end, int pos) {
        playingSong = songs.get(pos);
        songIndex = pos;
        player.playUri(null, "spotify:track:" + playingSong.id, 0, start);

        TimerState.startTimer(end - start, new PlayNextAction());
    }
}
