package com.example.faari.perfectplaylsit;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.spotify.protocol.types.Artist;

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

    public Song(String uri){
        this.uri = uri;
        title = "Call Me Maybe";
        artist = "Carly Rae Jepson";
        album = "Some album";
        length = 62;
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
}
