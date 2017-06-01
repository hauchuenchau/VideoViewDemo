package com.cnlive.videoviewdemo.adapter.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.cnlive.videoviewdemo.R;
import com.cnlive.videoviewdemo.bean.Video;
import com.example.videolibrary.controller.MediaPlayerController;
import com.example.videolibrary.player.VideoPlayer;


/**
 * @author HouZhongzhou
 * @date 2017/6/19:21
 * @email houzhongzhou@cnlive.com
 * @desc VideoViewHolder
 */

public class VideoViewHolder extends RecyclerView.ViewHolder {

    private MediaPlayerController mController;
    private VideoPlayer mVideoPlayer;

    public VideoViewHolder(View itemView) {
        super(itemView);
        mVideoPlayer = (VideoPlayer) itemView.findViewById(R.id.video_player);
    }

    public void setController(MediaPlayerController controller) {
        mController = controller;
    }

    public void bindData(Video video) {
        mController.setTitle(video.getTitle());
        mController.setImage(video.getImageUrl());
        mVideoPlayer.setController(mController);
        mVideoPlayer.setDataSource(video.getVideoUrl(), null);
    }
}
