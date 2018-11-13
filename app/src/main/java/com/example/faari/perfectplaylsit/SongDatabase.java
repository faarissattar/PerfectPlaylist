package com.example.faari.perfectplaylsit;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {Song.class}, version = 1, exportSchema = false)
public abstract class SongDatabase extends RoomDatabase {
    public abstract SongDAO songDAO();
    private static SongDatabase appDatabase;
    public static SongDatabase getInstance(Context context){

        if(appDatabase==null){

            appDatabase = Room.databaseBuilder(context.getApplicationContext(),
                    SongDatabase.class, "database-name").build();
        }

        return appDatabase;

    }
}
