package com.joshua.a51bike.entity;

public class Car {
	public static final int STATE_AVALIABLE = 0;
	public static final int STATE_RENTED = 1;
	public static final int STATE_START = 2;
	public static final int STATE_PARKING = 3;
      int carId;
      String carName;
      int carPrice;
      String carX;
      String carY;
      int carState; // 0 可租 1不可租 2正在使用中 
      String carMac;

	public String getCarNum() {
		return carNum;
	}

	public void setCarNum(String carNum) {
		this.carNum = carNum;
	}

	String carNum;
	public String getCarMac() {
		return carMac;
	}
	public void setCarMac(String carMac) {
		this.carMac = carMac;
	}
	public int getCarId() {
		return carId;
	}
	public void setCarId(int carId) {
		this.carId = carId;
	}
	public String getCarName() {
		return carName;
	}
	public void setCarName(String carName) {
		this.carName = carName;
	}
	public int getCarPrice() {
		return carPrice;
	}
	public void setCarPrice(int carPrice) {
		this.carPrice = carPrice;
	}
	public String getCarX() {
		return carX;
	}
	public void setCarX(String carX) {
		this.carX = carX;
	}
	public String getCarY() {
		return carY;
	}
	public void setCarY(String carY) {
		this.carY = carY;
	}
	public int getCarState() {
		return carState;
	}
	public void setCarState(int carState) {
		this.carState = carState;
	}
}
