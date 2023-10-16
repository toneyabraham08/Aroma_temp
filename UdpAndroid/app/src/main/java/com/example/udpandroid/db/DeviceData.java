package com.example.udpandroid.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class DeviceData implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "unique_id")
    public String unique_id;

    @ColumnInfo(name = "device_name")
    public String device_name="";

    @ColumnInfo(name = "ip")
    public String ip;

    @ColumnInfo(name = "liquid_level")
    public int liquid_level;

    @ColumnInfo(name = "intensity_level")
    public int intensity_level;

    @ColumnInfo(name = "zone1_start")
    public int zone1_start;

    @ColumnInfo(name = "zone1_end")
    public int zone1_end;

    @ColumnInfo(name = "zone2_start")
    public int zone2_start;

    @ColumnInfo(name = "zone2_end")
    public int zone2_end;

    @ColumnInfo(name = "zone3_start")
    public int zone3_start;

    @ColumnInfo(name = "zone3_end")
    public int zone3_end;

    @ColumnInfo(name = "zone4_start")
    public int zone4_start;

    @ColumnInfo(name = "zone4_end")
    public int zone4_end;

    @ColumnInfo(name = "zone1_start_m")
    public int zone1_start_m;

    @ColumnInfo(name = "zone1_end_m")
    public int zone1_end_m;

    @ColumnInfo(name = "zone2_start_m")
    public int zone2_start_m;

    @ColumnInfo(name = "zone2_end_m")
    public int zone2_end_m;

    @ColumnInfo(name = "zone3_start_m")
    public int zone3_start_m;

    @ColumnInfo(name = "zone3_end_m")
    public int zone3_end_m;

    @ColumnInfo(name = "zone4_start_m")
    public int zone4_start_m;

    @ColumnInfo(name = "zone4_end_m")
    public int zone4_end_m;

    @ColumnInfo(name = "status_switch")
    public boolean status_switch;

    private boolean switchStatus;
    public boolean getSwitchStatus() {
        return switchStatus;
    }
    public void setSwitchStatus(boolean switchStatus) {
        this.switchStatus = switchStatus;
    }

}