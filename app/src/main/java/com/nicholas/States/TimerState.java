package com.nicholas.States;

import android.os.CountDownTimer;
import android.util.Log;

import com.nicholas.spotifyapp.EditSongActivity;

import java.util.concurrent.Callable;

import static android.content.ContentValues.TAG;

/**
 * Created by Nicholas on 11/3/2017.
 */

public class TimerState {
    private static CountDownTimer timer;

    static {
        timer = new CountDownTimer(0, 0) {

            public void onTick(long millisUntilFinished) {            }
            public void onFinish() {       }
        };
    }

    public static void startTimer(int millis, final Callable<Void> endFun) {
        timer.cancel();
        timer = new CountDownTimer(millis, 1000) {

            public void onTick(long millisUntilFinished) {
                PlayerState.elapsedTimeSec+=1;
                EditSongActivity.setProgressBar((int) (PlayerState.getPositionMs() / (float) PlayerState.playingSong.durationMs * 100));
            }
            public void onFinish() {
                try {
                    PlayerState.elapsedTimeSec = 0;
                    endFun.call();
                } catch (Exception e) {
                    Log.e(TAG, "onFinish: Excpection in fn call");
                }
            }
        }.start();
    }

    public static void stopTimer() {
        timer.cancel();
    }

}
