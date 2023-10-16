package com.example.udpandroid;

import android.widget.Switch;

import com.google.gson.GsonBuilder;

import java.io.Serializable;

public class DeviceModel implements Serializable {

	private String uniqueId;
	private String ip;
	private int device_image;
	private int liquidLevel;

	private Switch statusSwitch;
	private String[] timeZones;

	public String getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getDevice_image() {
		return device_image;
	}

	public void setDevice_image(int device_image) {
		this.device_image = device_image;
	}

	public int getLiquidLevel() {
		return liquidLevel;
	}
	public Switch getstatusSwitch(){
		return statusSwitch;
	}

	public void setLiquidLevel(int liquidLevel) {
		this.liquidLevel = liquidLevel;
	}

	public void setStatusSwitch(Switch statusSwitch){
		this.statusSwitch = statusSwitch;
	}

	public String[] getTimeZones() {
		return timeZones;
	}

	public void setTimeZones(String[] timeZones) {
		this.timeZones = timeZones;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	private boolean status;

	// Constructor
	public DeviceModel(String uniqueId, String device_ip, int device_image,int liquidLevel,boolean status,String[] timeZones,Switch statusSwitch) {
		this.uniqueId = uniqueId;
		this.ip = device_ip;
		this.device_image = device_image;
		this.liquidLevel = liquidLevel;
		this.status = status;
		this.timeZones = timeZones;
		this.statusSwitch = statusSwitch;
	}

	@Override
	public String toString() {
		return new GsonBuilder().create().toJson(this, DeviceModel.class);
	}

}
