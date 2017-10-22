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

        TextView songName = (TextView) findViewById(R.id.song_name);
        songName.setText(songId);

        final SeekBar startBar = (SeekBar) findViewById(R.id.start_seek);
        final SeekBar endBar = (SeekBar) findViewById(R.id.end_seek);
        final TextView startLabel = (TextView) findViewById(R.id.start_seek_label);
        final TextView endLabel = (TextView) findViewById(R.id.end_seek_label);
        Pair<Integer, Integer> times = UserState.getSongTimes(playlistId, songId);
        startBar.setProgress(times.first);
        endBar.setProgress(times.second);
        startLabel.setText(String.valueOf(times.first));
        endLabel.setText(String.valueOf(times.second));

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
                UserState.editSong(playlistId, songId, String.valueOf(startBar.getProgress()), String.valueOf(endBar.getProgress()));
            }
        };

        startBar.setOnSeekBarChangeListener(listener);
        endBar.setOnSeekBarChangeListener(listener);
    }

}
