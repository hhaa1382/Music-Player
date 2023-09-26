package com.example.musicplayer.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.musicplayer.adaptors.ListAdaptor;
import com.example.musicplayer.enums.MusicPage;
import com.example.musicplayer.R;
import com.example.musicplayer.Read;
import com.example.musicplayer.adaptors.ViewPagerAdaptor;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Objects;

public class FirstPage extends Fragment {
    private final String[] values={"Musics","Favorites","Playlist"};
    private Read controller;
    private ViewPagerAdaptor adaptor;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.first_page,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ViewPager2 viewPager=view.findViewById(R.id.view_page_2);
        TabLayout tabLayout=view.findViewById(R.id.tab_layout);
        controller=Read.getRead();

        adaptor=new ViewPagerAdaptor((FragmentActivity) view.getContext());
        viewPager.setAdapter(adaptor);
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                MusicList page=(MusicList) adaptor.getFragment(position);
                if(page.getRecyclerView()!=null){
                    checkListPage(position,page);
                }
            }
        });

        new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(values[position]);
            }
        }).attach();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                MusicList page=(MusicList) adaptor.getFragment(tab.getPosition());
                if(page==null){
                    adaptor.createFragment(tab.getPosition());
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void checkListPage(int position,MusicList page){
        if (position == 0) {
            if (controller.getChangeCheck(position)) {
                controller.setListView(page.getRecyclerView(), MusicPage.all);
                controller.setChangeCheck(position, false);
            } else {
                controller.changeList(false, page.getRecyclerView());
                changeListIndex((ListAdaptor) Objects.requireNonNull(page.getRecyclerView().getAdapter()));
            }
        } else if (position == 1) {
            if (controller.getChangeCheck(position)) {
                controller.setListView(page.getRecyclerView(), MusicPage.favorite);
                controller.setChangeCheck(position, false);
            } else {
                controller.changeList(true, page.getRecyclerView());
                changeListIndex((ListAdaptor) Objects.requireNonNull(page.getRecyclerView().getAdapter()));
            }
        }
    }

    private void changeListIndex(ListAdaptor adaptor){
        int selectedIndex=adaptor.getSelectedIndex();
        if(controller.getIndex()!=selectedIndex) {
            adaptor.setSelected(controller.getIndex());
            adaptor.notifyItemChanged(selectedIndex);
            adaptor.notifyItemChanged(controller.getIndex());
        }
    }
}
