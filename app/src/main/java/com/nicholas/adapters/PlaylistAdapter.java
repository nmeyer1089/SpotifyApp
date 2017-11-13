package com.nicholas.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.nicholas.States.PlayerState;
import com.nicholas.httpwrapper.ResponseTransferHelper;
import com.nicholas.models.SongModel;
import com.nicholas.spotifyapp.EditSongActivity;
import com.nicholas.spotifyapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Nicholas on 10/30/2017.
 */

public class PlaylistAdapter extends ArrayAdapter<SongModel> {

    private static Map<String, View> cvMap = new HashMap<>();
    private static String playingSongId = null;

    public PlaylistAdapter(Context context, ArrayList<SongModel> songs) {
        super(context, 0, songs);
        cvMap.clear();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final SongModel song = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.playlist_row_layout, parent, false);
        }
        final View cv = convertView;

        cv.setBackgroundColor(Color.WHITE);

        // Lookup view for data population
        Button playButton = (Button) convertView.findViewById(R.id.play_song);
        TextView nameText = (TextView) convertView.findViewById(R.id.song_name);
        // Populate the data into the template view using the data object
        playButton.setText("Play");
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cv.setBackgroundColor(Color.rgb(178,232,240));
                PlayerState.playSong(position);
            }
        });

        cvMap.put(song.id, cv);

        nameText.setText(song.name + ", " + song.artist);
        nameText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), EditSongActivity.class);
                ResponseTransferHelper.getInstance().addPair("trackId", song.id);
                ResponseTransferHelper.getInstance().setEditingSong(song, position);
                getContext().startActivity(intent);
            }
        });

        // if this is the playing song, highlight
        if(song.id.equals(playingSongId)) {
            cv.setBackgroundColor(Color.rgb(178,232,240));
            playingSongId = null;
        }

        // Return the completed view to render on screen
        return convertView;
    }

    public static void updateSelection(String lastId, String songId) {

        if(!lastId.equals("")) {
            cvMap.get(lastId).setBackgroundColor(Color.WHITE);
        }
        cvMap.get(songId).setBackgroundColor(Color.rgb(178,232,240));
    }

    public static void savePlayingSongId() {
        if(PlayerState.isPlaying) {
            playingSongId = PlayerState.playingSong.id;
        }
    }
}
