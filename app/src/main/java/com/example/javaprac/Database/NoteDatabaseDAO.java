package com.example.javaprac.Database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface NoteDatabaseDAO {
    @Insert
    void insertAll(Note... note);

    @Query("SELECT * FROM stored_notes_table")
    List<Note> getAll();

    @Query("DELETE FROM stored_notes_table WHERE id=:id")
    void delete(Long id);

    @Query("SELECT title FROM stored_notes_table")
    List<String> getTitles();

    @Query("DELETE FROM stored_notes_table")
    void deleteAll();

    @Query("SELECT id FROM stored_notes_table")
    List<Long> getIds();

    @Query("SELECT * FROM stored_notes_table WHERE id=:id")
    Note getNote(Long id);

    @Query("UPDATE stored_notes_table SET body=:newBody WHERE id=:id")
    void update(Long id,String newBody);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long getNewId(Note note);

}
