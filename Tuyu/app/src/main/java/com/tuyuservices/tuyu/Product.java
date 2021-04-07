package com.tuyuservices.tuyu;

/* © 2020 All rights reserved. abilash432@gmail.com/@thenextbiggeek® Extending to Water360*/

public class Product extends Shop {
    String productCode, productName, thumbnailUrl;
    float price;
    int count;
    int primaryKey;
    //extends shop but accessing shop data/methods from product is not recommended in mark1

    public Product(String productCode, String productName, String thumbnailUrl , float price){
        this.productCode = productCode;
        this.productName = productName;
        this.thumbnailUrl = thumbnailUrl;
        this.price = price;
    }
    public Product(String productCode, String productName, String thumbnailUrl , String price){
        this.productCode = productCode;
        this.productName = productName;
        this.thumbnailUrl = thumbnailUrl;
        if(price != "null") {
            this.price = Float.parseFloat(price);
        }
    }
    public Product(String productCode, String productName, String thumbnailUrl , String price,int primaryKey){
        this.productCode = productCode;
        this.productName = productName;
        this.thumbnailUrl = thumbnailUrl;
        this.primaryKey = primaryKey;
        if(price != "null") {
            this.price = Float.parseFloat(price);
        }
    }
    public Product(String productCode, String productName, String thumbnailUrl , float price, int primaryKey ){
        this.productCode = productCode;
        this.productName = productName;
        this.thumbnailUrl = thumbnailUrl;
        this.price = price;
        this.primaryKey = primaryKey;
    }

    public Product(String s, int i) {
        this.name = s;
        this.price = i;
    }

    public int getCount() {
        return count;
    }

    public int getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(int primaryKey) {
        this.primaryKey = primaryKey;
    }

    @Override
    public float getDist() {
        return super.getDist();
    }

    @Override
    public float getLat() {
        return super.getLat();
    }

    @Override
    public float getLon() {
        return super.getLon();
    }

    @Override
    public float getRating() {
        return super.getRating();
    }

    @Override
    public int getSamplePrice() {
        return super.getSamplePrice();
    }

    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public String getShopId() {
        return super.getShopId();
    }

    @Override
    public String getShopImageUrl() {
        return super.getShopImageUrl();
    }

    @Override
    public void setDist(float dist) {
        super.setDist(dist);
    }

    @Override
    public void setLat(float lat) {
        super.setLat(lat);
    }

    @Override
    public void setLon(float lon) {
        super.setLon(lon);
    }

    @Override
    public void setName(String name) {
        super.setName(name);
    }

    @Override
    public void setRating(float rating) {
        super.setRating(rating);
    }

    @Override
    public void setSamplePrice(int samplePrice) {
        super.setSamplePrice(samplePrice);
    }

    @Override
    public void setShopId(String shopId) {
        super.setShopId(shopId);
    }

    @Override
    public void setShopImageUrl(String shopImageUrl) {
        super.setShopImageUrl(shopImageUrl);
    }

    public int getProductCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }


    public float getPrice() {
        return price;
    }

    public String getProductCode() {
        return productCode;
    }

    public String getProductName() {
        return productName;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    /* © 2020 All rights reserved. abilash432@gmail.com/@thenextbiggeek® Extending to Water360*/


}
