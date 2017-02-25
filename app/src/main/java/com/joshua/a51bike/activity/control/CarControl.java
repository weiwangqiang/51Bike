package com.joshua.a51bike.activity.control;

import com.joshua.a51bike.entity.Car;

/**
 * class description here
 *
 * @version 1.0.0
 * @outher wangqiang
 * @project 51Bike
 * @since 2017-02-08
 */
public class CarControl {
    private String TAG = "CarControl";
    private Car car = null;
    public CarControl() {
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }
}
