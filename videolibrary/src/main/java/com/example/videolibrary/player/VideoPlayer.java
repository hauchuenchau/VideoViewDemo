package com.example.videolibrary.player;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.Surface;
import android.view.TextureView;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.example.videolibrary.controller.IMediaPlayerController;
import com.example.videolibrary.controller.MediaPlayerController;
import com.example.videolibrary.manager.VideoPlayerManager;
import com.example.videolibrary.util.LogUtil;
import com.example.videolibrary.util.VideoPlayerUtil;

import java.io.IOException;
import java.util.Map;

/**
 * @Author Mr.hou
 * @time 2017/5/31
 * @Email houzhongzhou@cnlive.com
 * @Desc videoPlayer
 */

public class VideoPlayer extends FrameLayout implements IMediaPlayerController, TextureView.SurfaceTextureListener {
    public static final int STATE_ERROR = -1;          // 播放错误
    public static final int STATE_IDLE = 0;            // 播放未开始
    public static final int STATE_PREPARING = 1;       // 播放准备中
    public static final int STATE_PREPARED = 2;        // 播放准备就绪
    public static final int STATE_PLAYING = 3;         // 正在播放
    public static final int STATE_PAUSED = 4;          // 暂停播放
    /**
     * 正在缓冲(播放器正在播放时，缓冲区数据不足，进行缓冲，缓冲区数据足够后恢复播放)
     **/
    public static final int STATE_BUFFERING_PLAYING = 5;
    /**
     * 正在缓冲(播放器正在播放时，缓冲区数据不足，进行缓冲，此时暂停播放器，继续缓冲，缓冲区数据足够后恢复暂停)
     **/
    public static final int STATE_BUFFERING_PAUSED = 6;
    public static final int STATE_COMPLETED = 7;       // 播放完成

    public static final int PLAYER_NORMAL = 10;        // 普通播放器
    public static final int PLAYER_FULL_SCREEN = 11;   // 全屏播放器
    public static final int PLAYER_TINY_WINDOW = 12;   // 小窗口播放器

    private int mCurrentState = STATE_IDLE;
    private int mPlayerState = PLAYER_NORMAL;

    private Context mContext;
    private FrameLayout mContainer;
    private TextureView mTextureView;
    private MediaPlayerController mController;
    private SurfaceTexture mSurfaceTexture;
    private String mUrl;
    private Map<String, String> mHeaders;
    private MediaPlayer mMediaPlayer;

    private int mBufferPercentage;

    public VideoPlayer(Context context) {
        this(context, null);
    }

    public VideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    private void init() {
        mContainer = new FrameLayout(mContext);
        mContainer.setBackgroundColor(Color.BLACK);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.addView(mContainer, params);
    }

    /**
     * 设置播放地址
     *
     * @param url
     * @param headers
     */
    public void setDataSource(String url, Map<String, String> headers) {
        mUrl = url;
        mHeaders = headers;
    }

    /**
     * 设置控制栏
     *
     * @param controller
     */
    public void setController(MediaPlayerController controller) {
        mController = controller;
        mController.setVideoPlayer(this);
        mContainer.removeView(mController);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mContainer.addView(mController, params);

    }

    /**
     * IMediaPlayerController实现方法
     */
    @Override
    public void start() {
        VideoPlayerManager.getInstance().releaseVideoPlayer();
        VideoPlayerManager.getInstance().setCurrentVideoPlayer(this);
        if (mCurrentState == STATE_IDLE || mCurrentState == STATE_ERROR || mCurrentState == STATE_COMPLETED) {
            initMediaPlayer();
            initTextureView();
            addTextureView();
        }
    }


    @Override
    public void restart() {
        if (mCurrentState == STATE_PAUSED) {
            mMediaPlayer.start();
            mCurrentState = STATE_PLAYING;
            mController.setControllerState(mPlayerState, mCurrentState);
        }

        if (mCurrentState == STATE_BUFFERING_PAUSED) {
            mMediaPlayer.start();
            mCurrentState = STATE_BUFFERING_PLAYING;
            mController.setControllerState(mPlayerState, mCurrentState);
        }
    }

    @Override
    public void pause() {
        if (mCurrentState == STATE_PLAYING) {
            mMediaPlayer.pause();
            mCurrentState = STATE_PAUSED;
            mController.setControllerState(mPlayerState, mCurrentState);
            LogUtil.d("STATE_PAUSED");
        }
        if (mCurrentState == STATE_BUFFERING_PLAYING) {
            mMediaPlayer.pause();
            mCurrentState = STATE_BUFFERING_PAUSED;
            mController.setControllerState(mPlayerState, mCurrentState);
            LogUtil.d("STATE_BUFFERING_PAUSED");
        }
    }

    @Override
    public void seekTo(int pos) {
        if (mMediaPlayer != null) {
            mMediaPlayer.seekTo(pos);
        }
    }

    @Override
    public boolean isIdle() {
        return mCurrentState == STATE_IDLE;
    }

    @Override
    public boolean isPreparing() {
        return mCurrentState == STATE_PREPARING;
    }

    @Override
    public boolean isPrepared() {
        return mCurrentState == STATE_PREPARED;

    }

    @Override
    public boolean isBufferingPlaying() {
        return mCurrentState == STATE_BUFFERING_PLAYING;

    }

    @Override
    public boolean isBufferingPaused() {
        return mCurrentState == STATE_BUFFERING_PAUSED;
    }

    @Override
    public boolean isPlaying() {
        return mCurrentState == STATE_PLAYING;
    }

    @Override
    public boolean isPaused() {
        return mCurrentState == STATE_PAUSED;
    }

    @Override
    public boolean isError() {
        return mCurrentState == STATE_ERROR;
    }

    @Override
    public boolean isCompleted() {
        return mCurrentState == STATE_COMPLETED;
    }

    @Override
    public boolean isFullScreen() {
        return mPlayerState == PLAYER_FULL_SCREEN;
    }

    @Override
    public boolean isSmallVideo() {
        return mPlayerState == PLAYER_TINY_WINDOW;
    }

    @Override
    public boolean isNormal() {
        return mPlayerState == PLAYER_NORMAL;
    }

    @Override
    public int getDuration() {
        return mMediaPlayer != null ? mMediaPlayer.getDuration() : 0;
    }

    @Override
    public int getCurrentPosition() {
        return mMediaPlayer != null ? mMediaPlayer.getCurrentPosition() : 0;
    }

    @Override
    public int getBufferPercentage() {
        return mBufferPercentage;
    }

    /**
     * 全屏，将mContainer(内部包含mTextureView和mController)从当前容器中移除，
     * 并添加到android.R.content中.
     */
    @Override
    public void enterFullScreen() {
        if (mPlayerState == PLAYER_FULL_SCREEN) return;

        // 隐藏ActionBar、状态栏，并横屏
        VideoPlayerUtil.hideActionBar(mContext);
        VideoPlayerUtil.scanForActivity(mContext)
                .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        this.removeView(mContainer);
        ViewGroup contentView = (ViewGroup) VideoPlayerUtil.scanForActivity(mContext)
                .findViewById(android.R.id.content);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        contentView.addView(mContainer, params);

        mPlayerState = PLAYER_FULL_SCREEN;
        mController.setControllerState(mPlayerState, mCurrentState);
    }

    /**
     * 退出全屏，移除mTextureView和mController，并添加到非全屏的容器中。
     *
     * @return true退出全屏.
     */
    @Override
    public boolean exitFullScreen() {
        if (mPlayerState == PLAYER_FULL_SCREEN) {
            VideoPlayerUtil.showActionBar(mContext);
            VideoPlayerUtil.scanForActivity(mContext)
                    .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            ViewGroup contentView = (ViewGroup) VideoPlayerUtil.scanForActivity(mContext)
                    .findViewById(android.R.id.content);
            contentView.removeView(mContainer);
            LayoutParams params = new LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            this.addView(mContainer, params);

            mPlayerState = PLAYER_NORMAL;
            mController.setControllerState(mPlayerState, mCurrentState);
            LogUtil.d("PLAYER_NORMAL");
            return true;
        }
        return false;
    }

    /**
     * 进入小窗口播放，小窗口播放的实现原理与全屏播放类似。
     */
    @Override
    public void enterSmallVideo() {
        if (mPlayerState == PLAYER_TINY_WINDOW) return;
        this.removeView(mContainer);

        ViewGroup contentView = (ViewGroup) VideoPlayerUtil.scanForActivity(mContext)
                .findViewById(android.R.id.content);
        // 小窗口的宽度为屏幕宽度的60%，长宽比默认为16:9，右边距、下边距为8dp。
        LayoutParams params = new LayoutParams((int)
                (VideoPlayerUtil.getScreenWidth(mContext) * 0.6f),
                (int) (VideoPlayerUtil.getScreenWidth(mContext) * 0.6f * 9f / 16f));
        params.gravity = Gravity.BOTTOM | Gravity.END;
        params.rightMargin = VideoPlayerUtil.dp2px(mContext, 8f);
        params.bottomMargin = VideoPlayerUtil.dp2px(mContext, 8f);

        contentView.addView(mContainer, params);

        mPlayerState = PLAYER_TINY_WINDOW;
        mController.setControllerState(mPlayerState, mCurrentState);
    }

    /**
     * 退出小窗口
     */
    @Override
    public boolean exitSmallVideo() {
        if (mPlayerState == PLAYER_TINY_WINDOW) {
            ViewGroup contentView = (ViewGroup) VideoPlayerUtil.scanForActivity(mContext)
                    .findViewById(android.R.id.content);
            contentView.removeView(mContainer);
            LayoutParams params = new LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            this.addView(mContainer, params);

            mPlayerState = PLAYER_NORMAL;
            mController.setControllerState(mPlayerState, mCurrentState);
            LogUtil.d("PLAYER_NORMAL");
            return true;
        }
        return false;
    }

    @Override
    public void release() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        mContainer.removeView(mTextureView);
        if (mSurfaceTexture != null) {
            mSurfaceTexture.release();
            mSurfaceTexture = null;
        }
        if (mController != null) {
            mController.reset();
        }
        mCurrentState = STATE_IDLE;
        mPlayerState = PLAYER_NORMAL;
    }


    /**
     * SurfaceTextureListener的操作
     *
     * @param surface
     * @param width
     * @param height
     */
    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        if (mSurfaceTexture == null) {
            mSurfaceTexture = surface;
            openMediaPlayer();
        } else {
            mTextureView.setSurfaceTexture(mSurfaceTexture);
        }
    }

    private void openMediaPlayer() {
        try {
            mMediaPlayer.setDataSource(mContext.getApplicationContext(), Uri.parse(mUrl));
            mMediaPlayer.setSurface(new Surface(mSurfaceTexture));
            mMediaPlayer.prepareAsync();
            mCurrentState = STATE_PREPARING;
            mController.setControllerState(mPlayerState, mCurrentState);
        } catch (IOException e) {
            e.printStackTrace();
            LogUtil.e("打开播放器发生错误", e);

        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return mSurfaceTexture == null;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    private void addTextureView() {
        mContainer.removeView(mTextureView);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mContainer.addView(mTextureView, 0, params);
    }

    private void initTextureView() {
        if (mTextureView == null) {
            mTextureView = new TextureView(mContext);
            mTextureView.setSurfaceTextureListener(this);
        }
    }

    private void initMediaPlayer() {
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();

            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setScreenOnWhilePlaying(true);

            mMediaPlayer.setOnPreparedListener(mOnPreparedListener);
            mMediaPlayer.setOnVideoSizeChangedListener(mOnVideoSizeChangedListener);
            mMediaPlayer.setOnCompletionListener(mOnCompletionListener);
            mMediaPlayer.setOnErrorListener(mOnErrorListener);
            mMediaPlayer.setOnInfoListener(mOnInfoListener);
            mMediaPlayer.setOnBufferingUpdateListener(mOnBufferingUpdateListener);
        }
    }

    private MediaPlayer.OnPreparedListener mOnPreparedListener
            = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            mp.start();
            mCurrentState = STATE_PREPARED;
            mController.setControllerState(mPlayerState, mCurrentState);
            LogUtil.d("onPrepared ——> STATE_PREPARED");
        }
    };

    private MediaPlayer.OnVideoSizeChangedListener mOnVideoSizeChangedListener
            = new MediaPlayer.OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
            LogUtil.d("onVideoSizeChanged ——> width：" + width + "，height：" + height);
        }
    };

    private MediaPlayer.OnCompletionListener mOnCompletionListener
            = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            mCurrentState = STATE_COMPLETED;
            mController.setControllerState(mPlayerState, mCurrentState);
            LogUtil.d("onCompletion ——> STATE_COMPLETED");
            VideoPlayerManager.getInstance().setCurrentVideoPlayer(null);
        }
    };

    private MediaPlayer.OnErrorListener mOnErrorListener
            = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            mCurrentState = STATE_ERROR;
            mController.setControllerState(mPlayerState, mCurrentState);
            LogUtil.d("onError ——> STATE_ERROR ———— what：" + what);
            return false;
        }
    };

    private MediaPlayer.OnInfoListener mOnInfoListener
            = new MediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                // 播放器渲染第一帧
                mCurrentState = STATE_PLAYING;
                mController.setControllerState(mPlayerState, mCurrentState);
                LogUtil.d("onInfo ——> MEDIA_INFO_VIDEO_RENDERING_START：STATE_PLAYING");
            } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
                // MediaPlayer暂时不播放，以缓冲更多的数据
                if (mCurrentState == STATE_PAUSED || mCurrentState == STATE_BUFFERING_PAUSED) {
                    mCurrentState = STATE_BUFFERING_PAUSED;
                    LogUtil.d("onInfo ——> MEDIA_INFO_BUFFERING_START：STATE_BUFFERING_PAUSED");
                } else {
                    mCurrentState = STATE_BUFFERING_PLAYING;
                    LogUtil.d("onInfo ——> MEDIA_INFO_BUFFERING_START：STATE_BUFFERING_PLAYING");
                }
                mController.setControllerState(mPlayerState, mCurrentState);
            } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
                // 填充缓冲区后，MediaPlayer恢复播放/暂停
                if (mCurrentState == STATE_BUFFERING_PLAYING) {
                    mCurrentState = STATE_PLAYING;
                    mController.setControllerState(mPlayerState, mCurrentState);
                    LogUtil.d("onInfo ——> MEDIA_INFO_BUFFERING_END： STATE_PLAYING");
                }
                if (mCurrentState == STATE_BUFFERING_PAUSED) {
                    mCurrentState = STATE_PAUSED;
                    mController.setControllerState(mPlayerState, mCurrentState);
                    LogUtil.d("onInfo ——> MEDIA_INFO_BUFFERING_END： STATE_PAUSED");
                }
            } else {
                LogUtil.d("onInfo ——> what：" + what);
            }
            return true;
        }
    };

    private MediaPlayer.OnBufferingUpdateListener mOnBufferingUpdateListener
            = new MediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
            mBufferPercentage = percent;
        }
    };
}
