package com.example.musicplayer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.musicplayer.activities.Music;
import com.example.musicplayer.adaptors.CardViewData;
import com.example.musicplayer.adaptors.ListAdaptor;
import com.example.musicplayer.adaptors.PlayListAdaptor;
import com.example.musicplayer.adaptors.PlayListClickListener;
import com.example.musicplayer.enums.MusicPage;
import com.example.musicplayer.enums.MusicType;
import com.example.musicplayer.fragments.PlayListMusics;
import com.example.musicplayer.room.AppDB;
import com.example.musicplayer.room.FavoriteDAO;
import com.example.musicplayer.room.FavoriteList;
import com.example.musicplayer.room.PlayList;
import com.example.musicplayer.room.PlayListDAO;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Read {
    private Context context;
    private FavoriteList favoriteSongs;
    private ArrayList<String> readFiles;
    private ArrayList<String> currentList;
    private boolean readMusic=false;
    private RecyclerView list;
    private final MediaPlayer player=new MediaPlayer();
    private MediaMetadataRetriever retriever;

    private ImageView playedMusicImage;
    private TextView playedMusicTitle;
    private TextView playedMusicArtist;
    private ImageButton playedMusicButton;

    private boolean destroyClick =true;
    private boolean isPlayed=false;
    private int index=0;
    private String currentMusicRes;
    private MusicPage pages;
    private boolean[] changeCheck={false,false};
    private MusicType type=MusicType.Repeat;

    @SuppressLint("StaticFieldLeak")
    private static final Read read=new Read();

    private Read(){}

    public static Read getRead(){
        return read;
    }

    public void readFiles() throws FileNotFoundException {
        readFiles=new ArrayList<>();
        retriever=new MediaMetadataRetriever();
        readFavoriteList();

        String path= Environment.getExternalStorageDirectory().toString();

        File[] files = new File(path).listFiles();

        if(files !=null) {
            for (File file : files) {
                if(file.isDirectory()){
                    File[] musics=file.listFiles(new FileFilter() {
                        @Override
                        public boolean accept(File file) {
                            return file.getName().contains(".mp3");
                        }
                    });

                    if(musics!=null) {
                        for (File f : musics) {
                            readFiles.add(f.getAbsolutePath());
                        }
                    }
                }
                else if(file.getName().contains(".mp3")){
                    readFiles.add(file.getAbsolutePath());
                }
            }
        }

        currentList=readFiles;
        currentMusicRes=currentList.get(index);/*it must be start music played*/
    }

    public void setPlayedValues(ImageView image, TextView title, TextView artist, ImageButton currentMusicButton){
        this.playedMusicImage=image;
        this.playedMusicTitle=title;
        this.playedMusicArtist=artist;
        this.playedMusicButton=currentMusicButton;

        playedMusicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playButtonListener();
            }
        });
    }

    public void playButtonListener(){
        try {
            if (isPlayed) {
                pauseMusic();
            }
            else {
                if (readMusic) resumeMusic();
                else playMusic();
            }
        }
        catch (IOException ex){
            ex.printStackTrace();
        }
    }

    public void playedMusicListener(){
        changeFragment();
    }

    public void setPlayedMusicValue(){
        if(currentList.size()>0) {
            retriever.setDataSource(currentMusicRes);
            setMusicImage(playedMusicImage);
            playedMusicTitle.setText(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));
            playedMusicArtist.setText(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));
        }
    }

    public String getArtist(){
        String artist=retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        if(artist==null){
            return "Unknown";
        }
        return artist;
    }

    public String getTitle(){
        String title=retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        if(title==null){
            return "Unknown";
        }
        return title;
    }

    public MediaPlayer getPlayer(){
        return this.player;
    }

    public void setMusicImage(ImageView image){
        byte[] imageByte=retriever.getEmbeddedPicture();
        if(imageByte!=null){
            Bitmap temp= BitmapFactory.decodeByteArray(imageByte,0,imageByte.length);
            image.setImageBitmap(temp);
        }
        else image.setImageResource(R.drawable.ic_launcher_background);
    }

    public void setActivity(Context context){
        this.context=context;
    }

    public void setFavoriteMusic(){
        favoriteSongs.addFavoriteMusic(currentMusicRes);
        saveFavoriteMusic();
    }

    public void removeFavoriteMusic(){
        favoriteSongs.getPaths().remove(currentMusicRes);
        saveFavoriteMusic();
    }

    private void saveFavoriteMusic(){
        AppDB appDB= Room.databaseBuilder(context,AppDB.class,"favorites_db").allowMainThreadQueries()
                .fallbackToDestructiveMigration().build();
        FavoriteDAO favoriteDB=appDB.getFavoriteDao();
        favoriteDB.update(favoriteSongs);
    }

    private void readFavoriteList(){
        AppDB appDB= Room.databaseBuilder(context,AppDB.class,"favorites_db").allowMainThreadQueries()
                .fallbackToDestructiveMigration().build();
        FavoriteDAO favoriteDB=appDB.getFavoriteDao();

        if(favoriteDB.getFavoriteList().size()==1){
            favoriteSongs=favoriteDB.getFavoriteList().get(0);
        }
        else{
            favoriteSongs=new FavoriteList();
            favoriteDB.insert(favoriteSongs);
        }
    }

    public boolean isFavourite(){
        return favoriteSongs.getPaths().contains(currentMusicRes);
    }

    public void setListView(RecyclerView list,MusicPage page){
        this.list=list;
        this.pages=page;

        if(page==MusicPage.all){
            ListAdaptor adaptor= new ListAdaptor(getAllData(), index);
            currentList=readFiles;
            list.setAdapter(adaptor);
        }
        else if(page==MusicPage.favorite){
            ListAdaptor adaptor=new ListAdaptor(getFavoriteData(),index);
            currentList= favoriteSongs.getPaths();
            list.setAdapter(adaptor);
        }
        else{
            List<PlayList> playLists=readPlayList();
            PlayListAdaptor adaptor=new PlayListAdaptor(getPlayLists(playLists),playLists,getPlayListListener(playLists));
            list.setAdapter(adaptor);
        }

        list.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
    }

    public void checkFavoriteChange(){
        if(pages==MusicPage.favorite){
            ListAdaptor adaptor;
            adaptor=new ListAdaptor(getFavoriteData(),index);
            list.setAdapter(adaptor);
            list.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        }
    }

    public void addPlayList(String name){
        PlayList temp=new PlayList(name);
        savePlayList(temp,"insert");
        Log.i("Hamid", "playlist added");
    }

    public void addMusicToPlayList(PlayList playList,boolean add){
        if(add) playList.getMusics().add(currentMusicRes);
        else playList.getMusics().remove(currentMusicRes);

        savePlayList(playList,"update");
    }

    public boolean checkPlayListMusics(PlayList playList){
        ArrayList<String> musics=playList.getMusics();
        return musics.contains(currentMusicRes);
    }

    public void deletePlayList(PlayList playList){
        savePlayList(playList,"delete");
    }

    private void savePlayList(PlayList playList,String value){
        AppDB appDB= Room.databaseBuilder(context,AppDB.class,"playlist_db").allowMainThreadQueries()
                .fallbackToDestructiveMigration().build();
        PlayListDAO playListDAO=appDB.getPlayListDao();

        if(value.equals("insert")) playListDAO.insertPlayList(playList);
        else if(value.equals("update")) playListDAO.updatePlayList(playList);
        else if(value.equals("delete")) playListDAO.deletePlayList(playList);
    }

    public List<PlayList> readPlayList(){
        AppDB appDB= Room.databaseBuilder(context,AppDB.class,"playlist_db").allowMainThreadQueries()
                .fallbackToDestructiveMigration().build();
        PlayListDAO playListDAO=appDB.getPlayListDao();
        return playListDAO.getPlayLists();
    }

    public void checkAllChange(){
        if(pages==MusicPage.all){
            ListAdaptor adaptor;
            adaptor=new ListAdaptor(getAllData(),index);
            list.setAdapter(adaptor);
            list.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        }
    }

    public void setChangeCheck(int position,boolean change){
        changeCheck[position]=change;
    }

    public boolean getChangeCheck(int position){
        return this.changeCheck[position];
    }

    public void changeList(boolean favorite,RecyclerView list){
        this.list=list;
        boolean check=false;
        if(!favorite){
            for (int i = 0; i < readFiles.size(); i++) {
                if (readFiles.get(i).equals(currentMusicRes)) {
                    index = i;
                    check = true;
                    break;
                }
            }

            if (!check) {
                index = -1;
            }
            currentList=readFiles;
        }
        else{
            for (int i = 0; i < favoriteSongs.getPaths().size(); i++) {
                if (favoriteSongs.getPaths().get(i).equals(currentMusicRes)) {
                    index = i;
                    check = true;
                    break;
                }
            }

            if (!check) {
                index = -1;
            }

            currentList= favoriteSongs.getPaths();
        }
    }

    public int getIndex(){
        return index;
    }

    private ArrayList<CardViewData> getAllData(){
        ArrayList<CardViewData> data=new ArrayList<>();
        boolean existCheck=false;

        for(int i=0;i<readFiles.size();i++) {
            retriever.setDataSource(readFiles.get(i));
            CardViewData newData = new CardViewData();
            setMusicValues(newData,retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
                    ,retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));
            newData.setMusicImage(retriever.getEmbeddedPicture());
            data.add(newData);

            if(readFiles.get(i).equals(currentMusicRes) && !existCheck){
                index=i;
                existCheck=true;
            }
        }

        return data;
    }

    private ArrayList<CardViewData> getFavoriteData(){
        ArrayList<CardViewData> data=new ArrayList<>(favoriteSongs.getPaths().size());
        boolean existCheck=false;

        for (int i=0;i<favoriteSongs.getPaths().size();i++) {
            retriever.setDataSource(favoriteSongs.getPaths().get(i));
            CardViewData newData = new CardViewData();
            setMusicValues(newData,retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
                    ,retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));
            newData.setMusicImage(retriever.getEmbeddedPicture());
            data.add(newData);

            if(favoriteSongs.getPaths().get(i).equals(currentMusicRes) && !existCheck){
                index=i;
                existCheck=true;
            }
        }

        if(!existCheck) index=-1;

        return data;
    }

    private ArrayList<CardViewData> getPlayLists(List<PlayList> playLists){
        ArrayList<CardViewData> data=new ArrayList<>();

        for(PlayList p:playLists){
            CardViewData temp=new CardViewData();
            temp.setTitle(p.getName());
            temp.setArtist(p.getMusics().size()+" songs");
            data.add(temp);
        }

        return data;
    }

    public void setPlayListAdaptor(RecyclerView list,PlayList playList){
        this.list=list;
        ListAdaptor adaptor=new ListAdaptor(getPlayListMusics(playList),index);
        list.setAdapter(adaptor);
        list.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        currentList=playList.getMusics();
    }

    private ArrayList<CardViewData> getPlayListMusics(PlayList playList){
        ArrayList<CardViewData> data=new ArrayList<>(playList.getMusics().size());
        boolean existCheck=false;
        for(int i=0;i<playList.getMusics().size();i++){
            retriever.setDataSource(playList.getMusics().get(i));
            CardViewData d=new CardViewData();
            setMusicValues(d,retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
                    ,retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));
            d.setMusicImage(retriever.getEmbeddedPicture());
            data.add(d);

            if(favoriteSongs.getPaths().get(i).equals(currentMusicRes) && !existCheck){
                index=i;
                existCheck=true;
            }
        }

        return data;
    }

    private void setMusicValues(CardViewData d,String title,String artist){
        if(title==null) d.setTitle("Unknown");
        else d.setTitle(title);

        if(artist==null) d.setArtist("Unknown");
        else d.setArtist(artist);
    }

    private PlayListClickListener getPlayListListener(List<PlayList> playLists){
        return new PlayListClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public void onClick(int index) {
                AppCompatActivity activity=(AppCompatActivity) context;
                FragmentManager manager=activity.getSupportFragmentManager();
                FragmentTransaction transaction=manager.beginTransaction();
                transaction.hide(manager.findFragmentByTag("list"));
                transaction.add(R.id.fragment_container,
                        new PlayListMusics(playLists.get(index).getName(),playLists.get(index)),"play list musics").commit();
            }
        };
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public void readMusic(int index) throws IOException {
        if(this.index==index){
            if(isPlayed){
                changeFragment();
            }
            else if(!readMusic){
                playMusic();
            }
            else{
                resumeMusic();
            }
        }
        else{
            this.index = index;
            playMusic();
        }
    }

    public void playMusic() throws IOException {
        retriever.setDataSource(currentList.get(index));
        currentMusicRes=currentList.get(index);
        if (!readMusic) {
            player.setDataSource(currentList.get(index));

            player.prepare();
            player.start();

            readMusic = true;
            playedMusicButton.setImageResource(R.drawable.pause_image);
        }
        else {
            player.stop();
            player.reset();
            player.setDataSource(currentList.get(index));

            player.prepare();
            player.start();
        }
        isPlayed=true;
        setPlayedMusicValue();
        playedMusicButton.setImageResource(R.drawable.pause_image);
    }

    public boolean isPlayed(){
        return this.isPlayed;
    }

    public void pauseMusic() {
        player.pause();
        isPlayed=false;
        playedMusicButton.setImageResource(R.drawable.play_image);
    }

    public void resumeMusic(){
        player.start();
        isPlayed=true;
        playedMusicButton.setImageResource(R.drawable.pause_image);
    }

    public boolean toPreviousMusic(){
        if(index!=0){
            index--;
            retriever.setDataSource(currentList.get(index));
            return true;
        }
        return false;
    }

    public boolean toNextMusic(){
        if(index!=currentList.size()-1){
            index++;
            retriever.setDataSource(currentList.get(index));
            return true;
        }
        return false;
    }

    public boolean endMusic(){
        if(type==MusicType.Repeat){
            return true;
        }
        else if(type==MusicType.Continue) {
            boolean path=toNextMusic();
            if(!path){
                path=toFirstMusic();
            }
            return path;
        }
        else{
            index= new Random().nextInt(currentList.size());
            retriever.setDataSource(currentList.get(index));
            return true;
        }
    }

    public boolean toFirstMusic(){
        if(currentList.size()!=0){
            index=0;
            retriever.setDataSource(currentList.get(index));
            return true;
        }
        return false;
    }

    public boolean getStopCount(){
        return destroyClick;
    }

    public void setStopCount(boolean isStop){
        this.destroyClick =isStop;
    }

    public void setMusicTime(int time){
        player.seekTo(time*1000);
    }

    public int getTime(){
        return player.getCurrentPosition()/1000;
    }

    public int getDuration(){
        return Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION))/1000;
    }

    public MusicType getMusicType(){return type;}

    public void setMusicType(MusicType type){
        this.type=type;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setItem(){
        ListAdaptor adaptor=(ListAdaptor) list.getAdapter();
        assert adaptor != null;
        adaptor.setSelected(index);
        adaptor.notifyDataSetChanged();
    }

    private void changeFragment(/*String path*/){
        destroyClick =false;
//        AppCompatActivity activity=(AppCompatActivity) context;
//        FragmentManager manager=activity.getSupportFragmentManager();
//        FragmentTransaction transaction=manager.beginTransaction();
//        transaction.hide(Objects.requireNonNull(manager.findFragmentByTag("list")));
//        transaction.add(R.id.fragment_container,new MusicFragment(path),"music").commit();
        Intent temp=new Intent(context, Music.class);
        context.startActivity(temp);
    }

    private JSONObject getIndexJson() throws JSONException {
        JSONObject object=new JSONObject();
        object.put("index",index);
        return object;
    }

    public void writeMusicIndex() throws IOException, JSONException {
        String jsonIndex=getIndexJson().toString();

        File file=new File(context.getFilesDir(),"index.json");
        FileWriter tempWriter=new FileWriter(file);
        BufferedWriter writer=new BufferedWriter(tempWriter);
        writer.write(jsonIndex);

        writer.close();
        tempWriter.close();
    }

    public void readMuicIndex() throws IOException, JSONException {
        File file=new File(context.getFilesDir(),"index.json");
        if(file.exists()) {
            FileReader tempReader = new FileReader(file);
            BufferedReader reader = new BufferedReader(tempReader);
            StringBuilder value = new StringBuilder();
            String temp;

            while ((temp = reader.readLine()) != null) {
                value.append(temp);
            }

            reader.close();
            tempReader.close();

            JSONObject object = new JSONObject(value.toString());
            this.index=object.getInt("index");
        }
        else{
            this.index=0;
        }
    }
}
