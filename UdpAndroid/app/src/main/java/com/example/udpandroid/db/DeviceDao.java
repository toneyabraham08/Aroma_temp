package com.example.udpandroid.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface DeviceDao {
    @Query("SELECT * FROM devicedata")
    List<DeviceData> getAll();

    @Query("SELECT * FROM devicedata WHERE unique_id IN (:userIds)")
    List<DeviceData> loadAllByIds(int[] userIds);

    @Query("SELECT * FROM devicedata WHERE unique_id LIKE :uniqueId LIMIT 1")
    DeviceData findById(String uniqueId);

    @Insert
    void insertAll(DeviceData... deviceDatas);

    @Delete
    void delete(DeviceData deviceData);

    @Update
    int updateDevice(DeviceData deviceData);
}