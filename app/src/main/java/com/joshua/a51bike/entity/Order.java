package com.joshua.a51bike.entity;

import java.sql.Timestamp;

public class Order {
     Integer carId;
	 Timestamp useHour;
	 Integer useMoney;
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
	public String toString(){
		return "carId "+carId + " useDistance "+useDistance+" useDistance "
				+useDistance+" useStartTime "+useStartTime+" useHour "+useHour;
	}
	
}
