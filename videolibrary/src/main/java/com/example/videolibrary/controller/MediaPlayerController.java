package com.example.videolibrary.controller;

import android.content.Context;
import android.os.CountDownTimer;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.videolibrary.R;
import com.example.videolibrary.player.VideoPlayer;
import com.example.videolibrary.util.VideoPlayerUtil;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @Author Mr.hou
 * @time 2017/5/31
 * @Email houzhongzhou@cnlive.com
 * @Desc 播放器控制器
 */

public class MediaPlayerController extends FrameLayout implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    private Context mContext;
    private IMediaPlayerController mediaController;
    //视频image
    private ImageView mImage;
    //中间的播放按钮
    private ImageView mCenterStart;
    //播放器顶部控制力
    private LinearLayout mTop;
    //返回按钮
    private ImageView mBack;
    //视频标题
    private TextView mTitle;
    //播放器底部控制栏
    private LinearLayout mBottom;
    //重播按钮
    private ImageView mRestartPause;
    //当前时长
    private TextView mPosition;
    //总时长
    private TextView mDuration;
    //seekbar
    private SeekBar mSeek;
    //全屏按钮
    private ImageView mFullScreen;
    //loadding
    private LinearLayout mLoading;
    //loadText
    private TextView mLoadText;
    //播放器播放出错时UI
    private LinearLayout mError;
    //重试
    private TextView mRetry;
    //播放完成时UI
    private LinearLayout mCompleted;
    //重播
    private TextView mReplay;
    //分享
    private TextView mShare;

    private Timer mUpdateProgressTimer;
    private TimerTask mUpdateProgressTimerTask;
    private boolean topBottomVisible;
    private CountDownTimer mDismissTopBottomCountDownTimer;

    public MediaPlayerController(@NonNull Context context) {
        super(context);
        mContext = context;
        init();
    }

    private void init() {
        LayoutInflater.from(mContext).inflate(R.layout.video_palyer_controller, this, true);
        mCenterStart = (ImageView) findViewById(R.id.center_start);
        mImage = (ImageView) findViewById(R.id.image);
        //top
        mTop = (LinearLayout) findViewById(R.id.top);
        mBack = (ImageView) findViewById(R.id.back);
        mTitle = (TextView) findViewById(R.id.title);
        //bottom
        mBottom = (LinearLayout) findViewById(R.id.bottom);
        mRestartPause = (ImageView) findViewById(R.id.restart_or_pause);
        mPosition = (TextView) findViewById(R.id.position);
        mDuration = (TextView) findViewById(R.id.duration);
        mSeek = (SeekBar) findViewById(R.id.seek);
        mFullScreen = (ImageView) findViewById(R.id.full_screen);
        //loadding
        mLoading = (LinearLayout) findViewById(R.id.loading);
        mLoadText = (TextView) findViewById(R.id.load_text);
        //error
        mError = (LinearLayout) findViewById(R.id.error);
        mRetry = (TextView) findViewById(R.id.retry);
        //completed
        mCompleted = (LinearLayout) findViewById(R.id.completed);
        mReplay = (TextView) findViewById(R.id.replay);
        mShare = (TextView) findViewById(R.id.share);
        //button
        mCenterStart.setOnClickListener(this);
        mBack.setOnClickListener(this);
        mRestartPause.setOnClickListener(this);
        mFullScreen.setOnClickListener(this);
        mRetry.setOnClickListener(this);
        mReplay.setOnClickListener(this);
        mShare.setOnClickListener(this);
        mSeek.setOnSeekBarChangeListener(this);
        this.setOnClickListener(this);
    }

    /**
     * 设置播放器标题
     *
     * @param title
     */
    public void setTitle(String title) {
        mTitle.setText(title);
    }

    /**
     * 设置视频播放器图片
     *
     * @param imageUrl
     */
    public void setImage(String imageUrl) {
        Glide.with(mContext)
                .load(imageUrl)
                .placeholder(R.drawable.img_default)
                .crossFade()
                .into(mImage);
    }

    public void setImage(@DrawableRes int resId) {
        mImage.setImageResource(resId);
    }

    /**
     *
     */
    public void setVideoPlayer(IMediaPlayerController iMediaPlayer) {
        mediaController = iMediaPlayer;
        if (mediaController.isIdle()) {
            mBack.setVisibility(GONE);
            mTop.setVisibility(VISIBLE);
            mBottom.setVisibility(GONE);
        }

    }

    /**
     * 控制器恢复到初始状态
     */
    public void reset() {
        topBottomVisible=false;
        cancelUpdateProgressTimer();
        cancleDismissTopBottomTimer();
        mSeek.setProgress(0);
        mSeek.setSecondaryProgress(0);

        mCenterStart.setVisibility(VISIBLE);
        mImage.setVisibility(VISIBLE);

        mBottom.setVisibility(GONE);
        mFullScreen.setImageResource(R.drawable.ic_player_enlarge);

        mTop.setVisibility(VISIBLE);
        mBack.setVisibility(GONE);

        mLoading.setVisibility(GONE);
    }

    @Override
    public void onClick(View v) {
        if (v == mCenterStart) {
            start();
        } else if (v == mBack) {
            back();
        } else if (v == mRestartPause) {
            restartPause();
        } else if (v == mFullScreen) {
            fullScreen();
        } else if (v == mRetry) {
            retry();
        } else if (v == mReplay) {
            replay();
        } else if (v == mShare) {
            share();
        } else if (v == this) {
            deafult();
        }
    }

    private void deafult() {
        if (mediaController.isPlaying()
                || mediaController.isPaused()
                || mediaController.isBufferingPlaying()
                || mediaController.isBufferingPaused()) {
            setTopBottomVisible(!topBottomVisible);
        }
    }

    private void start() {
        if (mediaController.isIdle()) {
            mediaController.start();
        }
    }

    private void back() {
        if (mediaController.isFullScreen()) {
            mediaController.exitFullScreen();
        } else if (mediaController.isSmallVideo()) {
            mediaController.exitSmallVideo();
        }
    }

    private void restartPause() {
        if (mediaController.isPlaying() || mediaController.isBufferingPlaying()) {
            mediaController.pause();
        } else if (mediaController.isPaused() || mediaController.isBufferingPaused()) {
            mediaController.restart();
        }
    }

    private void retry() {
        mediaController.release();
        mediaController.start();
    }

    private void replay() {
        mRetry.performClick();
    }

    private void fullScreen() {
        if (mediaController.isNormal()) {
            mediaController.enterFullScreen();
        } else if (mediaController.isFullScreen()) {
            mediaController.exitFullScreen();
        }
    }

    private void share() {
        Toast.makeText(mContext, "此功能待实现。。。", Toast.LENGTH_SHORT).show();
    }

    private void setTopBottomVisible(boolean isShow) {
        mTop.setVisibility(isShow ? VISIBLE : GONE);
        mBottom.setVisibility(isShow ? VISIBLE : GONE);
        topBottomVisible = isShow;
        if (isShow) {
            if (!mediaController.isPaused() && !mediaController.isBufferingPaused()) {
                startDismissTopBottomTimer();
            }
        } else {
            cancleDismissTopBottomTimer();
        }
    }

    private void cancleDismissTopBottomTimer() {
        if (mDismissTopBottomCountDownTimer != null) {
            mDismissTopBottomCountDownTimer.cancel();
        }
    }

    private void startDismissTopBottomTimer() {
        cancleDismissTopBottomTimer();
        if (mDismissTopBottomCountDownTimer == null) {
            mDismissTopBottomCountDownTimer = new CountDownTimer(8000, 8000) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    setTopBottomVisible(false);
                }
            };
            mDismissTopBottomCountDownTimer.start();
        }
    }

    /**
     * @param playerState 播放器状态
     * @param playState   当前播放状态
     */
    public void setControllerState(int playerState, int playState) {
        switch (playerState) {
            case VideoPlayer.PLAYER_NORMAL:
                mBack.setVisibility(View.GONE);
                mFullScreen.setVisibility(View.VISIBLE);
                mFullScreen.setImageResource(R.drawable.ic_player_enlarge);
                break;
            case VideoPlayer.PLAYER_FULL_SCREEN:
                mBack.setVisibility(View.VISIBLE);
                mFullScreen.setVisibility(View.VISIBLE);
                mFullScreen.setImageResource(R.drawable.ic_player_shrink);
                break;
            case VideoPlayer.PLAYER_TINY_WINDOW:
                mFullScreen.setVisibility(View.GONE);
                break;
        }
        switch (playState) {
            case VideoPlayer.STATE_IDLE:
                break;
            case VideoPlayer.STATE_PREPARING:
                // 只显示准备中动画，其他不显示
                mImage.setVisibility(View.GONE);
                mLoading.setVisibility(View.VISIBLE);
                mLoadText.setText("正在准备...");
                mError.setVisibility(View.GONE);
                mCompleted.setVisibility(View.GONE);
                mTop.setVisibility(View.GONE);
                mCenterStart.setVisibility(View.GONE);
                break;
            case VideoPlayer.STATE_PREPARED:
                startUpdateProgressTimer();
                break;
            case VideoPlayer.STATE_PLAYING:
                mLoading.setVisibility(View.GONE);
                mRestartPause.setImageResource(R.drawable.ic_player_pause);
                startDismissTopBottomTimer();
                break;
            case VideoPlayer.STATE_PAUSED:
                mLoading.setVisibility(View.GONE);
                mRestartPause.setImageResource(R.drawable.ic_player_start);
                cancleDismissTopBottomTimer();
                break;
            case VideoPlayer.STATE_BUFFERING_PLAYING:
                mLoading.setVisibility(View.VISIBLE);
                mRestartPause.setImageResource(R.drawable.ic_player_pause);
                mLoadText.setText("正在缓冲...");
                startDismissTopBottomTimer();
                break;
            case VideoPlayer.STATE_BUFFERING_PAUSED:
                mLoading.setVisibility(View.VISIBLE);
                mRestartPause.setImageResource(R.drawable.ic_player_start);
                mLoadText.setText("正在缓冲...");
                cancleDismissTopBottomTimer();
            case VideoPlayer.STATE_COMPLETED:
                cancelUpdateProgressTimer();
                setTopBottomVisible(false);
                mImage.setVisibility(View.VISIBLE);
                mCompleted.setVisibility(View.VISIBLE);
                if (mediaController.isFullScreen()) {
                    mediaController.exitFullScreen();
                }
                if (mediaController.isSmallVideo()) {
                    mediaController.exitSmallVideo();
                }
                break;
            case VideoPlayer.STATE_ERROR:
                cancelUpdateProgressTimer();
                setTopBottomVisible(false);
                mTop.setVisibility(View.VISIBLE);
                mError.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void cancelUpdateProgressTimer() {
        if (mUpdateProgressTimer != null) {
            mUpdateProgressTimer.cancel();
            mUpdateProgressTimer = null;
        }
        if (mUpdateProgressTimerTask != null) {
            mUpdateProgressTimerTask.cancel();
            mUpdateProgressTimerTask = null;
        }
    }

    private void startUpdateProgressTimer() {
        cancelUpdateProgressTimer();
        if (mUpdateProgressTimer == null) {
            mUpdateProgressTimer = new Timer();
        }
        if (mUpdateProgressTimerTask == null) {
            mUpdateProgressTimerTask = new TimerTask() {
                @Override
                public void run() {
                    MediaPlayerController.this.post(new Runnable() {
                        @Override
                        public void run() {
                            updateProgress();
                        }
                    });
                }
            };
        }
        mUpdateProgressTimer.schedule(mUpdateProgressTimerTask, 0, 300);
    }

    private void updateProgress() {
        int position = mediaController.getCurrentPosition();
        int duration = mediaController.getDuration();
        int bufferPercentage = mediaController.getBufferPercentage();
        mSeek.setSecondaryProgress(bufferPercentage);
        int progress = (int) (100f * position / duration);
        mSeek.setProgress(progress);
        mPosition.setText(VideoPlayerUtil.formatTime(position));
        mDuration.setText(VideoPlayerUtil.formatTime(duration));
    }

    /**
     * seekbarChanagedListrener操作方法
     *
     * @param seekBar
     * @param progress
     * @param fromUser
     */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        cancleDismissTopBottomTimer();
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (mediaController.isBufferingPaused() || mediaController.isPaused()) {
            mediaController.restart();
        }
        int position = (int) (mediaController.getDuration() * seekBar.getProgress() / 100f);
        mediaController.seekTo(position);
        startDismissTopBottomTimer();
    }
}
