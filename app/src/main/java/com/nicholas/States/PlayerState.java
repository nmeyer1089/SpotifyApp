package com.nicholas.States;

import com.nicholas.models.SongModel;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.SpotifyPlayer;

import java.util.List;

/**
 * Created by Nicholas on 9/9/2017.
 */

public class PlayerState {
    //this is public because it helps with login/initialization
    //never actually call this directly please
    private static SpotifyPlayer player;
    private static SongModel playingSong;
    private static List<SongModel> queue;
    private static int songIndex;

    public static  void setQueue(List<SongModel> q) {
        queue = q;
    }
    public static void setPosition(int i) {
        songIndex = i;
    }
    public static void setPlayer(SpotifyPlayer p) {
        player = p;
    }

    public static void playSong(SongModel s) {
        playSong(s, s.startTime);
    }
    public static void playSong(SongModel s, int start) {
        playSong(s, start, s.endTime);
    }
    public static void playSong(SongModel s, int start, int end) {
        playingSong = s;
        player.playUri(null, "spotify:track:" + s.id, 0, start);

        //set timer for end - start milis
    }

    private void onFinishSong() {
        songIndex++;
        playingSong = queue.get(songIndex);
        playSong(playingSong, playingSong.startTime);
    }
}
