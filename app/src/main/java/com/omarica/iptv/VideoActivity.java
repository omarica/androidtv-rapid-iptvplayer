package com.omarica.iptv;

import android.content.Context;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class VideoActivity extends AppCompatActivity {


    // VideoView videoView;
    PlayerView playerView;
    ProgressBar progressBar;
    LinearLayout linearLayout;
    TextView channelNumberTextView, channelNameTextView;
    AudioManager audioManager;
    int index;
    List<Stream> mStreams;
    SimpleExoPlayer player;
    DataSource.Factory dataSourceFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        // videoView = findViewById(R.id.videoView);
        playerView = findViewById(R.id.playerView);
        progressBar = findViewById(R.id.progressBar);
        linearLayout = findViewById(R.id.linearLayout);
        channelNumberTextView = findViewById(R.id.channelNumberTextView);
        channelNameTextView = findViewById(R.id.channelNameTextView);

        setUpVideo();


        audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);

        mStreams = MainActivity.streams;

        index = getIntent().getIntExtra("index", -1);

        play(index);


    }

    private void setUpVideo() {


        player = ExoPlayerFactory.newSimpleInstance(this);

        playerView.setPlayer(player);

        playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
        player.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);

        // Produces DataSource instances through which media data is loaded.
        dataSourceFactory = new DefaultDataSourceFactory(VideoActivity.this,
                Util.getUserAgent(VideoActivity.this, "iptv"));


        player.addListener(new Player.EventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

                if (playWhenReady && playbackState == Player.STATE_READY) {
                    // media actually playing

                    hideLoading();

                } else if (playWhenReady) {
                    // might be idle (plays after prepare()),
                    // buffering (plays when data available)
                    // or ended (plays when seek away from end)
                    if (playbackState == Player.STATE_ENDED) {
                        stop();
                        play(index);
                    }
                } else {
                    // player paused in any state
                }

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {

                stop();
                play(index);

            }
        });


    }

    void showLoading() {
        // progressBar.setVisibility(View.VISIBLE);

        channelNameTextView.setText(mStreams.get(index).getName());
        channelNumberTextView.setText((index + 1) + "");
        linearLayout.setVisibility(View.VISIBLE);


    }

    void hideLoading() {
        //   progressBar.setVisibility(View.INVISIBLE);
        linearLayout.setVisibility(View.INVISIBLE);
    }


    void play(final int index) {

        showLoading();


        Uri uri = Uri.parse(mStreams.get(index).getUrl());

        MediaSource videoSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(uri);

        // Prepare the player with the source.


        player.prepare(videoSource);
        // Auto Play video as soon as it buffers
        resumeLivePreview();

    }


    @Override
    public void onPause() {
        super.onPause();
        stop();
        //  pauseLivePreview();
    }

    @Override
    public void onResume() {
        super.onResume();
        //setUpVideo();
        play(index);
        //resumeLivePreview();
    }


    private void resumeLivePreview() {
        if (player != null) {
            player.setPlayWhenReady(true);
        }
    }

    private void pauseLivePreview() {
        if (player != null) {
            if (playerView != null && playerView.getPlayer() != null) {
                playerView.getPlayer().release();
            }


        }

    }

    void stop() {
        if (player != null) {
            player.setPlayWhenReady(false);
            player.stop();
            player.seekTo(0);
        }
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {


        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            Log.e("VideoActivity", "Key down, code " + event.getKeyCode());

            switch (event.getKeyCode()) {

                case KeyEvent.KEYCODE_VOLUME_UP:
                case 22: //RIGHT
                    audioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND);

                    break;
                case KeyEvent.KEYCODE_VOLUME_DOWN:


                case 21: //LEFT

                    audioManager.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND);
                    break;


                case 19: //UP

                    index = (index + 1) % mStreams.size();

                    stop();
                    play(index);

                    break;
                case 20: //Down

                    if (index - 1 < 0) {
                        index = mStreams.size() - 1;


                    } else {
                        index--;
                    }

                    stop();
                    play(index);

                    break;
                case 4: //Back
                case 23: // OK


                    MainActivity.index = index;
                    pauseLivePreview();

                    finish();


                    break;
            }

        }

        return true;
    }


}