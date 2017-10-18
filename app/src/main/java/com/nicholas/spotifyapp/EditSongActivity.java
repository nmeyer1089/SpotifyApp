package com.nicholas.spotifyapp;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.nicholas.States.PlayerState;
import com.nicholas.httpwrapper.ResponseTransferHelper;
import com.nicholas.managers.FileManager;
import com.nicholas.models.SongModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.nicholas.httpwrapper.GetPlaylist.PLAYLIST_JSON_KEY;

public class EditSongActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_song);

        String songId = ResponseTransferHelper.getInstance().getValue("trackId");

        TextView songName = (TextView) findViewById(R.id.song_name);
        songName.setText(songId);

        SeekBar.OnSeekBarChangeListener listener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                TextView seekLabel;
                switch(seekBar.getId()) {
                    case R.id.start_seek:
                        seekLabel = (TextView) findViewById(R.id.start_seek_label);
                        seekLabel.setText(String.valueOf(progress));
                        break;
                    case R.id.end_seek:
                        seekLabel = (TextView) findViewById(R.id.end_seek_label);
                        seekLabel.setText(String.valueOf(progress));
                        break;
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // play song snippet here
            }
        };

        SeekBar startBar = (SeekBar) findViewById(R.id.start_seek);
        startBar.setOnSeekBarChangeListener(listener);
        SeekBar endBar = (SeekBar) findViewById(R.id.end_seek);
        endBar.setOnSeekBarChangeListener(listener);
    }

}
