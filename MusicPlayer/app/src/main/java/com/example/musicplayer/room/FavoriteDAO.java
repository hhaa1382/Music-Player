package com.example.musicplayer.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface FavoriteDAO {
    @Insert()
    void insert(FavoriteList favoriteList);

    @Update
    void update(FavoriteList favoriteList);

    @Query("select * from favorite")
    List<FavoriteList> getFavoriteList();
}
