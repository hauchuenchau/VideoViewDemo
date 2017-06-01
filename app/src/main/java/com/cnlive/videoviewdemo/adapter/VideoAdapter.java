package com.cnlive.videoviewdemo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cnlive.videoviewdemo.R;
import com.cnlive.videoviewdemo.adapter.holder.VideoViewHolder;
import com.cnlive.videoviewdemo.bean.Video;
import com.example.videolibrary.controller.MediaPlayerController;

import java.util.List;


public class VideoAdapter extends RecyclerView.Adapter<VideoViewHolder> {

    private Context mContext;
    private List<Video> mVideoList;

    public VideoAdapter(Context context, List<Video> videoList) {
        mContext = context;
        mVideoList = videoList;
    }

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_video, parent, false);
        VideoViewHolder holder = new VideoViewHolder(itemView);
        MediaPlayerController controller = new MediaPlayerController(mContext);
        holder.setController(controller);
        return holder;
    }

    @Override
    public void onBindViewHolder(VideoViewHolder holder, int position) {
        Video video = mVideoList.get(position);
        holder.bindData(video);
    }

    @Override
    public int getItemCount() {
        return mVideoList.size();
    }
}
