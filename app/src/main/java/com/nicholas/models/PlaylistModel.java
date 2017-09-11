package com.nicholas.models;

/**
 * Created by Nicholas on 9/2/2017.
 */

public class PlaylistModel {
    //playlist id
    public String id;
    //playlist name
    public String name;
    //owner's id
    public String ownerId;

    @Override
    public String toString() {
        String nameString = "No Name";
        if (this.name != null) {
            nameString = this.name;
        }
        return nameString;
    }
}
