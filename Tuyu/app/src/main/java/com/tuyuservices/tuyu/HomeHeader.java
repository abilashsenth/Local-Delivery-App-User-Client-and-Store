package com.tuyuservices.tuyu;

/* © 2020 All rights reserved. abilash432@gmail.com/@thenextbiggeek® Extending to Water360*/

public class HomeHeader {
    String name, address;
    boolean ordersEnabled;
    Orders orders;
    public  HomeHeader(String name, String address, boolean ordersEnabled){
        this.address = address;
        this.name = name;
        this.ordersEnabled = ordersEnabled;
    }
    public  HomeHeader(String name, String address, boolean ordersEnabled,Orders orders){
        this.address = address;
        this.name = name;
        this.ordersEnabled = ordersEnabled;
        this.orders = orders;
    }


    public Orders getOrders() {
        return orders;
    }

    public void setOrders(Orders orders) {
        this.orders = orders;
    }

    public String getAddress() {
        return address;
    }

    public String getName() {
        return name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOrdersEnabled(boolean ordersEnabled) {
        this.ordersEnabled = ordersEnabled;
    }

    public boolean isOrdersEnabled() {
        return ordersEnabled;
    }
    /* © 2020 All rights reserved. abilash432@gmail.com/@thenextbiggeek® Extending to Water360*/

}
