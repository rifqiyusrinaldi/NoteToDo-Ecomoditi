package com.rifqi.myapplication;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface TaskDao {
    @Insert(onConflict = REPLACE)
    void insert(TaskData taskData);

    @Query("SELECT * FROM task")
    LiveData<List<TaskData>> getAllLiveData();

    @Query("SELECT * FROM task WHERE id = :id")
    TaskData selectTask(int id);

    @Query("SELECT * FROM task")
    List<TaskData> getAllData();

    @Query("SELECT COUNT(status) as Jumlah FROM task WHERE status == 0")
    LiveData<Integer> getCountData();

    @Query("SELECT COUNT(status) as Jumlah FROM task WHERE status == 1")
    LiveData<Integer> getCountDoneData();

    @Query("UPDATE task SET status = :st WHERE id = :id")
    void updateTask(int st, int id);

    @Query("DELETE FROM task WHERE status == 1")
    void deleteAllDone();

    @Query("UPDATE task SET status = 1 WHERE status = 0")
    void updateSelectAll();

}
