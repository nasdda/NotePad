package com.example.javaprac.Database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities =  {Note.class},version = 1,exportSchema = false)
public abstract class NoteDatabase extends RoomDatabase {
    private static NoteDatabase instance = null;
    public abstract NoteDatabaseDAO noteDatabaseDao();

    public static NoteDatabase getNoteDatabase(Context context){
        if(instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    NoteDatabase.class,
                    "note-database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
