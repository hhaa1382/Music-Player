package com.example.musicplayer.adaptors;

public class CardViewData {
    private byte[] musicImage;
    private String title;
    private String artist;

    public byte[] getMusicImage() {
        return musicImage;
    }

    public void setMusicImage(byte[] musicImage) {
        this.musicImage = musicImage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }
}
