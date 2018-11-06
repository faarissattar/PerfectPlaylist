package com.example.faari.perfectplaylsit;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Song {
    @PrimaryKey
    public long mkey;

    long getKey(){
        return mkey;
    }
    void setKey(long key){
        mkey = key;
    }
}
