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
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class VideoActivity extends AppCompatActivity {


    StyledPlayerView playerView;
    ProgressBar progressBar;
    LinearLayout linearLayout;
    TextView channelNumberTextView, channelNameTextView;
    AudioManager audioManager;
    int index;
    List<Stream> mStreams;
    ExoPlayer player;
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

        player = new ExoPlayer.Builder(getApplicationContext()).build();
        playerView.setPlayer(player);
        playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
        player.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);

        player.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                if (playbackState == Player.STATE_READY) {

                    hideLoading();

                } else if (playbackState == Player.STATE_ENDED) {

                    stop();
                    play(index);
                } else {
                }

            }


            @Override
            public void onPlayerError(PlaybackException error) {

                stop();
                play(index);
            }
        });


    }

    void showLoading() {

        channelNameTextView.setText(mStreams.get(index).getName());
        channelNumberTextView.setText((index + 1) + "");
        linearLayout.setVisibility(View.VISIBLE);

    }

    void hideLoading() {
        linearLayout.setVisibility(View.INVISIBLE);
    }


    void play(final int index) {

        showLoading();

        Uri uri = Uri.parse(mStreams.get(index).getUrl());
        MediaItem mediaItem = MediaItem.fromUri(uri);
        player.setMediaItem(mediaItem);

        player.prepare();
        resumeLivePreview();
    }


    @Override
    public void onPause() {
        super.onPause();
        stop();
    }

    @Override
    public void onResume() {
        super.onResume();
        play(index);
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
