package com.example.faari.perfectplaylsit;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface AppDAO {
    @Query("SELECT * FROM song")
    List<Song> getAllSongs();

    @Query("SELECT * FROM song WHERE uri IN (:songIds)")
    List<Song> loadAllByIdsSong(long[] songIds);

    @Query("SELECT * FROM song WHERE artist LIKE :artist")
    Song findAllByArtist(String artist);

    @Query("SELECT * FROM song WHERE title LIKE :title")
    Song findAllByTitle(String title);

    @Query("SELECT * FROM song WHERE album LIKE :album")
    Song findAllByAlbum(String album);

    @Query("DELETE FROM song")
    void deleteAllSongs();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Song... songs);

    @Delete
    void delete(Song song);

    @Query("SELECT * FROM command")
    List<Command> getAllCommands();

    @Query("DELETE FROM command")
    void deleteAllCommands();

    @Query("SELECT * FROM command WHERE id IN (:commandIds)")
    List<Command> loadAllByIdsCommand(long[] commandIds);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Command... commands);

    @Delete
    void delete(Command command);
}
