package org.stemacademy.akmeier.sievemobileapplication.db;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;


import java.util.List;

/**
 * Defines various ways to interact with the database.
 */

@Dao
public interface TaskDao {
    @Query("SELECT * FROM Task")
    List<Task> getAll();

    @Insert
    void insertAll(Task task);

    @Delete
    void delete(Task task);

    @Update
    void update(Task task);

    @Query("SELECT COUNT(name) FROM Task")
    int getRowCount();

    //@Query("SELECT * FROM activities WHERE uid IN (:userIds)")
    //List<Task> loadAllByIds(int[] userIds);

    //@Query("SELECT * FROM activities WHERE first_name LIKE :first AND "
    //        + "last_name LIKE :last LIMIT 1")
    //Task findByName(String first, String last);
}