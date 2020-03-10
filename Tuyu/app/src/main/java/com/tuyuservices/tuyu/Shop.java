package com.tuyuservices.tuyu;

public class Shop {
    String UID;
    String name;
    String shopImageUrl;
    String locationCoordinate;
    String rating;
    String samplePrice;

    public Shop(String UID, String name, String rating, String samplePrice, String shopImageUrl, String locationCoordinate ){
        this.UID = UID;
        this.name = name;
        this.shopImageUrl = shopImageUrl;
        this.locationCoordinate = locationCoordinate;
        this.rating = rating;
        this.samplePrice = samplePrice;
    }

    public String getUID() {
        return UID;
    }

    public String getLocationCoordinate() {
        return locationCoordinate;
    }

    public String getName() {
        return name;
    }

    public String getRating() {
        return rating;
    }

    public String getSamplePrice() {
        return samplePrice;
    }

    public String getShopImageUrl() {
        return shopImageUrl;
    }

    public void setLocationCoordinate(String locationCoordinate) {
        this.locationCoordinate = locationCoordinate;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRating(String rating) {
        rating = rating;
    }

    public void setSamplePrice(String samplePrice) {
        this.samplePrice = samplePrice;
    }

    public void setShopImageUrl(String shopImageUrl) {
        this.shopImageUrl = shopImageUrl;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

}
