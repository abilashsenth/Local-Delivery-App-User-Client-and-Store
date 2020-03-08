package com.tuyuservices.tuyumain;

import java.io.Serializable;

public class Orders implements Serializable {
    String address, name, time, date, timepreference, servicesordered, totalAmount, number;

    public Orders(){

    }

    public Orders(String name, String address, String time, String date,
                  String timepreference, String servicesordered, String totalAmount, String number){
        this.name = name;
        this.address = address;
        this.time = time;
        this.date = date;
        this.number = number;
        this.timepreference = timepreference;
        this.servicesordered = servicesordered;
        this.totalAmount = totalAmount;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getAddress() {
        return address;
    }

    public String getDate() {
        return date;
    }

    public String getName() {
        return name;
    }

    public String getTime() {
        return time;
    }

    public String getTimepreference() {
        return timepreference;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setTimepreference(String timepreference) {
        this.timepreference = timepreference;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public String getServicesordered() {
        return servicesordered;
    }

    public void setServicesordered(String servicesordered) {
        this.servicesordered = servicesordered;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }
}

