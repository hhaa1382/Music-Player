package com.example.musicplayer.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.musicplayer.R;
import com.example.musicplayer.Read;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.FileNotFoundException;

@SuppressLint("CustomSplashScreen")
public class SplashScreen extends AppCompatActivity {
    private final static Object checkPermission=new Object();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (checkPermission){
                    try {
                        checkPermission.wait();

                        TextView text=findViewById(R.id.splash_screen_text);
                        Animation textAnimation=new TranslateAnimation(0,0,0,-200);
                        textAnimation.setDuration(2000);
                        textAnimation.setFillAfter(true);
                        text.startAnimation(textAnimation);

                        ImageView welcome=findViewById(R.id.splash_screen_icon);
                        Animation welcomeAnimation=new AlphaAnimation(0,1);
                        welcomeAnimation.setDuration(2000);
                        welcomeAnimation.setFillAfter(true);
                        welcome.startAnimation(welcomeAnimation);

                        readMusics();

                        Thread.sleep(4000);

                        Intent intent=new Intent(SplashScreen.this, MainActivity.class);
                        startActivity(intent);

                        finish();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE )!= PackageManager.PERMISSION_GRANTED){
            appExternalStoragePermission();
        }
        else{
            synchronized (checkPermission){
                checkPermission.notify();
            }
        }

    }

    private void appExternalStoragePermission(){
        Dexter.withContext(this).
                withPermission(Manifest.permission.READ_EXTERNAL_STORAGE).
                withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        synchronized (checkPermission){
                            checkPermission.notify();
                        }
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {}
                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {}
                }).check();
    }

    private void readMusics(){
        try {
            Read read=Read.getRead();
            read.setActivity(this);
            read.readFiles();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
