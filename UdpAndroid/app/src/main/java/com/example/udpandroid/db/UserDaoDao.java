package com.example.udpandroid.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface UserDaoDao {
    @Query("SELECT * FROM userdata")
    List<UserData> getAll();

    @Query("SELECT * FROM userdata WHERE uid IN (:userIds)")
    List<UserData> loadAllByIds(int[] userIds);

    @Query("SELECT * FROM userdata WHERE uid LIKE :uid LIMIT 1")
    UserData findById(String uid);

    @Query("SELECT * FROM userdata WHERE email LIKE :email LIMIT 1")
    UserData findByEmail(String email);

    @Insert
    void insertAll(UserData... propertyDatas);

    @Delete
    void delete(UserData propertyData);
}