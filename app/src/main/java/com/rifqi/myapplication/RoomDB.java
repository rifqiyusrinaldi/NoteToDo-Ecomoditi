package com.rifqi.myapplication;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;


@Database(entities = {TaskData.class},version = 1,exportSchema = false)

public abstract class RoomDB extends RoomDatabase {
    private static RoomDB database;

    private static String DATEBASE_NAME = "database";

    public synchronized static RoomDB getInstance(Context context){
        if (database==null){
            database = Room.databaseBuilder(context.getApplicationContext()
                    ,RoomDB.class,DATEBASE_NAME)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return database;
    }
    public abstract TaskDao taskDao();
}
