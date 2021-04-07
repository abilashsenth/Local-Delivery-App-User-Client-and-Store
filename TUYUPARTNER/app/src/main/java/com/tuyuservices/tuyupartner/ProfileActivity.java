package com.tuyuservices.tuyupartner;

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
    private boolean loggedIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        loadProfileFragment();

    }
    private void loadProfileFragment() {
        String shopID = loadValSharedPref("shopID");
        String partnerID = loadValSharedPref("partnerID");
        String number = loadValSharedPref("partnerNumber");
        String pass = loadValSharedPref("partnerPass");

        Log.e("name", partnerID);

        TextView shopIDText = (TextView)  findViewById(R.id.profile_shop_ID);
        TextView partnerIDText = (TextView) findViewById(R.id.profile_ID);
        TextView passText = (TextView) findViewById(R.id.profile_Pass);
        TextView numberText = (TextView) findViewById(R.id.profile_Number);

        /* © 2020 All rights reserved. abilash432@gmail.com/@thenextbiggeek® Extending to Water360*/

        shopIDText.setText(shopID);
        partnerIDText.setText(partnerID);
        passText.setText(pass);
        numberText.setText(number);


    }

    public String loadValSharedPref(String key){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return  sharedPreferences.getString(key, "NULL");
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ProfileActivity.this, TaskActivity.class);
        startActivity(intent);
    }



    public void logOut(View view) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("login", false);
        editor.apply();
        loggedIn = false;
        stopService(new Intent(ProfileActivity.this,MyService.class));
        Intent intent= new Intent(ProfileActivity.this, LoginActivity.class);
        startActivity(intent);
        /* © 2020 All rights reserved. abilash432@gmail.com/@thenextbiggeek® Extending to Water360*/

    }
}
