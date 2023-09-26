package com.example.musicplayer.adaptors;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.musicplayer.enums.MusicPage;
import com.example.musicplayer.fragments.MusicList;

public class ViewPagerAdaptor extends FragmentStateAdapter {
    private final int fragmentNum=3;
    private final Fragment[] fragments =new Fragment[fragmentNum];

    public ViewPagerAdaptor(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if(position==0){
            fragments[0]=new MusicList(MusicPage.all);
        }
        else if(position==1){
            fragments[1]=new MusicList(MusicPage.favorite);
        }
        else{
            fragments[2]=new MusicList(MusicPage.playlist);
        }

        return fragments[position];
    }

    @Override
    public int getItemCount() {
        return fragmentNum;
    }

    public Fragment getFragment(int position){
        return fragments[position];
    }
}
