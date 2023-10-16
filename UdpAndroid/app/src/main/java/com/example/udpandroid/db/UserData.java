package com.example.udpandroid.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class UserData implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "email")
    public String email;

    @ColumnInfo(name = "username")
    public String username;

    @ColumnInfo(name = "mobileNumber")
    public String mobileNumber;

    @ColumnInfo(name = "password")
    public String password;

}