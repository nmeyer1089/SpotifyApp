package com.nicholas.spotifyapp;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.nicholas.States.PlayerState;
import com.nicholas.States.UserState;
import com.nicholas.httpwrapper.ResponseTransferHelper;
import com.nicholas.managers.FileManager;
import com.nicholas.models.SongModel;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

import static com.nicholas.httpwrapper.GetPlaylist.PLAYLIST_JSON_KEY;

public class EditSongActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_song);

        final String songId = ResponseTransferHelper.getInstance().getValue("trackId");
        final String playlistId = ResponseTransferHelper.getInstance().getValue("playlistId");
        final SongModel currentSong = ResponseTransferHelper.getInstance().getCurrentSong();

        TextView songName = (TextView) findViewById(R.id.song_name);
        songName.setText(songId);

        final SeekBar startBar = (SeekBar) findViewById(R.id.start_seek);
        startBar.setMax(currentSong.durationMs);
        final SeekBar endBar = (SeekBar) findViewById(R.id.end_seek);
        endBar.setMax(currentSong.durationMs);

        final TextView startLabel = (TextView) findViewById(R.id.start_seek_label);
        final TextView endLabel = (TextView) findViewById(R.id.end_seek_label);
        //Pair<Integer, Integer> times = UserState.getSongTimes(playlistId, currentSong);
        startBar.setProgress(currentSong.startTime);
        endBar.setProgress(currentSong.endTime);
        startLabel.setText(String.valueOf(currentSong.startTime));
        endLabel.setText(String.valueOf(currentSong.endTime));

        SeekBar.OnSeekBarChangeListener listener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                switch(seekBar.getId()) {
                    case R.id.start_seek:
                        startLabel.setText(String.valueOf(progress));
                        break;
                    case R.id.end_seek:
                        endLabel.setText(String.valueOf(progress));
                        break;
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // play song snippet here

                // update song info
                switch(seekBar.getId()) {
                    case R.id.start_seek:
                        if (startBar.getProgress() > endBar.getProgress()) { startBar.setProgress(endBar.getProgress()); }
                        currentSong.startTime = startBar.getProgress();
                        break;
                    case R.id.end_seek:
                        if (endBar.getProgress() < startBar.getProgress()) { endBar.setProgress(startBar.getProgress()); }
                        currentSong.endTime = endBar.getProgress();
                        break;
                }
                UserState.editSong(playlistId, currentSong, String.valueOf(startBar.getProgress()), String.valueOf(endBar.getProgress()));
            }
        };

        startBar.setOnSeekBarChangeListener(listener);
        endBar.setOnSeekBarChangeListener(listener);
    }

}
