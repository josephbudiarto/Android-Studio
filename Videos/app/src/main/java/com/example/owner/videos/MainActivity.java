package com.example.owner.videos;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.widget.VideoView;
import android.widget.MediaController;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final VideoView buckysVideoView = (VideoView) findViewById(R.id.buckysVideoView);
        buckysVideoView.setVideoPath("https://www.thenewboston.com/forum/project_files/006_testVideo.mp4");

        //Player controls(play, pause, stop, etc...)
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(buckysVideoView);
        buckysVideoView.setMediaController(mediaController);

        buckysVideoView.start();
    }

}