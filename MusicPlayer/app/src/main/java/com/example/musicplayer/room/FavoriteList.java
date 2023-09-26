package com.example.musicplayer.room;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.ArrayList;

@Entity(tableName = "favorite")
public class FavoriteList {
    @PrimaryKey
    private int codeNum=0;

    private ArrayList<String> paths;

    public FavoriteList() {
        this.paths=new ArrayList<>();
    }

    public void addFavoriteMusic(String path){
        paths.add(path);
    }

    public int getCodeNum() {
        return codeNum;
    }

    public void setCodeNum(int codeNum) {
        this.codeNum = codeNum;
    }

    public void setPaths(@NonNull ArrayList<String> paths) {
        this.paths = paths;
    }

    @NonNull
    public ArrayList<String> getPaths(){
        return paths;
    }
}
