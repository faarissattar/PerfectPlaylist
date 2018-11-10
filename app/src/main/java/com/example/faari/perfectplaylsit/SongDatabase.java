package com.example.faari.perfectplaylsit;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {Song.class}, version = 1, exportSchema = false)
public abstract class SongDatabase extends RoomDatabase {
    public abstract SongDAO songDAO();
}
