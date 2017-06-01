package com.example.videolibrary.manager;


import com.example.videolibrary.player.VideoPlayer;

/**
 * @Author Mr.hou
 * @time 2017/5/31
 * @Email houzhongzhou@cnlive.com
 * @Desc 视频播放器管理器
 */

public class VideoPlayerManager {
    private VideoPlayer player;
    private static VideoPlayerManager instance;

    public VideoPlayerManager() {
    }

    public static synchronized VideoPlayerManager getInstance() {
        if (instance == null) {
            instance = new VideoPlayerManager();
        }
        return instance;
    }

    public void setCurrentVideoPlayer(VideoPlayer videoPlayer) {
        player = videoPlayer;
    }

    /**
     * 释放
     */
    public void releaseVideoPlayer() {
        if (player != null) {
            player.release();
            player = null;
        }
    }

    /**
     * 点击返回按钮时调用
     */
    public boolean onBackPress() {
        if (player != null) {
            if (player.isFullScreen()) {
                return player.exitFullScreen();
            } else if (player.isSmallVideo()) {
                return player.exitSmallVideo();
            }
        } else {
            player.release();
            return false;
        }
        return false;
    }
}
