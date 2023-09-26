package com.example.musicplayer.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.example.musicplayer.R;
import com.example.musicplayer.Read;
import com.example.musicplayer.fragments.FirstPage;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private Read read;
    private final int requestCode=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        read=Read.getRead();
        read.setActivity(this);

        CardView playedMusicLayout= findViewById(R.id.current_music_played_layout);
        playedMusicLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                read.playedMusicListener();
            }
        });

        TextView title=findViewById(R.id.current_music_title);
        title.setSelected(true);
        read.setPlayedValues(findViewById(R.id.current_music_image),title,findViewById(R.id.current_music_artist),
                findViewById(R.id.current_music_play_button));
        read.setPlayedMusicValue();

        setFragment();
    }

    private void setFragment(){
        FragmentManager manager = this.getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.fragment_container, new FirstPage(), "list");
        transaction.commit();
    }

//    private void onRequestPermission(){
//        if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
//            if(!ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){
//                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},requestCode);
//            }
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (grantResults[0] == requestCode && permissions[0].equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
//            readMusics();
//        }
//    }

    @Override
    public void onBackPressed() {
        FragmentManager manager=getSupportFragmentManager();
        Fragment temp=manager.findFragmentByTag("play list musics");
        if(temp!=null){
            FragmentTransaction transaction=manager.beginTransaction();
            transaction.remove(temp);
            transaction.show(Objects.requireNonNull(manager.findFragmentByTag("list"))).commit();
        }
        else {
            super.onBackPressed();
        }
    }

    private void setAnimationView(Fragment temp){
        Animation animation= AnimationUtils.loadAnimation(this,R.anim.move_down);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                read.setItem();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.show(Objects.requireNonNull(getSupportFragmentManager().findFragmentByTag("list"))).
                remove(temp).commit();
                read.setStopCount(true);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        temp.getView().startAnimation(animation);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        read.setStopCount(true);
    }
}