package com.example.musicplayer.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.musicplayer.enums.MusicType;
import com.example.musicplayer.R;
import com.example.musicplayer.Read;
import com.example.musicplayer.databinding.ActivityMusicBinding;
import com.example.musicplayer.fragments.PlayListPage;

import java.io.IOException;
import java.util.Objects;

public class Music extends AppCompatActivity {
    private ActivityMusicBinding bindingView;
    private Read musicController;
    private int counter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindingView=ActivityMusicBinding.inflate(getLayoutInflater());
        setContentView(bindingView.getRoot());

        musicController=Read.getRead();

        setValues();
        setTimer();
        setButtonValues();
        setCompleteListener();
        setToolbarValues();
        setFavoriteListener();
        setRepeatListener();
    }

    @Override
    public void onBackPressed() {
        Fragment fragment=getSupportFragmentManager().findFragmentByTag("playlist");
        if(fragment==null) {
            musicController.setItem();
            musicController.setStopCount(true);
            super.onBackPressed();
            finish();
        }
        else{
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }
    }

    private void setValues() {
        TextView title=bindingView.musicTitle;
        title.setText(musicController.getTitle());
        title.setSelected(true);

        TextView artist=bindingView.musicArtist;
        artist.setText(musicController.getArtist());

        ImageView image=bindingView.musicImage;
        musicController.setMusicImage(image);

        TextView musicTime=bindingView.musicTime;
        musicTime.setText(getMusicTime(musicController.getDuration()));

        TextView musicCurrentTime=bindingView.musicCurrentTime;
        musicCurrentTime.setText(getMusicTime(counter));

        setSeekBarListener();
        setFavoriteImage();
        setButtonImage();
        setRepeatImage();
    }

    private void setButtonValues() {
        ImageButton playButton=bindingView.playButton;
        ImageButton next=bindingView.nextButton;
        ImageButton previous=bindingView.previousButton;

        playButton.setOnClickListener(view1 -> {
            musicController.playButtonListener();
            this.setButtonImage();
        });
        previous.setOnClickListener(e-> skipButtonListener(false));
        next.setOnClickListener(e-> skipButtonListener(true));
    }

    private void setSeekBarListener(){
        SeekBar seekBar=bindingView.musicTimer;
        seekBar.setMax(musicController.getDuration());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(b){
                    musicController.setMusicTime(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void setCompleteListener() {
        musicController.getPlayer().setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                try {
//                    Activity activity = (Activity) bindingView.getRoot().getContext();
                    boolean next = musicController.endMusic();
//                    Runnable runnable= () -> setValues();

                    if (next) {
//                        assert activity != null;
//                        activity.runOnUiThread(runnable);
                        setValues();
                        counter = 0;
                        musicController.playMusic();
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void skipButtonListener(boolean next) {
        boolean check;
        try {
            if(!next) check=musicController.toPreviousMusic();
            else check=musicController.toNextMusic();

            if(check) {
                setValues();
                counter = 0;
                musicController.playMusic();
            }
        }
        catch (IOException ignore){}
    }

    private void setTimer() {
        TextView musicCurrentTime=bindingView.musicCurrentTime;
        SeekBar seekBar=bindingView.musicTimer;

        Thread timeCheck=new Thread(() -> {
            Activity activity = (Activity) bindingView.getRoot().getContext();
            Runnable runnable = () -> musicCurrentTime.setText(getMusicTime(counter));

            try {
                while (!musicController.getStopCount()) {
                    Thread.sleep(1000);
                    counter=musicController.getTime();
                    seekBar.setProgress(counter);
                    activity.runOnUiThread(runnable);
                }
            }
            catch (InterruptedException  e) {
                e.printStackTrace();
            }
        });
        timeCheck.start();
    }

    private String getMusicTime(int time) {
        int minute=time/60;
        int second;
        String temp;

        if(minute<60) {
            second = time % 60;
            temp =""+minute;

            if (second < 10) temp += ":0" + second;
            else temp += ":" + second;
        }
        else{
            int hour=time/3600;
            minute=(time%3600)/60;
            second=(time%3600)%60;

            temp=""+hour;

            if(minute<10) temp+=":0"+minute;
            else temp+=":"+minute;

            if(second<10) temp+=":0"+second;
            else temp+=":"+second;
        }

        return temp;
    }

    private void setToolbarValues(){
        Toolbar toolbar=bindingView.musicToolBar;
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Music.this.onBackPressed();
            }
        });
    }

    private void setRepeatListener() {
        ImageView repeatImage=bindingView.repeatImage;
        repeatImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MusicType type=musicController.getMusicType();
                if(type==MusicType.Repeat){
                    musicController.setMusicType(MusicType.Continue);
                }
                else if(type==MusicType.Continue){
                    musicController.setMusicType(MusicType.Shuffle);
                }
                else{
                    musicController.setMusicType(MusicType.Repeat);
                }
                setRepeatImage();
            }
        });
    }

    private void setFavoriteListener() {
        ImageView favoriteImage=bindingView.favoriteImage;
        favoriteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!musicController.isFavourite()){
                    musicController.setFavoriteMusic();
                }
                else{
                    musicController.removeFavoriteMusic();
                }
                musicController.checkFavoriteChange();
                musicController.setChangeCheck(1,true);
                setFavoriteImage();
            }
        });
    }

    private void setRepeatImage() {
        ImageView repeatImage=bindingView.repeatImage;
        MusicType type=musicController.getMusicType();
        if(type==MusicType.Repeat) repeatImage.setImageResource(R.drawable.ic_repeat_one);
        else if(type==MusicType.Continue) repeatImage.setImageResource(R.drawable.ic_repeat);
        else repeatImage.setImageResource(R.drawable.ic_shuffle);
    }

    private void setFavoriteImage() {
        ImageView favoriteImage=bindingView.favoriteImage;
        if(musicController.isFavourite()) favoriteImage.setImageResource(R.drawable.ic_favorite);
        else favoriteImage.setImageResource(R.drawable.ic_favorite_border);
    }

    private void setButtonImage() {
        ImageButton playButton=bindingView.playButton;
        if(musicController.isPlayed()) playButton.setImageResource(R.drawable.pause_image);
        else playButton.setImageResource(R.drawable.play_image);
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.muisc_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.play_list_item){
            playListPart();
        }
        else if(item.getItemId()==R.id.delete_item){

        }
        return true;
    }

    private void playListPart(){
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.music_fragment_layout,new PlayListPage(),"playlist");
        transaction.commit();
    }
}