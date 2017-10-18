package com.nicholas.models;

/**
 * Created by Nicholas on 9/8/2017.
 */

public class SongWithTimesModel {
    public SongModel songModel;
    public double startTime;
    public double endTime;

    @Override
    public String toString() {
        String nameString = "No Name";
        if (songModel.name != null) {
            nameString = songModel.name;
        }
        return nameString;
    }
}
