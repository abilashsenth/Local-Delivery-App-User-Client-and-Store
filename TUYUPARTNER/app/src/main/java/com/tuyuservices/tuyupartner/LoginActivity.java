package com.tuyuservices.tuyupartner;

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

public class LoginActivity extends AppCompatActivity {
    final String TAG = "Fbase" ;
    String fBaseURL = "https://tuyuservices.firebaseio.com/";
    String shopID;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference databaseReference;
    /* © 2020 All rights reserved. abilash432@gmail.com/@thenextbiggeek® Extending to Water360*/

    EditText usernameEdittext, passwordEdittext;
    List<Partner> partnerList;
    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = mFirebaseDatabase.getReference();
        getPartnerInstances();


        boolean isLoggedIn = LoadBool();
        if(isLoggedIn){
            //user is already logged in, going straight to task
            Toast.makeText(getApplicationContext(), "Welcome", Toast.LENGTH_SHORT).show();
            Intent loginIntent = new Intent(LoginActivity.this, TaskActivity.class);
            startActivity(loginIntent);
        }

    }

    private void getPartnerInstances() {


        partnerList = new ArrayList<>();
        // Read from the database
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String partners = String.valueOf(dataSnapshot.child("PARTNER").getChildrenCount());
                Log.e("PARTNERS", partners);



                for(DataSnapshot ds : dataSnapshot.child("PARTNER").getChildren()){
                   String shopID = ds.getKey();
                   for(DataSnapshot ds1: ds.getChildren()){
                       String partnerID = ds1.getKey();

                       String partnerNumber = String.valueOf(ds1.child("NUMBER").getValue());
                       String partnerLat = String.valueOf(ds1.child("LATITUDE").getValue());
                       String partnerLong = String.valueOf(ds1.child("LONGITUDE").getValue());
                       String partnerPass = String.valueOf(ds1.child("PASS").getValue());
                       Partner p = new Partner(partnerID, partnerPass, partnerLat, partnerLong, partnerNumber, shopID);
                       partnerList.add(p);
                       Log.e("PARTNERTAG", partnerID);

                   }
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });


    }


    public void partnerLoginClick(View view) {
        usernameEdittext = (EditText) findViewById(R.id.usernameEditText);
        passwordEdittext = (EditText) findViewById(R.id.passwordEditText);
        String tempUID= usernameEdittext.getText().toString();
        String tempPASS = passwordEdittext.getText().toString();

        Log.e("TEMPUID", tempUID);
        Log.e("TEMPPASS", tempPASS);

        for(Partner p : partnerList){

            if(tempUID.equals(p.getName()) && tempPASS.equals(p.getPass())){
                Toast.makeText(getApplicationContext(), "Welcome " + p.getName().toString(), Toast.LENGTH_SHORT).show();
                SaveBool("login", true);
                SaveSharedPreferences("partnerID", p.getName());
                SaveSharedPreferences("shopID", p.getShopID());
                SaveSharedPreferences("partnerNumber", p.getNumber());
                SaveSharedPreferences("partnerPass", p.getPass());

                Intent intent = new Intent(LoginActivity.this, TaskActivity.class);
                startActivity(intent);

            }
        }
    }

    public void SaveBool(String key, boolean value){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }
    public void SaveSharedPreferences(String key, String value){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }
    /* © 2020 All rights reserved. abilash432@gmail.com/@thenextbiggeek® Extending to Water360*/

    public boolean LoadBool(){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return  sharedPreferences.getBoolean("login", false);
    }


}
