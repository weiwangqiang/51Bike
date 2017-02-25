package com.joshua.a51bike.entity;

import java.sql.Timestamp;
public class Recharge {
       Integer userId;
       Integer userCharge;
       Timestamp userTime;
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public Integer getUserCharge() {
		return userCharge;
	}
	public void setUserCharge(Integer userCharge) {
		this.userCharge = userCharge;
	}
	public Timestamp getUserTime() {
		return userTime;
	}
	public void setUserTime(Timestamp userTime) {
		this.userTime = userTime;
	}
	
}
