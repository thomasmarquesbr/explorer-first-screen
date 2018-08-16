package com.lapic.thomas.explorador_primeira_tela;

import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.VideoView;

import java.net.URI;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MediaPlayer.OnPreparedListener {

    @BindView(R.id.scroll_view) protected ScrollView scrollView;
    @BindView(R.id.video_view) protected VideoView videoView;
    @BindView(R.id.rl_details) protected RelativeLayout rl_details;

    private final String TAG = this.getClass().getSimpleName();
    private MediaController mediaController;
    private int position = 0;
    private static float dpWidth;
    private static float dpHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        dpHeight = displayMetrics.heightPixels / displayMetrics.density;

        Log.e(TAG, "deviceWidth: " + dpWidth);
        Log.e(TAG, "deviceHeight: "+ dpHeight);
        setFullescreenVideo();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    private void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        // Store current position.
        savedInstanceState.putInt("CurrentPosition", videoView.getCurrentPosition());
        videoView.pause();
    }


    // After rotating the phone. This method is called.
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // Get saved position.
        position = savedInstanceState.getInt("CurrentPosition");
        videoView.seekTo(position);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        videoView.seekTo(position);
        if (position == 0) {
            videoView.start();
        }

        MediaPlayer mediaPlayer = new MediaPlayer();
        // When video Screen change size.
        mediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
            @Override
            public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {

                // Re-Set the videoView that acts as the anchor for the MediaController
                mediaController.setAnchorView(videoView);
            }
        });
    }

    // METHODS

    public void prepareVideo() {
        if (mediaController == null)
            mediaController = new MediaController(this);
        String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.video ;
        mediaController.setAnchorView(videoView);
//        mediaController.setMediaPlayer(videoView);
        videoView.setVideoURI(Uri.parse(videoPath));
//        videoView.setOnPreparedListener(this);
    }

    public void setFullescreenVideo() {
        scrollView.setVisibility(View.GONE);
        rl_details.setVisibility(View.GONE);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        videoView.setLayoutParams(layoutParams);
        videoView.requestLayout();
        prepareVideo();
        mediaController.requestFocus();
//        videoView.start();
    }

    public void setMinimizeVideo() {
        scrollView.setVisibility(View.VISIBLE);
        rl_details.setVisibility(View.VISIBLE);
//        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams();

    }


}
