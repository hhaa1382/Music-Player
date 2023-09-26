package com.example.musicplayer.room;

import android.util.Log;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collections;

public class FavoriteConverter {

    @TypeConverter
    public String listToString(ArrayList<String> values){
//        StringBuilder temp=new StringBuilder();
//        for(int i=0;i<values.size();i++) {
//            temp.append(values.get(i));
//            temp.append(";");
//        }
//
//        return temp.toString();

        Gson gson=new Gson();
        return gson.toJson(values);
    }

    @TypeConverter
    public ArrayList<String> stringToList(String favorites){
//        String[] splits=favorites.split(";");
//        ArrayList<String> values=new ArrayList<>();
//        Collections.addAll(values, splits);
//        return values;

        Gson gson=new Gson();
        ArrayList<String> temp=gson.fromJson(favorites,new TypeToken<ArrayList<String>>(){});
        return temp;
    }
}
