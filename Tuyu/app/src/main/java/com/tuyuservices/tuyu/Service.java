package com.tuyuservices.tuyu;

import android.os.Parcel;
import android.os.Parcelable;

/* © 2020 All rights reserved. abilash432@gmail.com/@thenextbiggeek® Extending to Water360*/

public class Service implements Parcelable {

     int price;
     String serviceName;
     String imageUri;


     //service instance for listonetype
    public Service(String serviceName, String imageUri){
        this.serviceName = serviceName;
        this.imageUri = imageUri;
    }

    public Service(Parcel p){
        this.serviceName = p.readString();
        this.price = p.readInt();

    }



    //service instance
    public Service(String serviceNamex, int pricex){
        serviceName = serviceNamex;
        price = pricex;
    }


    public int getPrice(){
        return price;
    }

    String getServiceName(){
        return serviceName;
    }

    String getImageUri(){
        return imageUri;
    }

    public void setPrice(int price){
        this.price = price;
    }

    public void setServiceName(String serviceName){
        this.serviceName  = serviceName;
    }

    public void setImageUri(String imageUri){
        this.setImageUri(imageUri);
    }


    //nothing much to do here
    public static final Creator<Service> CREATOR = new Creator<Service>() {
        @Override
        public Service createFromParcel(Parcel in) {
            return new Service(in);
        }

        @Override
        public Service[] newArray(int size) {
            return new Service[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }


    //here the parcel is written with the string an
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(serviceName);
        dest.writeInt(price);
    }

    /* © 2020 All rights reserved. abilash432@gmail.com/@thenextbiggeek® Extending to Water360*/

}
