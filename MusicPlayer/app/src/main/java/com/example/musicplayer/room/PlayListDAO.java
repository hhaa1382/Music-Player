package com.example.musicplayer.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface PlayListDAO {
    @Insert
    void insertPlayList(PlayList playList);

    @Update
    void updatePlayList(PlayList playList);

    @Delete
    void deletePlayList(PlayList playList);

    @Query("SELECT * FROM play_list")
    List<PlayList> getPlayLists();
}
