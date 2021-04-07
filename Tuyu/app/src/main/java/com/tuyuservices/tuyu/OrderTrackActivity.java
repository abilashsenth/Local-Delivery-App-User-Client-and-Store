package com.tuyuservices.tuyu;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
/* © 2020 All rights reserved. abilash432@gmail.com/@thenextbiggeek® Extending to Water360*/

public class OrderTrackActivity extends FragmentActivity implements OnMapReadyCallback, RatingDialogListener {

    private GoogleMap mMap;

    String number, name, address, date, time, timepreference, services, userID, shopID, paymentMethod, shopNameText;
    int totalprice;
    TextView statusText;
    private static final String TAG = "Fbase";
    DatabaseReference databaseReference;
    String fBaseURL = "https://tuyuservices.firebaseio.com/";
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mRef;
    private double latitude;
    private double longitude;
    String partnerID, OID, shopName;
    private ValueEventListener valueEventListener2;
    private ValueEventListener cartValueEventListener;
    private ArrayList<Product> cartList;
    String userLatitude, userLongitude;
    private SharedPreferences sharedPreferences;

    FrameLayout animationFrame, trackingFrame;
    private int reserveCanCount;

    boolean orderExistsAlready;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_track);
        cartList = new ArrayList<>();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);


        Intent intent = getIntent();


        orderExistsAlready = intent.getBooleanExtra("orderExistsAlready", false);

        statusText = (TextView) findViewById(R.id.statustext);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        userLatitude = sharedPreferences.getString("latitude", "NULL");
        userLongitude = sharedPreferences.getString("longitude", "NULL");
        animationFrame = findViewById(R.id.animation_frame);
        trackingFrame = findViewById(R.id.tracking_frame);

        userID = intent.getStringExtra("userID");
        number = intent.getStringExtra("number");
        name = intent.getStringExtra("name");
        address = intent.getStringExtra("address");
        date = intent.getStringExtra("date");
        time = intent.getStringExtra("time");
        timepreference = intent.getStringExtra("timepreference");
        paymentMethod = intent.getStringExtra("paymentmethod");
        reserveCanCount = intent.getIntExtra("reservecancount", 0);
        totalprice = intent.getIntExtra("totalprice", 0);
        shopName = intent.getStringExtra("shopname");
        OID = synthesizeOID(date, time);
        listenOrderStatusNotifService();

        if (orderExistsAlready) {
            OID = intent.getStringExtra("OID");
            Log.e("orderExistsAlready", OID);
            animationFrame.setVisibility(View.GONE);
            trackingFrame.setVisibility(View.VISIBLE);

        } else {
            YoYo.with(Techniques.FadeIn)
                    .duration(2000)
                    .playOn(findViewById(R.id.animation_frame));
            Timer mTimer = new Timer();
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            // Stuff that updates the UI
                            animationFrame.setVisibility(View.GONE);
                            trackingFrame.setVisibility(View.VISIBLE);
                            YoYo.with(Techniques.FadeIn)
                                    .duration(1500)
                                    .playOn(findViewById(R.id.tracking_frame));
                        }
                    });
                }
            };
            mTimer.schedule(timerTask, 1000);
            getCartData();

        }


        updateUserAddress();
        listenOrderStatus();
        setOrderInformation();


        statusText.setText("Waiting To Recieve Order");
    }

    private void setOrderInformation() {
      Log.e("UserName", userID);
      Log.e("Shopname", shopName);
        TextView dateText = (TextView)  findViewById(R.id.order_track_date);
        TextView timeText = (TextView) findViewById(R.id.order_track_time);
        TextView slotTiming = (TextView) findViewById(R.id.slotTiming);
        TextView shopName = (TextView) findViewById(R.id.shop_name);


        TextView nameText = (TextView)  findViewById(R.id.name);
        TextView addressText = (TextView)  findViewById(R.id.address);
        TextView numberText = (TextView) findViewById(R.id.number);

        TextView total= (TextView) findViewById(R.id.total_amount);
        TextView total_final= (TextView) findViewById(R.id.total_amount_final);
        TextView serviceList = (TextView) findViewById(R.id.service_list);
        TextView deliverCharges = (TextView) findViewById(R.id.delivery_charges);


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }


    private void removeUserCartObject(String userID) {
        databaseReference.child("USERCART").child(userID).removeValue();
    }

    private void getCartData() {
        //checks whether USERCART data exists under the USERID
        // Read from the database
        cartValueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                if (dataSnapshot.child("USERCART").child(userID).hasChildren()) {
                    //cart value exists but could be null value. therefore loop and check for non null value to confirm prescence of a cart list data
                    for (DataSnapshot ds : dataSnapshot.child("USERCART").child(userID).getChildren()) {
                        shopID = String.valueOf(ds.getKey());
                        Log.e("SHOPIDVAL", shopID);
                        for (DataSnapshot mDs : ds.getChildren()) {
                            if (!String.valueOf(mDs.getValue()).equals("null")) {
                                Log.e("NONNULL", "true");
                                String productCode, productName, productThumbUrl, productPrice;

                                productCode = String.valueOf(dataSnapshot.child("PRODUCTS").child
                                        (shopID).child(String.valueOf(mDs.getValue())).getKey());

                                productName = String.valueOf(dataSnapshot.child("PRODUCTS").child
                                        (shopID).child(String.valueOf(mDs.getValue())).child("NAME").getValue());

                                productThumbUrl = String.valueOf(dataSnapshot.child("PRODUCTS").child
                                        (shopID).child(String.valueOf(mDs.getValue())).child("THUMBNAILURL").getValue());

                                productPrice = String.valueOf(dataSnapshot.child("PRODUCTS").child
                                        (shopID).child(String.valueOf(mDs.getValue())).child("PRICE").getValue());

                                //according to the existing db rules mds always is an int
                                Product p = new Product(productCode, productName, productThumbUrl, productPrice);
                                cartList.add(p);


                            }
                        }
                        Log.e("CARTPRDUCTLIST", String.valueOf(cartList.size()));
                    }
                }
                databaseReference.removeEventListener(cartValueEventListener);
                for (Product s : cartList) {
                    totalprice += s.getPrice();
                    Log.e("TOTALPRICE", String.valueOf(totalprice));

                }
                writeOrderObject(number, name, address, date, time, timepreference, totalprice);
                removeUserCartObject(userID);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

    }


    private void writeOrderObject(String number, String name, String address, String date,
                                  String time, String timepreference, int totalprice) {

        databaseReference.child("ORDERSPLACED").child(OID).child("NAME").setValue(name);
        databaseReference.child("ORDERSPLACED").child(OID).child("NUMBER").setValue(number);
        databaseReference.child("ORDERSPLACED").child(OID).child("ADDRESS").setValue(address);
        databaseReference.child("ORDERSPLACED").child(OID).child("DATE").setValue(date);
        databaseReference.child("ORDERSPLACED").child(OID).child("TIME").setValue(time);
        databaseReference.child("ORDERSPLACED").child(OID).child("TIMEPREFERENCE").setValue(timepreference);
        databaseReference.child("ORDERSPLACED").child(OID).child("SHOPID").setValue(shopID);
        databaseReference.child("ORDERSPLACED").child(OID).child("STATUS").setValue("NEW");
        databaseReference.child("ORDERSPLACED").child(OID).child("PAYMENTMETHOD").setValue(paymentMethod);
        databaseReference.child("ORDERSPLACED").child(OID).child("LATITUDE").setValue(userLatitude);
        databaseReference.child("ORDERSPLACED").child(OID).child("LONGITUDE").setValue(userLongitude);
        databaseReference.child("ORDERSPLACED").child(OID).child("RESERVECANCOUNT").setValue(reserveCanCount);
        databaseReference.child("ORDERSPLACED").child(OID).child("TOTALAMOUNT").setValue(String.valueOf(totalprice));

        //assigning NEW>SHOPID>ORDERID for shop app notification purpose
        databaseReference.child("NEW").child(shopID).setValue(OID);


        for (int i = 0; i < cartList.size(); i++) {
            //the loop exists to index the productCodes
            Product p = cartList.get(i);
            Log.e("ORDERSPLACEDProduct", String.valueOf(p.getProductCode()));
            databaseReference.child("ORDERSPLACED").child(OID).child("ORDERS").
                    child(String.valueOf(i)).setValue(p.getProductCode());
        }
    }


    ValueEventListener valueEventListener;
    private void listenOrderStatus() {
        Log.e("LISTENORDERSTATE", "true");
        final ImageView orderTrackStatusImageView = findViewById(R.id.order_status_view);
        // Read from the database
        valueEventListener = mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = String.valueOf(dataSnapshot.child("ORDERSPLACED").child(OID).child("STATUS").getValue());
                switch (value) {
                    case "NEW":
                        statusText.setText("ORDER Placed");
                        orderTrackStatusImageView.setImageResource(R.drawable.order_status_order_placed);
                        break;
                    case "ASSIGNED": //the order is assigned to a partner
                        statusText.setText("The order has been assigned to partner & will be on your way soon");
                        orderTrackStatusImageView.setImageResource(R.drawable.order_status_image_confirmed);
                        break;
                    case "ACCEPTED":
                        statusText.setText("ORDER ACCEPTED");
                        getLiveLocationAndId();
                        orderTrackStatusImageView.setImageResource(R.drawable.order_status_water_on_the_way);
                        break;
                    case "FINISHED":
                        statusText.setText("ORDER FINIShED");
                        openRatingView();
                        mRef.child("STATUS").child(number).setValue("DONE");
                        stopService(new Intent(OrderTrackActivity.this,UserNotificationService.class));
                        break;
                    default:
                        statusText.setText("WAiting to be assigned");
                        orderTrackStatusImageView.setImageResource(R.drawable.order_status_waiting_to_be_assigned);
                        break;
                }


            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });


    }

    //starts the service when order is placed
    private void listenOrderStatusNotifService() {
        Intent serviceIntent = new Intent(OrderTrackActivity.this, UserNotificationService.class);
        Log.e("TEST", shopID+ OID);
        serviceIntent.putExtra("shopID", shopID);
        serviceIntent.putExtra("OID", OID);
        startService(serviceIntent);
    }


    private void getLiveLocationAndId() {
        // Read from the database
        valueEventListener2 = mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Log.e("CHECK", "CHECK2");

                for (DataSnapshot ds : dataSnapshot.child("ACCEPTED").child(OID).child(shopID).getChildren()) {
                    String key = ds.getKey();
                    String value = String.valueOf(ds.getValue());
                    if (value.equals("ASSIGNED")) {
                        partnerID = key;
                        updateLocation(partnerID);
                    }
                }
                mRef.removeEventListener(valueEventListener2);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });


    }


    //location data can be taken
    private Location mLocation;
    private FusedLocationProviderClient fusedLocationClient;

    private void updateUserAddress() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    mLocation = location;
                    updateAddress();
                }
            }
        });



    }

    private void updateAddress() {
        if(mLocation != null){

            Geocoder mGeocoder = new Geocoder(this, Locale.getDefault());
            try {
                List<Address> addresses = mGeocoder.getFromLocation(mLocation.getLatitude(),
                        mLocation.getLongitude(), 1);
                latitude = mLocation.getLatitude();
                longitude = mLocation.getLongitude();
                String address1 = addresses.get(0).getAddressLine(0);
                String city = addresses.get(0).getLocality();
                //now onto updating onto the textviews
                Log.e("Address", address1);
                Log.e("Address", city);

                Log.e("LATLONG", " " +latitude+longitude);
                moveMap();


            } catch (IOException e) {
                e.printStackTrace();
            }
        }



    }

    private void moveMap() {
        // Add a marker in Sydney and move the camera
        Log.e("LATLONG", " " +latitude+longitude);
        LatLng userHome = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(userHome).title("Home"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(userHome));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userHome,17));
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.getUiSettings().setAllGesturesEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);


    }


    Partner partner = new Partner();
    private void updateLocation(final String partnerID) {
        // Read from the database
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for (DataSnapshot ds : dataSnapshot.child("PARTNER").child(shopID).child(partnerID).getChildren()) {
                    double latitude = 0, longitude=0;
                    String name = (String) ds.getKey();
                    partner.setPartnerID(partnerID);

                    if(name.equals("LATITUDE")){
                        latitude = (double) ds.getValue();
                        partner.setLat(latitude);

                    }else if(name.equals("LONGITUDE")){
                        longitude = (double) ds.getValue();
                        partner.setLongitude(longitude);

                    }

                }

                Log.e("num", partnerID+"  "+partner.getLat()+"   "+partner.getLongitude());
                // Add a marker in Sydney and move the camera
                LatLng partnerLoc = new LatLng(partner.getLat(), partner.getLongitude());
                mMap.addMarker(new MarkerOptions().position(partnerLoc).title("Marker in partner"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(partnerLoc));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(partnerLoc,15));
                mMap.getUiSettings().setCompassEnabled(false);
                mMap.getUiSettings().setAllGesturesEnabled(false);
                mMap.getUiSettings().setMapToolbarEnabled(false);






            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    private void openRatingView() {
        Intent intent = new Intent(OrderTrackActivity.this, RatingsActivity.class);
        intent.putExtra("date", date);
        intent.putExtra("OID", OID);
        intent.putExtra("time", time);
        intent.putExtra("UID", partnerID);
        startActivity(intent);
    }

    private String synthesizeOID(String date, String time) {

        String last3 = userID == null || userID.length() < 3 ?
                userID : userID.substring(userID.length() - 3);
        date = date.replace("-", "");
        time = time.replace(":","");
        String oidSerialKey = "O"+date+time+last3;
        return oidSerialKey;

    }


    public void feedbackCall(View view) {
        new AppRatingDialog.Builder()
                .setPositiveButtonText("Submit")
                .setNegativeButtonText("Cancel")
                .setNeutralButtonText("Later")
                .setNoteDescriptions(Arrays.asList("Very Bad", "Not good", "Quite ok", "Very Good", "Excellent !!!"))
                .setDefaultRating(2)
                .setTitle("Submit your Water360 feedback")
                .setDescription("Type out your valuable feedback, for us to improve our services")
                .setCommentInputEnabled(true)
                .setDefaultComment("Feedback Column")
                .setStarColor(R.color.starColor)
                .setNoteDescriptionTextColor(R.color.starColor)
                .setTitleTextColor(R.color.starColor)
                .setDescriptionTextColor(R.color.starColor)
                .setHint("Please write your comment here ...")
                .setHintTextColor(R.color.starColor)
                .setCommentTextColor(R.color.starColor)
                .setCommentBackgroundColor(R.color.colorPrimaryDark)
                .setWindowAnimation(R.style.MyDialogFadeAnimation)
                .setCancelable(false)
                .setCanceledOnTouchOutside(false)
                .create(OrderTrackActivity.this)
                .show();

    }

    @Override
    public void onPositiveButtonClicked(int rate, String comment) {
        // interpret results, send it to analytics etc...
        databaseReference.child("FEEDBACK").child(OID).child(String.valueOf(rate)).setValue(comment);
    }

    @Override
    public void onNegativeButtonClicked() {
        databaseReference.child("FEEDBACK").child(OID).child("NULL").setValue("NULL");

    }

    @Override
    public void onNeutralButtonClicked() {
        databaseReference.child("FEEDBACK").child(OID).child("NEUTRAL").setValue("NEUTRAL");

    }
    /* © 2020 All rights reserved. abilash432@gmail.com/@thenextbiggeek® Extending to Water360*/

}
