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
    public static Car car = null;
    public static CarControl carControl = new CarControl();
    private CarControl() {
    }
    public static CarControl getCarControl(){
        return carControl;
    }
    public Car getCar() {
        return this.car;
    }

    public void setCar(Car car) {
        this.car = car;
    }
}
