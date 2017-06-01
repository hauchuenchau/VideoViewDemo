package com.cnlive.videoviewdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.videolibrary.controller.MediaPlayerController;
import com.example.videolibrary.manager.VideoPlayerManager;
import com.example.videolibrary.player.VideoPlayer;


public class MainActivity extends AppCompatActivity {
    private VideoPlayer videoPlayer;
    private Button smllVideo;
    private Button listplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listplay = (Button) findViewById(R.id.list_play);
        smllVideo = (Button) findViewById(R.id.smllVideo);
        init();
    }

    private void init() {
        videoPlayer = (VideoPlayer) findViewById(R.id.video_player);
        videoPlayer.setDataSource("http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/05/2017-05-17_17-33-30.mp4", null);
        MediaPlayerController mp = new MediaPlayerController(this);
        mp.setTitle("办公室小野开番外了，居然在办公室开澡堂！老板还点赞？");
        mp.setImage("http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/05/2017-05-17_17-30-43.jpg");
        videoPlayer.setController(mp);
        smllVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterSmallWindow();
            }
        });
        listplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,RecyclerViewActivity.class));
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (VideoPlayerManager.getInstance().onBackPress()) return;
        super.onBackPressed();
    }

    public void enterSmallWindow() {
        if (videoPlayer.isPlaying()
                || videoPlayer.isBufferingPlaying()
                || videoPlayer.isPaused()
                || videoPlayer.isBufferingPaused()) {
            videoPlayer.enterSmallVideo();
        } else {
            Toast.makeText(this, "要播放后才能进入小窗口", Toast.LENGTH_SHORT).show();
        }
    }
}
