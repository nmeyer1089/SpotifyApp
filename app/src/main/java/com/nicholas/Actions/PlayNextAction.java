package com.nicholas.Actions;

import com.nicholas.States.PlayerState;

import java.util.concurrent.Callable;

/**
 * Created by Nicholas on 11/3/2017.
 */

public class PlayNextAction implements Callable<Void> {

    public Void call() {
        PlayerState.playNextSong();
        return null;
    }
}
