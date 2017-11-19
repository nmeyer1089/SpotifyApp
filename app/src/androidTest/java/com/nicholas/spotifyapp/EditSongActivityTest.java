package com.nicholas.spotifyapp;

import android.app.Instrumentation;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.annotation.UiThreadTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

import com.nicholas.httpwrapper.ResponseTransferHelper;
import com.nicholas.models.SongModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class EditSongActivityTest {

    private EditSongActivity editSongActivity;
    private SeekBar startBar;
    private TextView startLabel;

    @Rule
    public ActivityTestRule<EditSongActivity> activityRule = new ActivityTestRule<EditSongActivity>(EditSongActivity.class);

    @Before
    public void setUp() throws Exception {

        // put values in ResponseTransferHelper
        ResponseTransferHelper.getInstance().addPair("playlistId", "testPlaylistId");
        SongModel testSong = new SongModel();
        testSong.name = "testName";
        testSong.album = "testAlbum";
        testSong.artist = "testArtist";
        testSong.durationMs = 10000;
        testSong.id = "testSongId";
        ResponseTransferHelper.getInstance().setEditingSong(testSong,0);

        editSongActivity = activityRule.getActivity();
        editSongActivity.isTest = true;
        startBar = (SeekBar) editSongActivity.findViewById(R.id.start_seek);
        startLabel = (TextView) editSongActivity.findViewById(R.id.start_seek_label);
     }

    @Test
    public void testPreconditions() {
       assertNotNull(editSongActivity);
       assertNotNull(ResponseTransferHelper.getInstance().getValue("playlistId"));
       assertNotNull(ResponseTransferHelper.getInstance().getEditingSong());
       assertNotNull(startBar);
       assertNotNull(startLabel);
    }

    @Test
    public void testTimeChange() throws InterruptedException {
        assertEquals(0,startBar.getProgress());
        startBar.setProgress(2000);
        Thread.sleep(100);
        assertEquals(2000,startBar.getProgress());
        assertEquals("0:02",startLabel.getText().toString());
    }

    @Test
    public void testConversion() {
        assertEquals("0:02",editSongActivity.miliToTimestamp(2000));
    }
}
