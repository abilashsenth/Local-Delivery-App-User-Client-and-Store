package com.tuyuservices.tuyumain;

/* © 2020 All rights reserved. abilash432@gmail.com/@thenextbiggeek® Extending to Water360*/

public class Shop {
    float lat, lon, rating, dist;
    String shopId;
    String name, shopImageUrl, pass;
    int samplePrice;
    public Shop(){
        /* © 2020 All rights reserved. abilash432@gmail.com/@thenextbiggeek® Extending to Water360*/

    }

    public Shop(String id,float lat, float lon, String name, float rating,
                int samplePrice, String shopImageUrl, String pass){
        this.shopId = id;
        this.lat = lat;
        this.lon = lon;
        this.name = name;
        this.rating = rating;
        this.samplePrice = samplePrice;
        this.shopImageUrl = shopImageUrl;
        this.pass = pass;
    }
    public Shop(String id, String lat, String lon, String name, String rating,
                String samplePrice, String shopImageUrl, String pass){
        shopId = id;
        this.lat = Float.parseFloat(lat);
        this.lon = Float.parseFloat(lon);
        this.name = name;
        this.rating = Float.parseFloat(rating);
        this.samplePrice = Integer.parseInt(samplePrice);
        this.shopImageUrl = shopImageUrl;
        this.pass = pass;

    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getPass() {
        return pass;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public void setShopImageUrl(String shopImageUrl) {
        this.shopImageUrl = shopImageUrl;
    }

    public void setSamplePrice(int samplePrice) {
        this.samplePrice = samplePrice;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShopImageUrl() {
        return shopImageUrl;
    }

    public String getName() {
        return name;
    }

    public float getLat() {
        return lat;
    }

    public float getLon() {
        return lon;
    }

    public float getRating() {
        return rating;
    }

    public int getSamplePrice() {
        return samplePrice;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public void setLon(float lon) {
        this.lon = lon;
    }

    public float getDist() {
        return dist;
    }
    /* © 2020 All rights reserved. abilash432@gmail.com/@thenextbiggeek® Extending to Water360*/

    public void setDist(float dist) {
        this.dist = dist;
    }
}
