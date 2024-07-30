package com.wa.pranksound.Room;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {InsertPrankSound.class}, version = 3)
public abstract class AppDatabase extends RoomDatabase {
    public abstract QueryClass queryClass();
}