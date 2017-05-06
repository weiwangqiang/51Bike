package com.joshua.a51bike.entity;

import java.sql.Timestamp;

public class UserAndUse {
	   Integer id;
	   Integer state;
	   Integer userId;
	   Integer carId;

	public Integer getCarState() {
		return carState;
	}

	public void setCarState(Integer carState) {
		this.carState = carState;
	}

    	Integer carState;
	   Timestamp useHour;
	   Integer useMoney;
	   Integer useDistance;
	   Timestamp useStartTime;
	   Timestamp useEndTime;

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getState() {
		return state;
	}
	public void setState(Integer state) {
		this.state = state;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public Integer getCarId() {
		return carId;
	}
	public void setCarId(Integer carId) {
		this.carId = carId;
	}
	public Timestamp getUseHour() {
		return useHour;
	}
	public void setUseHour(Timestamp useHour) {
		this.useHour = useHour;
	}
	public Integer getUseMoney() {
		return useMoney;
	}
	public void setUseMoney(Integer useMoney) {
		this.useMoney = useMoney;
	}
	public Integer getUseDistance() {
		return useDistance;
	}
	public void setUseDistance(Integer useDistance) {
		this.useDistance = useDistance;
	}
	public Timestamp getUseStartTime() {
		return useStartTime;
	}
	public void setUseStartTime(Timestamp useStartTime) {
		this.useStartTime = useStartTime;
	}
	public Timestamp getUseEndTime() {
		return useEndTime;
	}
	public void setUseEndTime(Timestamp useEndTime) {
		this.useEndTime = useEndTime;
	}
	   
	   

}
