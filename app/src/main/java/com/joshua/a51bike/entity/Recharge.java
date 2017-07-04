package com.joshua.a51bike.entity;

import java.sql.Timestamp;
public class Recharge {
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	Integer id;
       Integer userid;
       Integer userCharge;
       Timestamp userTime;
	public Integer getUserId() {
		return userid;
	}
	public void setUserId(Integer userid) {
		this.userid = userid;
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
