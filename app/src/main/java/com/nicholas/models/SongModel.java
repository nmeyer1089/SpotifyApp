package com.nicholas.models;

/**
 * Created by Nicholas on 9/8/2017.
 */

public class SongModel {
    public String name;
    public String id;
    public int durationMs;
    public String artist;
    public String album;

    public double startTime;
    public double endTime;

    @Override
    public String toString() {
        String nameString = "No Name";
        if (this.name != null) {
            nameString = this.name;
        }
        return nameString;
    }
}
