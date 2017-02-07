package com.joshua.a51bike.entity;

public class Car {
  	private  int carId;
	private   String carName;
	private  int carPrice;
	private  String carX;
	private  String carY;
	private  int carState; // 0 可租 1不可租 2正在使用中
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
