package com.tuyuservices.tuyupartner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TaskActivity extends AppCompatActivity {
    private static final int PERMISSION_FINE_LOCATION = 20;
    boolean loggedIn;
    String partnerID;
    private SharedPreferences sharedPreferences;
    private double latitude;
    private double longitude;
    private boolean locationPermission;

    String fBaseURL = "https://tuyuservices.firebaseio.com/";
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mRef;
    private String TAG = "tag";
    List<String> numbers;
    List<Orders> mList;
    private DatabaseReference databaseReference;
    private RecyclerView mRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        loggedIn = LoadBool();
        checkLoggedIn();
        checkLocationPermission();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();
        //get userID
        partnerID = LoadID();

        Log.e("UID is ", partnerID);
        if(locationPermission){
            Intent intent = new Intent(TaskActivity.this, MyService.class);
            intent.putExtra("UID", partnerID);
            startService(intent);
        }

        numbers = new ArrayList<>();
        mList = new ArrayList<>();

        getNumberList();
        getTasks();
        //checkDBChange();



    }
    ValueEventListener valueEventListener;


    private void getNumberList() {

        // Read from the database
        valueEventListener = mRef.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(DataSnapshot dataSnapshot) {
               // This method is called once with the initial value and again
               // whenever data at this location is updated.
               for (DataSnapshot ds : dataSnapshot.child("ASSIGNED").getChildren()) {
                   String number = (String) ds.getKey();
                   String partnerUID = (String) ds.getValue();
                   if (partnerUID.equals(partnerID)) {
                       numbers.add(number);
                   }
                   Log.e("NUMBER ORDERS", number);

               }
               mRef.removeEventListener(valueEventListener);


           }

           @Override
           public void onCancelled(DatabaseError error) {
               // Failed to read value
               Log.w(TAG, "Failed to read value.", error.toException());
           }
       });
    }

    Orders orders = new Orders();
    ValueEventListener valueEventListener1;
    private void getTasks() {
        // Read from the database
        valueEventListener1 = mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for (String num : numbers) {
                    for (DataSnapshot ds : dataSnapshot.child("ORDERSPLACED").child(num).getChildren()) {
                        String value = (String) ds.getKey();
                        String price = String.valueOf(ds.getValue());
                        if (value.equals("NAME")) {
                            orders.setName(price);
                        } else if (value.equals("ADDRESS")) {
                            orders.setAddress(price);
                        } else if (value.equals("TIME")) {
                            orders.setTime(price);
                        } else if (value.equals("DATE")) {
                            orders.setDate(price);
                        } else if (value.equals("TIMEPREFERENCE")) {
                            orders.setTimepreference(price);
                        } else if (value.equals("SERVICESORDERED")) {
                            orders.setServicesordered(price);
                        } else if (value.equals("TOTALAMOUNT")) {
                            orders.setTotalAmount(price);
                        }

                    }
                    orders.setNumber(String.valueOf(num));
                    Log.e("num", num);
                    mList.add(orders);
                    orders = new Orders();
                }


                mRef.removeEventListener(valueEventListener1);
                setupCustomRecyclerView(mList);



            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }


    /**
     *the recyclerview setup of the first list where the recyclerview contains an image and title
     *
     **/


    ListAdapter listAdapter;

    public void setupCustomRecyclerView(List<Orders> list) {
        listAdapter = new ListAdapter(list);
        mRecyclerView = (RecyclerView) findViewById(R.id.ordersRecyclerView);
        mRecyclerView.setHasFixedSize(true);


        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);


        mRecyclerView.setAdapter(listAdapter);
        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, mRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                // Write your code here
                //pass servicetag and then the string[position]
                Log.e("Tag", " " + position);
                startResponseActivity(position);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


    }

    private void startResponseActivity(int position) {
        //position corresponds to the position of mList which was chosen;
        Orders mOrder = mList.get(position);
        Intent responseIntent  =new Intent(TaskActivity.this, StatusResponseActivity.class);
        responseIntent.putExtra("Orders", mOrder);
        startActivity(responseIntent);
    }


    private void checkLoggedIn() {
        if(!loggedIn){
            //user is not logged in. go back to login screen
            Intent loginIntent = new Intent(TaskActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            Toast.makeText(getApplicationContext(), "please login to continue", Toast.LENGTH_SHORT).show();

        }
        //the user is logged in and ready to continue




    }


    public boolean LoadBool(){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return  sharedPreferences.getBoolean("login", false);
    }

    public String LoadID(){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return  sharedPreferences.getString("UID", "NULL");
    }

    public void partnerLogoutClick(View view) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("login", false);
        editor.apply();
        loggedIn = false;
        stopService(new Intent(TaskActivity.this,MyService.class));

        checkLoggedIn();



    }








    /**
     * permission handling shall be done here
     */

    private void checkLocationPermission() {

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            //location permission required
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_FINE_LOCATION);

        }else{
            //location already granted
            locationPermission = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        switch (requestCode){
            case PERMISSION_FINE_LOCATION:{
                if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //now that there's permission go for address check
                    locationPermission = true;
                }else{
                    //permission denied
                    //TODO HANDLE
                }
                return;
            }

        }
    }


    public void refreshButton(View v){
        recreate();
    }

}
