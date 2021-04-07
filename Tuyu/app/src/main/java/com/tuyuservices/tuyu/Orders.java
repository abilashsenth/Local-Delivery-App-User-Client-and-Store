package com.tuyuservices.tuyu;

import java.io.Serializable;
/* © 2020 All rights reserved. abilash432@gmail.com/@thenextbiggeek® Extending to Water360*/

public class Orders implements Serializable {
    String OID, shopID, status, name, number, address, date, time, timepreference, totalAmount, paymentMethod, totalProducts;



    public Orders(String OID,String shopID, String status, String name, String number, String address, String date,
                  String time, String timepreference, String totalAmount, String paymentMethod, String totalProducts){
        this.OID = OID;
        this.shopID = shopID;
        this.status = status;
        this.name = name;
        this.number = number;
        this.address = address;
        this.date = date;
        this.time = time;
        this.timepreference = timepreference;
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
        this.totalProducts = totalProducts;
    }

    public Orders() {
    }

    public String getTotalProducts() {
        return totalProducts;
    }

    public void setTotalProducts(String totalProducts) {
        this.totalProducts = totalProducts;
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


    public String getOID() {
        return OID;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getShopID() {
        return shopID;
    }

    public String getStatus() {
        return status;
    }

    public String getTimepreference() {
        return timepreference;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setOID(String OID) {
        this.OID = OID;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }



    public void setShopID(String shopID) {
        this.shopID = shopID;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setTimepreference(String timepreference) {
        this.timepreference = timepreference;
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


    public String getTotalAmount() {
        return totalAmount;
    }



    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }
    /* © 2020 All rights reserved. abilash432@gmail.com/@thenextbiggeek® Extending to Water360*/

}