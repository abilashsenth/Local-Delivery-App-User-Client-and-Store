package com.tuyuservices.tuyumain;
/* © 2020 All rights reserved. abilash432@gmail.com/@thenextbiggeek® Extending to Water360*/

import java.util.ArrayList;

public class Product extends Orders {
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



    public int getProductCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    /* © 2020 All rights reserved. abilash432@gmail.com/@thenextbiggeek® Extending to Water360*/

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