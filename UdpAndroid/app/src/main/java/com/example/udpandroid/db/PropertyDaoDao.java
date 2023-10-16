package com.example.udpandroid.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PropertyDaoDao {
    @Query("SELECT * FROM propertydata")
    List<PropertyData> getAll();

    @Query("SELECT * FROM propertydata WHERE current_user LIKE :cuser")
    List<PropertyData> getAllCuser(int cuser);

    @Query("SELECT * FROM propertydata WHERE uid IN (:userIds)")
    List<PropertyData> loadAllByIds(int[] userIds);

    @Query("SELECT * FROM propertydata WHERE uid LIKE :uid LIMIT 1")
    PropertyData findById(String uid);



    @Insert
    void insertAll(PropertyData... propertyDatas);

    @Delete
    void delete(PropertyData propertyData);
}