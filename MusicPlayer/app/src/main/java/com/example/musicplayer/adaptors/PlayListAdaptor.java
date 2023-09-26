package com.example.musicplayer.adaptors;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.R;
import com.example.musicplayer.room.PlayList;

import java.util.ArrayList;
import java.util.List;

public class PlayListAdaptor extends RecyclerView.Adapter<PlayListAdaptor.PlayListHolder> {
    private ArrayList<CardViewData> data;
    private List<PlayList> playLists;
    private PlayListClickListener clickListener;

    public PlayListAdaptor(ArrayList<CardViewData> data,List<PlayList> playLists,PlayListClickListener listener) {
        this.data = data;
        this.playLists=playLists;
        this.clickListener=listener;
    }

    @NonNull
    @Override
    public PlayListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.music_list_values,parent,false);
        return new PlayListHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayListHolder holder, @SuppressLint("RecyclerView") int position) {
        CardViewData selected=data.get(position);
        holder.image.setImageResource(R.drawable.ic_library_music);
        holder.name.setText(selected.getTitle());
        holder.number.setText(selected.getArtist());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickListener.onClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    protected static class PlayListHolder extends RecyclerView.ViewHolder{
        ImageView image;
        TextView name;
        TextView number;

        public PlayListHolder(@NonNull View itemView) {
            super(itemView);
            image=itemView.findViewById(R.id.music_list_image);
            name=itemView.findViewById(R.id.music_list_title);
            number=itemView.findViewById(R.id.music_list_artist);
        }
    }
}
