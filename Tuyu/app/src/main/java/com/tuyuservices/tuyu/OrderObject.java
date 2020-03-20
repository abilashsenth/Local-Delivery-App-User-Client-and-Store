package com.tuyuservices.tuyu;

public class OrderObject {

    /**
     * order object replaces the existing system of passing cart information between HomeActivity, CartActivity and ListActivitySecondary.
     * the main purpose is to introduce a unique order ID for users to track multiple orders, as well as efficient cart info handling
     * Under ORDER UID = {shopUID, name, address, mobile, order date, time, timepref, services, amount paid}
     */



    String OrderUID, ShopUID, name, address, mobile, date, time, timepref, services, amountPaid;

}
