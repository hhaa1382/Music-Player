package com.example.musicplayer.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.R;
import com.example.musicplayer.Read;
import com.example.musicplayer.room.PlayList;
import com.google.android.material.appbar.CollapsingToolbarLayout;

public class PlayListMusics extends Fragment {
    private String title;
    private PlayList playList;

    public PlayListMusics(String title,PlayList playList) {
        this.title=title;
        this.playList=playList;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.play_list_musics,container,false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Toolbar toolbar=view.findViewById(R.id.play_list_musics_toolbar);
        toolbar.setTitle(title);

        RecyclerView list=view.findViewById(R.id.music_list);
        Read.getRead().setPlayListAdaptor(list,playList);
    }
}
