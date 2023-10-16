package com.example.udpandroid.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {DeviceData.class,PropertyData.class,UserData.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {
    public abstract DeviceDao deviceDao();
    public abstract PropertyDaoDao propertyDao();
    public abstract UserDaoDao userDaoDao();
}