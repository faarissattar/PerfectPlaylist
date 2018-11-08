package com.example.faari.perfectplaylsit;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface CommandDAO {
    @Query("SELECT * FROM command")
    List<Command> getAll();

    @Query("SELECT * FROM command WHERE id IN (:commandIds)")
    List<Command> loadAllByIds(long[] commandIds);

    @Insert
    void insertAll(Command... commands);

    @Delete
    void delete(Command command);
}
