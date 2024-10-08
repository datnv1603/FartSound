package com.joya.pranksound.room;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {InsertPrankSound.class}, version = 3)
public abstract class AppDatabase extends RoomDatabase {
    public abstract QueryClass queryClass();
}