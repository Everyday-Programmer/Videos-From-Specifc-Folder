package com.android.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {
    Context context;
    ArrayList<Video> arrayList;
    OnItemClickListener onItemClickListener;

    public VideoAdapter(Context context, ArrayList<Video> arrayList) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(context).load(arrayList.get(position).getPath()).into(holder.imageView);
        holder.title.setText(new File(arrayList.get(position).getPath()).getName());
        try {
            MediaPlayer mediaPlayer = MediaPlayer.create(context, Uri.parse(arrayList.get(position).getPath()));
            holder.duration.setText(getDuration(mediaPlayer.getDuration()));
        } catch (Exception e) {
            e.printStackTrace();
            holder.duration.setText("--:--");
        }
        holder.itemView.setOnClickListener(v -> onItemClickListener.onClick(v, arrayList.get(position).getPath()));
    }

    @SuppressLint("DefaultLocale")
    private String getDuration(long duration) {
        String videoDuration;

        int dur = (int) duration;
        int hrs = (dur / 3600000);
        int min = (dur / 60000) % 6000;
        int sec = (dur % 60000 / 1000);

        if (hrs > 0) {
            videoDuration = String.format("%02d hrs, %02d min, %02d sec", hrs, min, sec);
        } else if (min > 0) {
            videoDuration = String.format("%02d min, %02d sec", min, sec);
        } else {
            videoDuration = String.format("%02d sec", sec);
        } return videoDuration;
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView title, duration;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.list_item_image);
            title = itemView.findViewById(R.id.list_item_title);
            duration = itemView.findViewById(R.id.list_item_duration);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onClick(View view, String path);
    }
}