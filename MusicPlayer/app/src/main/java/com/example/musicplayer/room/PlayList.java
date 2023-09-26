package com.example.musicplayer.room;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.ArrayList;

@Entity(tableName = "play_list")
public class PlayList {
    @PrimaryKey
    @NonNull
    private String name;

    private ArrayList<String> musics;

    public PlayList(String name) {
        this.name = name;
        this.musics=new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getMusics() {
        return musics;
    }

    public void setMusics(ArrayList<String> musics) {
        this.musics = musics;
    }
}
