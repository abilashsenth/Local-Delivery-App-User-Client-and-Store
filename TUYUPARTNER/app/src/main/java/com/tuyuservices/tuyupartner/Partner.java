package com.tuyuservices.tuyupartner;

public class Partner {

    String userID;
    String password;

    public Partner(String userID, String password){
        this.userID = userID;
        this.password = password;
    }

    public String getUserID(){
        return userID;
    }

    public String getPassword(){
        return password;
    }

    public void setUserID(String i){
        userID = i;
    }
    public void setPassword(String i){
        password =i;
    }


}
