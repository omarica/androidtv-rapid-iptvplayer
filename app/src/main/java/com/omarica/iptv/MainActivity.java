package com.omarica.iptv;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {

    public static List<Stream> streams;
    public static int index = 0;
    AudioManager audioManager;
    private DatabaseReference mDatabase;
    private RecyclerView mPlaylistList;
    private PlaylistAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPlaylistList = findViewById(R.id.playlist_recycler);

        mDatabase = FirebaseDatabase.getInstance().getReference();


        streams = new ArrayList<Stream>();

        mAdapter = new PlaylistAdapter(MainActivity.this);
        mPlaylistList.setAdapter(mAdapter);


        mDatabase.child("streams").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                streams.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

//                    Log.d("TAG", postSnapshot.toString());

                    Stream stream = postSnapshot.getValue(Stream.class);
                    streams.add(stream);

                }

                if (streams != null) {
                    if (streams.size() > 0)
                        mAdapter.update(streams);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);

        mPlaylistList.setLayoutManager(new LinearLayoutManager(this));



        /*
        String  jsonString =loadJSONFromAsset("streams.json") ;
        Gson gson = new Gson();
        channels = gson.fromJson(jsonString,Channel[].class);*/


    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mAdapter != null) {
            mPlaylistList.smoothScrollToPosition(index);
            mAdapter.selectRow(index);
            mAdapter.notifyDataSetChanged();
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

                    if (index - 1 < 0) {
                        index = streams.size() - 1;

                    } else {
                        index--;

                    }

                    mPlaylistList.smoothScrollToPosition(index);
                    mAdapter.selectRow(index);
                    mAdapter.notifyDataSetChanged();

                    break;
                case 20: //Down

                    index = (index + 1) % streams.size();

                    mPlaylistList.smoothScrollToPosition(index);
                    mAdapter.selectRow(index);
                    mAdapter.notifyDataSetChanged();

                    break;
                case KeyEvent.KEYCODE_ENTER:
                case 23: //OK


                    Intent intent = new Intent(MainActivity.this, VideoActivity.class);
                    intent.putExtra("index", index);
                    startActivity(intent);


                    break;
            }

        }

        return true;
    }

    public String loadJSONFromAsset(String fileName) {
        String json = null;
        try {
            InputStream is = getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
