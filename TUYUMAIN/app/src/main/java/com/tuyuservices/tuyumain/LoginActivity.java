package com.tuyuservices.tuyumain;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
/* © 2020 All rights reserved. abilash432@gmail.com/@thenextbiggeek® Extending to Water360*/

public class LoginActivity extends AppCompatActivity {
    final String TAG = "Fbase" ;
    String fBaseURL = "https://tuyuservices.firebaseio.com/";
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference databaseReference;

    EditText usernameEdittext, passwordEdittext;
    ArrayList<Shop> shopList;
    private SharedPreferences sharedPreferences;
    private ValueEventListener valueEventListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = mFirebaseDatabase.getReference();
        getUserNamePasswords();


        boolean isLoggedIn = LoadBool();
        if(isLoggedIn){
            //user is already logged in, going straight to task
            Toast.makeText(getApplicationContext(), "Welcome", Toast.LENGTH_SHORT).show();
            Intent loginIntent = new Intent(LoginActivity.this, OrdersActivity.class);
            startActivity(loginIntent);
        }

    }

    Shop shop;
    private void getUserNamePasswords() {

        shopList = new ArrayList<>();
        // Read from the database
        valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for(DataSnapshot ds : dataSnapshot.child("SHOP").getChildren()){
                    String value = (String) ds.getKey();
                    Log.e("HOMEACTIVITY_SHOPSID", value);
                    shop = new Shop(value,String.valueOf(ds.child("LAT").getValue()),
                            String.valueOf( ds.child("LONG").getValue()),
                            String.valueOf(ds.child("NAME").getValue()),
                            String.valueOf(ds.child("RATING").getValue()),
                            String.valueOf(ds.child("SAMPLEPRICE").getValue()),
                            String.valueOf(ds.child("SHOPIMAGE").getValue()),
                            String.valueOf(ds.child("PASS").getValue())
                            );
                    shopList.add(shop);

                }
                //setupCustomRecyclerView(serviceList);
                databaseReference.removeEventListener(valueEventListener);


            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });


    }


    public void shopLoginClick(View view) {
        usernameEdittext = (EditText) findViewById(R.id.usernameEditText);
        passwordEdittext = (EditText) findViewById(R.id.passwordEditText);
        String tempUID= usernameEdittext.getText().toString();
        String tempPASS = passwordEdittext.getText().toString();

        Log.e("TEMPUID", tempUID);
        Log.e("TEMPPASS", tempPASS);
        /* © 2020 All rights reserved. abilash432@gmail.com/@thenextbiggeek® Extending to Water360*/

        for(Shop p : shopList){
            if(tempUID.equals(p.getShopId()) && tempPASS.equals(p.getPass())){
                Toast.makeText(getApplicationContext(), "Welcome " + p.getShopId().toString(), Toast.LENGTH_SHORT).show();
                SaveBool("login", true);
                SaveID("shopID", p.getShopId().toString());
                savetoSharedPref("LAT", String.valueOf(p.getLat()));
                savetoSharedPref("LONG", String.valueOf(p.getLon()));
                savetoSharedPref("NAME",p.getName());
                savetoSharedPref("PASS",p.getPass());
                savetoSharedPref("RATING", String.valueOf(p.getRating()));
                savetoSharedPref("SAMPLEPRICE", String.valueOf(p.getSamplePrice()));
                savetoSharedPref("SHOPIMAGE",p.getShopImageUrl());


                Intent intent = new Intent(LoginActivity.this, OrdersActivity.class);
                startActivity(intent);

            }else{
                //Toast.makeText(getApplicationContext(), "Wrong UserID or Password", Toast.LENGTH_SHORT).show();

            }
        }
    }

    public void SaveBool(String key, boolean value){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }
    public void SaveID(String key, String value){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public boolean LoadBool(){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return  sharedPreferences.getBoolean("login", false);
    }



    public void savetoSharedPref(String key, String value){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    /* © 2020 All rights reserved. abilash432@gmail.com/@thenextbiggeek® Extending to Water360*/

}
