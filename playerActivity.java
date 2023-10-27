package com.androidapp.musicplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.chibde.visualizer.BarVisualizer;

import java.io.File;
import java.util.ArrayList;

public class playerActivity extends AppCompatActivity {

    Button btnplay,btnnext,btnprev,btnfor,btnback;
    TextView txtname,txtstart,txtstop;
    SeekBar seekmusic;
    Visualizer visualizer;
    static MediaPlayer mediaPlayer;
    String sname;
    public static final String EXTRA_NAME="Song_name";
    ImageView imageView;
Thread updateseekbar;
    int position;
    ArrayList<File> mySongs;

//
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        if(item.getItemId()==android.R.id.home)
//        {
//            onBackPressed();
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
//
//    @Override
//    protected void onDestroy() {
//        if(visualizer!=null)
//        {
//            visualizer.release();
//        }
//        super.onDestroy();
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

//        getSupportActionBar().setTitle("Now Playing");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowCustomEnabled(true);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Now Playing");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowCustomEnabled(true);
        }

        btnprev = findViewById(R.id.btprev);
        btnback = findViewById(R.id.btnrewi);
        btnfor = findViewById(R.id.btnff);
        btnnext = findViewById(R.id.btnext);
        btnplay = findViewById(R.id.playbtn);
        txtname = findViewById(R.id.textsn);
        txtstart = findViewById(R.id.textstart);
        txtstop = findViewById(R.id.textstop);
        seekmusic = findViewById(R.id.seekbar);
//        visualizer=findViewById(R.id.visualizer);
        imageView = findViewById(R.id.imageview);
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        Intent i = getIntent();
        Bundle bundle = i.getExtras();
        mySongs = (ArrayList) bundle.getParcelableArrayList("songs");
        String songName = i.getStringExtra("songname");
        position = bundle.getInt("pos", 0);
        Uri uri = Uri.parse(mySongs.get(position).toString());
        sname = mySongs.get(position).getName();
        txtname.setText(sname);

        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        mediaPlayer.start();
        updateseekbar = new Thread() {
            @Override
            public void run() {
                int totalDuration = mediaPlayer.getDuration();
                int currentposition = 0;
                while (currentposition < totalDuration) {
                    try {
                        sleep(500);
                        currentposition = mediaPlayer.getCurrentPosition();
                        seekmusic.setProgress(currentposition);

                    } catch (InterruptedException | IllegalStateException e) {
                        e.printStackTrace();
                    }

                }
            }
        };
        seekmusic.setMax(mediaPlayer.getDuration());
        updateseekbar.start();
        seekmusic.getProgressDrawable().setColorFilter(getResources().getColor(com.google.android.material.R.color.design_default_color_primary), PorterDuff.Mode.MULTIPLY);
        seekmusic.getThumb().setColorFilter(getResources().getColor(com.google.android.material.R.color.design_default_color_primary), PorterDuff.Mode.MULTIPLY);
        seekmusic.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        String endtime = createtime(mediaPlayer.getDuration());
        txtstop.setText(endtime);
        final Handler handler = new Handler();
        final int delay = 1000;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String currentTime = createtime(mediaPlayer.getCurrentPosition());
                txtstart.setText(currentTime);
                handler.postDelayed(this, delay);
            }
        }, delay);

        btnplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    btnplay.setBackgroundResource(R.drawable.baseline_play_arrow_24);
                    mediaPlayer.pause();
                } else {
                    btnplay.setBackgroundResource(R.drawable.baseline_pause_24);
                    mediaPlayer.start();
                }
            }
        });
        btnnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position = (position + 1) % mySongs.size();
                Uri u = Uri.parse(mySongs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), u);
                sname = mySongs.get(position).getName();
                txtname.setText(sname);
                mediaPlayer.start();
                btnplay.setBackgroundResource(R.drawable.baseline_pause_24);
                startAnimation(imageView);

            }
        });

btnprev.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        mediaPlayer.stop();
        mediaPlayer.release();
        position=((position-1)<0)?(mySongs.size()-1):position-1;
        Uri u=Uri.parse(mySongs.get(position).toString());
        mediaPlayer=MediaPlayer.create(getApplicationContext(),u);
        sname=mySongs.get(position).getName();
        txtname.setText(sname);
        mediaPlayer.start();
        btnplay.setBackgroundResource(R.drawable.baseline_pause_24);
        startAnimation(imageView);
    }
});

//nextlistener

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                playNextSong();

            }
        });

//        int audiosessinid=mediaPlayer.getAudioSessionId();
//        if(audiosessinid!=-1)
//        {
//            visualizer.setAudio
//        }
        btnfor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying())
                {
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()+10000);
                    //        int audiosessinid=mediaPlayer.getAudioSessionId();
//        if(audiosessinid!=-1)
//        {
//            visualizer.setAudioSessionId(audiosessinid);
//        }
                }
            }
        });
        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying())
                {
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()-10000);
                    //        int audiosessinid=mediaPlayer.getAudioSessionId();
//        if(audiosessinid!=-1)
//        {
//            visualizer.setAudioSessionId(audiosessinid);
//        }

                }
            }
        });
    }
    public void startAnimation(View view)
    {
        ObjectAnimator animator=ObjectAnimator.ofFloat(imageView,"rotation",0f,360f);
        animator.setDuration(1000);
        AnimatorSet animatorSet=new AnimatorSet();
        animatorSet.playTogether(animator);
        animator.start();


    }
    public String createtime(int duration)
    {
        String time="";
        int min=duration/1000/60;
        int sec=duration/1000%60;
        time+=min+":";
        if(sec<10)
        {
            time+="0";
        }
        time+=sec;
        return time;
    }
    private void playNextSong() {
        mediaPlayer.stop();
        mediaPlayer.release();
        position = (position + 1) % mySongs.size();
        Uri u = Uri.parse(mySongs.get(position).toString());
        mediaPlayer = MediaPlayer.create(getApplicationContext(), u);
        sname = mySongs.get(position).getName();
        txtname.setText(sname);
        mediaPlayer.start();
        btnplay.setBackgroundResource(R.drawable.baseline_pause_24);
        startAnimation(imageView);
    }
}