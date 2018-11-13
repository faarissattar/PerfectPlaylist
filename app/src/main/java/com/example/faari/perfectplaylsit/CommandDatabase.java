package com.example.faari.perfectplaylsit;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {Command.class}, version = 1, exportSchema = false)
public abstract class CommandDatabase extends RoomDatabase {

    public abstract CommandDAO commandDAO();
    private static CommandDatabase appDatabase;
    public static CommandDatabase getInstance(Context context){

        if(appDatabase==null){

            appDatabase = Room.databaseBuilder(context.getApplicationContext(),
                    CommandDatabase.class, "database-name").build();
        }

        return appDatabase;

    }
}
