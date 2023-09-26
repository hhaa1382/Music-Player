package com.example.musicplayer.adaptors;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.R;
import com.example.musicplayer.Read;

import java.io.IOException;
import java.util.ArrayList;

public class ListAdaptor extends RecyclerView.Adapter<ListAdaptor.listHolder> {
    private final ArrayList<CardViewData> data;
    private int selectedIndex;

    public ListAdaptor(ArrayList<CardViewData> data,int index) {
        this.data = data;
        this.selectedIndex=index;
    }

    @NonNull
    @Override
    public listHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.music_list_values,parent,false);

        return new listHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull listHolder holder, @SuppressLint("RecyclerView") int position) {
        CardViewData dataValues=data.get(position);
        holder.title.setText(dataValues.getTitle());
        holder.artist.setText(dataValues.getArtist());
        setMusicImage(position, holder.image);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public void onClick(View view) {
                try {
                    Read.getRead().readMusic(position);
                    selectedIndex=position;
                    notifyDataSetChanged();
                }
                catch (IOException ignored){}
            }
        });

        if(selectedIndex==position){
            holder.title.setTextColor(holder.title.getContext().getResources().getColor(R.color.orange));
        }
        else{
            holder.title.setTextColor(Color.WHITE);
        }
    }

    public void setSelected(int selected){
        this.selectedIndex=selected;
    }

    public int getSelectedIndex(){
        return this.selectedIndex;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    private void setMusicImage(int position,ImageView image){
        byte[] imageByte=data.get(position).getMusicImage();
        if(imageByte!=null){
            Bitmap temp= BitmapFactory.decodeByteArray(imageByte,0,imageByte.length);
            image.setImageBitmap(temp);
        }
        else image.setImageResource(R.drawable.ic_launcher_background);
    }

    protected static class listHolder extends RecyclerView.ViewHolder{
        ImageView image;
        TextView title;
        TextView artist;

        public listHolder(@NonNull View itemView) {
            super(itemView);
            image=itemView.findViewById(R.id.music_list_image);
            title=itemView.findViewById(R.id.music_list_title);
            artist=itemView.findViewById(R.id.music_list_artist);
        }
    }
}
