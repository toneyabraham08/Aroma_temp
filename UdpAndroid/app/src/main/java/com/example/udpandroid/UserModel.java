package com.example.udpandroid;

import com.google.gson.GsonBuilder;

import java.io.Serializable;

public class UserModel implements Serializable {

	private String email;
	private String username;
	private String mobileNumber;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	private String password;

	// Constructor
	public UserModel(String email, String username, String mobileNumber,String password) {
		this.email = email;
		this.username = username;
		this.mobileNumber = mobileNumber;
		this.password = password;
	}


}
