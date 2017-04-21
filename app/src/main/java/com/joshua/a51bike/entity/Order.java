package com.joshua.a51bike.entity;

import java.sql.Time;
import java.sql.Timestamp;
public class Order {
       Integer carId;
       Time useHour;
       Integer useMoney;
//       Timestamp useTime;
       Timestamp useStartTime;

	public Integer getUseDistance() {
		return useDistance;
	}

	public void setUseDistance(Integer useDistance) {
		this.useDistance = useDistance;
	}

	Integer useDistance;
	Timestamp useEndTime;
	public Integer getCarId() {
		return carId;
	}
	public void setCarId(Integer carId) {
		this.carId = carId;
	}
	public Time getUseHour() {
		return useHour;
	}
	public void setUseHour(Time useHour) {
		this.useHour = useHour;
	}
	public Integer getUseMoney() {
		return useMoney;
	}
	public void setUseMoney(Integer useMoney) {
		this.useMoney = useMoney;
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
