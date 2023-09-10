package com.example.muplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class playingSong extends AppCompatActivity {
    @Override
    protected void onDestroy() {
        super.onDestroy();
        isRunning=false;
        mediaPlayer.stop();
        mediaPlayer.release();
    }
    TextView textView;
    ImageView play,previous,next;
    ArrayList<File> songs;
    MediaPlayer mediaPlayer;
    String textcontent;
    int position;
    SeekBar seekBar;
    Thread updateseek;
    TextView runningtimetext;
    TextView completiontimetext;
    // Declare a boolean flag to control the thread
    private volatile boolean isRunning = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playing_song);
        textView=findViewById(R.id.textView809);
        runningtimetext=findViewById(R.id.runningtimetext);
        completiontimetext=findViewById(R.id.completiontimetext);
        play=findViewById(R.id.play);
        previous=findViewById(R.id.previous);
        next=findViewById(R.id.next);
        seekBar=findViewById(R.id.seekBar);

        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        songs=(ArrayList)bundle.getParcelableArrayList("songlist");
        textcontent=intent.getStringExtra("currentsong");
        textView.setText(textcontent);
        //for marquee text note that we have to change in the xml file of this textview with many changes;;
        textView.setSelected(true);
        position=intent.getIntExtra("position",0);
        Uri uri=Uri.parse(songs.get(position).toString());
        mediaPlayer=MediaPlayer.create(this,uri);
        play.setImageResource(R.drawable.pause);
        mediaPlayer.start();
        // Set completion time to the total duration of the media file
        final String totalDuration = formatTime(mediaPlayer.getDuration());
        //this completion time working//
        completiontimetext.setText(totalDuration);
        seekBar.setMax(mediaPlayer.getDuration());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });
        updateseek=new Thread(){
            @Override
            public void run() {
                int currentposition=0;
                try {
                    while (currentposition<mediaPlayer.getDuration()){
                        currentposition=mediaPlayer.getCurrentPosition();
                        seekBar.setProgress(currentposition);
                        final String currenttime=formatTime(currentposition);
                        // Update the running time text view on the UI thread
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                runningtimetext.setText(currenttime);
                            }
                        });
                        sleep(500);
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                // Update completion time after the media playback is complete
                if (isRunning) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            completiontimetext.setText(formatTime(mediaPlayer.getDuration()));
                        }
                    });
                }
            }
        };
        updateseek.start();
        //event listeners to play,pause,next and previous the songs;;
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer.isPlaying()){
                    play.setImageResource(R.drawable.play);
                    mediaPlayer.pause();
                }
                else{
                    play.setImageResource(R.drawable.pause);
                    mediaPlayer.start();
                }
            }
        });
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();
                if (position!=0){
                    position=position-1;
                }
                else{
                    position=songs.size()-1;
                }
                Uri uri=Uri.parse(songs.get(position).toString());
                mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
                //this is for changing the icon when we change the song
                play.setImageResource(R.drawable.pause);
                mediaPlayer.start();
                //this is for changing the song name in the player in the previous
                textcontent=songs.get(position).getName().toString();
                textView.setText(textcontent);
                seekBar.setMax(mediaPlayer.getDuration());
                // Update the seek bar to the starting position
                seekBar.setProgress(0);
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();
                if (position!= songs.size()-1){
                    position=position+1;
                }
                else{
                    position=0;
                }
                Uri uri=Uri.parse(songs.get(position).toString());
                mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
                //this is for changing the icon when we change the song
                play.setImageResource(R.drawable.pause);
                mediaPlayer.start();
                //this is for changing the song name in the player for the next
                textcontent=songs.get(position).getName().toString();
                textView.setText(textcontent);
                seekBar.setMax(mediaPlayer.getDuration());
                // Update the seek bar to the starting position
                seekBar.setProgress(0);
            }
        });
    }
    private String formatTime(int milliseconds){
        int seconds=milliseconds/1000;
        int minutes=seconds/60;
        seconds%=60;
        return String.format("%02d:%02d",minutes,seconds);
    }
}