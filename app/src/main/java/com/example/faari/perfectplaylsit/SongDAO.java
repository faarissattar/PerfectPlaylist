package com.example.faari.perfectplaylsit;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface SongDAO {
    @Query("SELECT * FROM song")
    List<Song> getAll();

    @Query("SELECT * FROM song WHERE uri IN (:songIds)")
    List<Song> loadAllByIds(long[] songIds);

    @Query("SELECT * FROM song WHERE artist LIKE :artist")
    Song findAllByArtist(String artist);

    @Query("SELECT * FROM song WHERE title LIKE :title")
    Song findAllByTitle(String title);

    @Query("SELECT * FROM song WHERE album LIKE :album")
    Song findAllByAlbum(String album);

    @Query("DELETE FROM song")
    void deleteAll();

    @Insert
    void insertAll(Song... songs);

    @Delete
    void delete(Song song);
}
