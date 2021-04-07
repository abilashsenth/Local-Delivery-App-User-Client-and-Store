package com.tuyuservices.tuyu;

import android.location.Location;
import android.util.Log;

import java.util.ArrayList;
/* © 2020 All rights reserved. abilash432@gmail.com/@thenextbiggeek® Extending to Water360*/

public class LocationService {

    //LocationService object gets the user location, the list of shops and returns the shops within 5km
    Shop shop;
    private Location location;
    private ArrayList<Shop> shopArrayList;
    int kilometres;
    public LocationService(ArrayList<Shop> shopArrayList, float lat, float lon){
        location = new Location("serviceprovider");
        location.setLatitude(lat);
        location.setLongitude(lon);
        this.shopArrayList = shopArrayList;

    }

    Shop  temp;
    ArrayList<Shop> sortedShops = new ArrayList<Shop>();
    public ArrayList<Shop> sortLocation(int kilometres){
        this.kilometres = kilometres;
        //sorting the shop list by distance in ascending order
        for(int i=0;i<shopArrayList.size()-1;i++){
            for(int j=0;j<shopArrayList.size()-1;j++){
                if(distance(shopArrayList.get(j))>distance(shopArrayList.get(j+1))){
                    temp = shopArrayList.get(j);
                    shopArrayList.set(j,shopArrayList.get(j+1));
                    shopArrayList.set(j+1, temp);
                }
            }
        }
        //excluding the shops in the list with dist > threshold
        for(int i =0;i<shopArrayList.size();i++){
            if(distance(shopArrayList.get(i)) <(kilometres*1000)){
                sortedShops.add(shopArrayList.get(i));
            }
        }

        return sortedShops;
    }

    public float distance(Shop shop){
        Location shopLocation = new Location("");
        shopLocation.setLatitude( shop.lat);
        shopLocation.setLongitude( shop.lon);
        float distance = location.distanceTo(shopLocation);
        //we also embed the distance in each shop objects just as we calculate them
        shop.setDist(distance);
        return distance;

    }
}
/* © 2020 All rights reserved. abilash432@gmail.com/@thenextbiggeek® Extending to Water360*/

