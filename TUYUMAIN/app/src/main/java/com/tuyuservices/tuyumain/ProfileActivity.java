package com.tuyuservices.tuyumain;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
/* © 2020 All rights reserved. abilash432@gmail.com/@thenextbiggeek® Extending to Water360*/

public class ProfileActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        loadProfileFragment();
    }

    //to load the user credentials for profile fragment

    private void loadProfileFragment() {
        String shopID = loadValSharedPref("shopID");
        String name = loadValSharedPref("NAME");
        String latitude = loadValSharedPref("LAT");
        String longitude = loadValSharedPref("LONG");
        String rating = loadValSharedPref("RATING");
        String samplePrice = loadValSharedPref("SAMPLEPRICE");
        String shopImageURL = loadValSharedPref("SHOPIMAGE");
        String passkey = loadValSharedPref("PASS");


        Log.e("name", name);

        TextView shopIDText = (TextView)  findViewById(R.id.profile_shopID);
        TextView nameText = (TextView) findViewById(R.id.profile_name);
        TextView latText = (TextView) findViewById(R.id.profile_lat);
        TextView longText = (TextView) findViewById(R.id.profile_long);
        TextView passText = (TextView) findViewById(R.id.profile_pass);
        TextView ratingText = (TextView) findViewById(R.id.profile_rating);
        TextView samplePriceText = (TextView) findViewById(R.id.profile_samplePrice);
        ImageView shopImage = (ImageView) findViewById(R.id.profile_shopImage);


        /* © 2020 All rights reserved. abilash432@gmail.com/@thenextbiggeek® Extending to Water360*/


        shopIDText.setText(shopID);
        nameText.setText(name);
        latText.setText(latitude);
        longText.setText(longitude);
        passText.setText(passkey);
        ratingText.setText(rating);
        samplePriceText.setText(samplePrice);
        ImageLoadTask imageLoadTask = new ImageLoadTask(shopImageURL,shopImage,getApplicationContext());






    }


    public String loadValSharedPref(String key){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return  sharedPreferences.getString(key, "NULL");
    }


    public void logOut(View v){
        SaveLoggedIn("login", false);
        Intent loginIntent = new Intent(ProfileActivity.this, LoginActivity.class);
        startActivity(loginIntent);
    }

    public void SaveLoggedIn(String key, boolean value){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ProfileActivity.this, OrdersActivity.class);
        startActivity(intent);
    }
    /* © 2020 All rights reserved. abilash432@gmail.com/@thenextbiggeek® Extending to Water360*/

}
