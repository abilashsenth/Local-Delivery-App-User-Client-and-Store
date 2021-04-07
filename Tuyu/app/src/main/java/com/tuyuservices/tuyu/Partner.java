package com.tuyuservices.tuyu;
/* © 2020 All rights reserved. abilash432@gmail.com/@thenextbiggeek® Extending to Water360*/

public class Partner {
    String partnerID;
    double lat, longitude;
    Partner(String a){
        partnerID = a;
    }
    Partner(){
    }
    Partner(String a, double b, double c){
        partnerID = a;
        lat = b;
        longitude = c;
    }

    public double getLat() {
        return lat;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getPartnerID() {
        return partnerID;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setPartnerID(String partnerID) {
        this.partnerID = partnerID;
    }
    /* © 2020 All rights reserved. abilash432@gmail.com/@thenextbiggeek® Extending to Water360*/

}
