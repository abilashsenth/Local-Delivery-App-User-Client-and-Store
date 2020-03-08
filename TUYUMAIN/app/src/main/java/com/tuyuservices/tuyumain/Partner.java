package com.tuyuservices.tuyumain;

public class Partner {
    String UID;
    double lat, longitude;
    Partner(String a){
        UID = a;
    }
    Partner(){
    }
    Partner(String a, double b, double c){
        UID = a;
        lat = b;
        longitude = c;
    }

    public double getLat() {
        return lat;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getUID() {
        return UID;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }
}
