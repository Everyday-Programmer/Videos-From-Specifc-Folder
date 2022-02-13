package com.android.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.File;

public class VideoPlayActivity extends AppCompatActivity {
    String path;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);

        VideoView videoView = findViewById(R.id.videoView);
        ImageButton share = findViewById(R.id.shareVideo);
        ImageButton delete = findViewById(R.id.deleteVideo);
        ImageButton pausePlay = findViewById(R.id.pausePlay);

        Intent intent = getIntent();
        if (intent != null) {
            path = intent.getStringExtra("video");
            videoView.setVideoPath(path);
            videoView.setOnPreparedListener(mp -> {
                mp.start();

                pausePlay.setOnClickListener(v -> {
                    if (videoView.isPlaying()) {
                        videoView.pause();
                        Glide.with(VideoPlayActivity.this).load(R.drawable.ic_baseline_play_circle_24).into(pausePlay);
                    } else {
                        videoView.resume();
                        Glide.with(VideoPlayActivity.this).load(R.drawable.ic_baseline_pause_circle_24).into(pausePlay);
                    }
                });
            });
        }

        share.setOnClickListener(v -> new ShareCompat.IntentBuilder(VideoPlayActivity.this).setType("video/*").setStream(Uri.parse(path)).setChooserTitle("Share Video").startChooser());

        delete.setOnClickListener(v -> {
            MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(VideoPlayActivity.this);
            alertDialogBuilder.setMessage("Are you sure you want to delete this video ?");
            alertDialogBuilder.setPositiveButton("Yes", (dialog, which) -> {
                String[] projection = new String[] {MediaStore.Video.Media._ID};
                String selection = MediaStore.Video.Media.DATA + " = ?";
                String[] selectionArgs = new String[] {new File(path).getAbsolutePath()};
                Uri queryUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                ContentResolver contentResolver = getContentResolver();
                Cursor cursor = contentResolver.query(queryUri, projection, selection, selectionArgs,null);
                if (cursor.moveToFirst()) {
                    long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID));
                    Uri deleteUri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id);
                    try {
                        contentResolver.delete(deleteUri, null, null);
                        boolean delete1 = new File(path).delete();
                        Log.e("TAG", delete1 + "");
                        Toast.makeText(VideoPlayActivity.this, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(VideoPlayActivity.this, "Error Deleting Video", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(VideoPlayActivity.this, "File Not Found", Toast.LENGTH_SHORT).show();
                }
                cursor.close();
            });
            alertDialogBuilder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
            alertDialogBuilder.show();
        });
    }
}