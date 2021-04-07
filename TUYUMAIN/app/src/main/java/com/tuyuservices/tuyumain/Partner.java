package com.tuyuservices.tuyumain;
/* © 2020 All rights reserved. abilash432@gmail.com/@thenextbiggeek® Extending to Water360*/

public class Partner {
    String name,pass, number;
    double lat, longitude;
    Partner(String name, String pass, String lat, String lon, String number){
        this.name = name;
        this.pass = pass;
        this.lat = Double.valueOf(lat);
        this.longitude = Double.valueOf(lon);
        this.number = number;
    }
    Partner(){/* © 2020 All rights reserved. abilash432@gmail.com/@thenextbiggeek® Extending to Water360*/

    }
    Partner(String a,String b, float c, float d, String number){
        name = a;
        pass = b;
        lat = c;
        longitude = d;
        this.number = number;
    }

    public String getNumber() {
        return number;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setNumber(String number) {
        this.number = number;
    }


    public double getLat() {
        return lat;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getName() {
        return name;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPass() {
        return pass;
    }
    /* © 2020 All rights reserved. abilash432@gmail.com/@thenextbiggeek® Extending to Water360*/

    public void setPass(String pass) {
        this.pass = pass;
    }
}
