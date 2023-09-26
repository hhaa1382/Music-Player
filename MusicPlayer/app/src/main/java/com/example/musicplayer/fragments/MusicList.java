package com.example.musicplayer.fragments;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.enums.MusicPage;
import com.example.musicplayer.Read;
import com.example.musicplayer.databinding.MusicListLayoutBinding;

public class MusicList extends Fragment {
    private final Read read;
    private RecyclerView dataList;
    private MusicPage page;

    public MusicList(MusicPage page){
        this.page=page;
        read=Read.getRead();
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MusicListLayoutBinding view = MusicListLayoutBinding.inflate(inflater);

        dataList= view.musicList;
        read.setListView(dataList,page);

        return view.getRoot().getRootView();
    }

    public RecyclerView getRecyclerView(){
        return dataList;
    }
}
