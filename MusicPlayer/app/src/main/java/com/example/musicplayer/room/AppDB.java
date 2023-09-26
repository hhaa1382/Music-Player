package com.example.musicplayer.room;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {FavoriteList.class,PlayList.class},version = 2,exportSchema = false)
@TypeConverters({FavoriteConverter.class})
public abstract class AppDB extends RoomDatabase {
    public abstract FavoriteDAO getFavoriteDao();
    public abstract PlayListDAO getPlayListDao();
}
