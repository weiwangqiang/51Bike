package com.joshua.a51bike.entity;

/**
 * class description here
 *
 * @version 1.0.0
 * @outher wangqiang
 * @project 51Bike
 * @since 2017-04-15
 */
public class CarState {
    private String TAG = "CarState";
    public static final int STATE_AVALIABLE = 0;
    public static final int STATE_RENTED = 1;
    public static final int STATE_START = 2;
    public static final int STATE_PARKING = 3;
    public CarState() {
    }
}
