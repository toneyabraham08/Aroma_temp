package com.example.udpandroid.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class PropertyData implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "current_user")
    public int current_user;

    @ColumnInfo(name = "property_name")
    public String property_name;

    @ColumnInfo(name = "property_image")
    public String property_image;

}