package com.example.faari.perfectplaylsit;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {Command.class}, version = 1, exportSchema = false)
public abstract class CommandDatabase extends RoomDatabase {
    public abstract CommandDAO commandDAO();
}
