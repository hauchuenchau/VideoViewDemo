package com.example.videolibrary.controller;

/**
 * @Author Mr.hou
 * @time 2017/5/31
 * @Email houzhongzhou@cnlive.com
 * @Desc    播放器控制器方法
 */

public interface IMediaPlayerController {

    /**
     * 开始播放
     */
    void start();

    /**
     * 重新开始播放
     */
    void restart();

    /**
     * 暂停
     */
    void pause();

    /**
     * 到指定时间点位置
     *
     * @param pos
     */
    void seekTo(int pos);

    /**
     * 播放状态
     */
    boolean isIdle();

    /**
     * 播放准备中
     */
    boolean isPreparing();

    /**
     * 播放准备就绪
     */
    boolean isPrepared();

    /**
     * 正在缓冲（播放器正在播放时，缓冲区数据不足，进行缓冲，缓冲区数据足够恢复播放）
     */
    boolean isBufferingPlaying();

    /**
     * 正在缓冲（播放器正在播放时，缓冲区数据不足，进行缓冲，此时播放器暂停，继续缓冲，缓冲区数据足够恢复播放）
     */
    boolean isBufferingPaused();

    /**
     * 是否处于播放状态
     */
    boolean isPlaying();

    /**
     * 是否处于暂停状态
     */
    boolean isPaused();

    /**
     * 是否播放出错
     */
    boolean isError();

    /**
     * 是否播放完成
     */
    boolean isCompleted();

    /**
     * isFullScreen 全屏
     * isTinyWindow 小窗口
     * isNormal 普通
     */
    boolean isFullScreen();

    boolean isSmallVideo();

    boolean isNormal();

    /**
     * 总时长
     */
    int getDuration();

    /**
     * 当前时长
     */
    int getCurrentPosition();

    /**
     * 缓冲进度
     */
    int getBufferPercentage();

    /**
     * enterFullScreen  进入全屏
     * exitFullScreen   退出全屏
     */
    void enterFullScreen();

    boolean exitFullScreen();

    /**
     * enterSmallVideo  进入小屏
     * exitSmallVideo   退出小屏
     */
    void enterSmallVideo();

    boolean exitSmallVideo();

    /**
     * 释放
     */
    void release();
}
