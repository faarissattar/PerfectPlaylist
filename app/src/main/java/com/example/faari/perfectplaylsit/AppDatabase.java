package com.example.faari.perfectplaylsit;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.DatabaseConfiguration;
import android.arch.persistence.room.InvalidationTracker;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {Song.class, Command.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract AppDAO appDAO();
    private static AppDatabase appDatabase;
    public static AppDatabase getInstance(Context context){

        if(appDatabase==null){

            appDatabase = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class, "app-database").build();
        }

        return appDatabase;

    }
}
