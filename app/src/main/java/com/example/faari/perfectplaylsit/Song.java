package com.example.faari.perfectplaylsit;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class Song {
    @PrimaryKey
    @NonNull public String uri;

    @ColumnInfo(name = "title")
    public String title;
    @ColumnInfo(name = "artist")
    public String artist;
    @ColumnInfo(name = "album")
    public String album;
    @ColumnInfo(name = "length")
    public int length;
    @ColumnInfo(name = "imgUrl")
    String imgUrl;
    @ColumnInfo(name = "spotifyId")
    String spotifyId;

    public Song(int length){
        this.length = length;
    }

    public void setSongInfo(String songInfo[]){
        this.title = songInfo[0];
        this.uri = songInfo[1];
        this.spotifyId = songInfo[2];
        this.imgUrl = songInfo[3];
        this.album = songInfo[4];
        this.artist = songInfo[5];
    }

    String getImgUrl(){
        return imgUrl;
    }

    void setImgUrl(String imgUrl){
        this.imgUrl = imgUrl;
    }

    String getKey(){
        return uri;
    }

    void setKey(String uri){
        this.uri = uri;
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

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public void setSpotifyId(String id){
        spotifyId = id;
    }


}
