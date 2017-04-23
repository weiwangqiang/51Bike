package com.joshua.a51bike.entity;

import java.sql.Time;
public class Order {
       Integer carId;
       Time useHour;
       Integer useMoney;
//       Timestamp useTime;
long useStartTime;

	public Integer getUseDistance() {
		return useDistance;
	}

	public void setUseDistance(Integer useDistance) {
		this.useDistance = useDistance;
	}

	Integer useDistance;
	long useEndTime;
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

	public long getUseStartTime() {
		return useStartTime;
	}
	public void setUseStartTime(long useStartTime) {
		this.useStartTime = useStartTime;
	}
	public long getUseEndTime() {
		return useEndTime;
	}
	public void setUseEndTime(long useEndTime) {
		this.useEndTime = useEndTime;
	}
	   
	
}
