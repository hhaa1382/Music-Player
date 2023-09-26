package com.example.musicplayer.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.adaptors.CardViewData;
import com.example.musicplayer.adaptors.PlayListAdaptor;
import com.example.musicplayer.adaptors.PlayListClickListener;
import com.example.musicplayer.R;
import com.example.musicplayer.Read;
import com.example.musicplayer.room.PlayList;

import java.util.ArrayList;
import java.util.List;

public class PlayListPage extends Fragment implements PlayListClickListener {
    private Read controller;
    private List<PlayList> playLists;
    private RecyclerView recyclerView;
    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.playlist_page,container,false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        controller=Read.getRead();

        recyclerView=view.findViewById(R.id.play_list_values);
        setPlayListAdaptor();

        ImageButton button=view.findViewById(R.id.add_playlist_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder=new AlertDialog.Builder(view.getContext()).setMessage("Enter play list name :")
                        .setTitle("Play List").setPositiveButton("Add", null)
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });

                AlertDialog alert=builder.create();
                EditText alertView=getAlertView();
                alert.setView(alertView);
                alert.show();

                alert.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String name=alertView.getText().toString();
                        if(name.isEmpty()){
                            Toast.makeText(view.getContext(),"Enter a name !",Toast.LENGTH_SHORT).show();
                        }
                        else if(!checkName(name)){
                            Toast.makeText(view.getContext(),"Play list already exist ! ",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            controller.addPlayList(name);
                            alert.dismiss();
                            setPlayListAdaptor();
                        }
                    }
                });
            }
        });
    }

    private boolean checkName(String name){
        for(PlayList p:playLists){
            if(p.getName().equals(name)){
                return false;
            }
        }
        return true;
    }

    private EditText getAlertView(){
        EditText editText=new EditText(view.getContext());
        LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        editText.setLayoutParams(params);
        return editText;
    }

    private void setPlayListAdaptor(){
        PlayListAdaptor adaptor=new PlayListAdaptor(getPlayLists(),playLists,this);
        recyclerView.setAdapter(adaptor);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(),LinearLayoutManager.VERTICAL,false));
    }

    private ArrayList<CardViewData> getPlayLists(){
        ArrayList<CardViewData> data=new ArrayList<>();
        playLists=controller.readPlayList();

        for(PlayList p:playLists){
            CardViewData temp=new CardViewData();
            temp.setTitle(p.getName());
            temp.setArtist(p.getMusics().size()+" songs");
            data.add(temp);
        }

        return data;
    }

    @Override
    public void onClick(int index) {
        Read controller=Read.getRead();
        if(controller.checkPlayListMusics(playLists.get(index))){
            Toast.makeText(view.getContext(),"This play list already has this music",Toast.LENGTH_SHORT).show();
        }
        else {
            controller.addMusicToPlayList(playLists.get(index), true);
            Toast.makeText(view.getContext(), "Music added", Toast.LENGTH_SHORT).show();
        }

        AppCompatActivity activity=(AppCompatActivity) view.getContext();
        FragmentManager manager=activity.getSupportFragmentManager();
        Fragment fragment=manager.findFragmentByTag("playlist");

        FragmentTransaction transaction=manager.beginTransaction();
        transaction.remove(fragment);
        transaction.commit();
    }
}
