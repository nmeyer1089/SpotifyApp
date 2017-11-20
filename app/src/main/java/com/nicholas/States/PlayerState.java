package com.nicholas.States;

import com.nicholas.Actions.PlayNextAction;
import com.nicholas.adapters.PlaylistAdapter;
import com.nicholas.httpwrapper.ResponseTransferHelper;
import com.nicholas.models.SongModel;
import com.nicholas.spotifyapp.EditSongActivity;
import com.spotify.sdk.android.player.SpotifyPlayer;

import java.util.List;

/**
 * Created by Nicholas on 9/9/2017.
 */

public class PlayerState {
    private static SpotifyPlayer player;
    public static SongModel playingSong;
    private static List<SongModel> songs;
    public static int songIndex;

    public static int elapsedTimeSec = 0;
    public static boolean isPlaying = false;

    public static long getPositionMs() { return player.getPlaybackState().positionMs; }

    public static void setSongs(List<SongModel> q) {
        songs = q;
    }

    public static void setPlayer(SpotifyPlayer p) {
        player = p;
    }

    public static void playNextSong() {
        playSong(songIndex + 1);
    }

    public static void playSong(int pos) {
        playSong(songs.get(pos).startTime, pos);
    }

    public static void playSong(int start, int pos) {
        playSong(start, songs.get(pos).endTime, pos);
        isPlaying = true;
    }

    public static void playSong(int start, int end, int pos) {
        String lastId = "";
        if(playingSong != null) {
            lastId = playingSong.id;
        }
        playingSong = songs.get(pos);
        songIndex = pos;
        player.playUri(null, "spotify:track:" + playingSong.id, 0, start);

        TimerState.startTimer(end - start, new PlayNextAction());

        PlaylistAdapter.updateSelection(lastId, playingSong.id);

        EditSongActivity.setButtonsEnabled(ResponseTransferHelper.getInstance().getEditingSong() == playingSong);
    }

    public static void togglePlaying() {

        if(isPlaying) {
            TimerState.stopTimer();
            player.pause(null);
            isPlaying = false;
        } else {
            playSong(playingSong.startTime+(elapsedTimeSec*1000), songIndex);
            isPlaying = true;
        }

    }

}
