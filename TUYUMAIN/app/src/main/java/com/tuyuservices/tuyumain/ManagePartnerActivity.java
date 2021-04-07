package com.tuyuservices.tuyumain;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/* © 2020 All rights reserved. abilash432@gmail.com/@thenextbiggeek® Extending to Water360*/

public class ManagePartnerActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;
    DatabaseReference mDatabaseReference;
    ValueEventListener mValueEventListener;
    private ArrayList<Partner> mPartnerList;
    private String TAG = "ManagePartnerActivity";
    private String shopID, shopLat, shopLon;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_partner);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mRecyclerView = (RecyclerView) findViewById(R.id.partnerManagementRecyclerView);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        shopID = sharedPreferences.getString("shopID", "NULL");
        shopLat = sharedPreferences.getString("LAT", "NULL");
        shopLon = sharedPreferences.getString("LONG", "NULL");
        retreivePartnerData();

    }

    ValueEventListener valueEventListener;

    void retreivePartnerData() {
        mPartnerList = new ArrayList<>();
        // Read from the database
        valueEventListener = mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                if (dataSnapshot.child("PARTNER").child(shopID).exists()) {
                    for (DataSnapshot ds : dataSnapshot.child("PARTNER").child(shopID).getChildren()) {
                        String partnerName = (String) ds.getKey();
                        String partnerPass = String.valueOf(ds.child("PASS").getValue());
                        String partnerLAT = String.valueOf(ds.child("LATITUDE").getValue());
                        String partnerLON = String.valueOf(ds.child("LONGITUDE").getValue());
                        String partnerNumber = String.valueOf(ds.child("NUMBER").getValue());
                        Partner mPartner;

                        mPartner = new Partner(partnerName, partnerPass, partnerLAT, partnerLON, partnerNumber);
                        mPartnerList.add(mPartner);
                    }
                }
                setupCustomRecyclerView(mPartnerList);
                mDatabaseReference.removeEventListener(valueEventListener);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

    }

    /* © 2020 All rights reserved. abilash432@gmail.com/@thenextbiggeek® Extending to Water360*/

    ListAdapterManagement listAdapter;

    public void setupCustomRecyclerView(ArrayList<Partner> list) {

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        //shopID = sharedPreferences.getString("SHOPID", "NULL");

        listAdapter = new ListAdapterManagement(list, new ListAdapterManagement.ClickListener() {
            @Override
            public void onPositionClicked(int position) {
            }
            @Override
            public void onLongClicked(int position) {
            }
        }, this, shopID);

        mRecyclerView = (RecyclerView) findViewById(R.id.partnerManagementRecyclerView);
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
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


    }

    public void addPartner(View v){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setMessage("Create new partner");
        alert.setTitle("Partner Add");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

    // Add a TextView here for the "Title" label, as noted in the comments
        final EditText partnerIDEdit = new EditText(this);
        partnerIDEdit.setHint("PARTNERID Should be incremental number");
        layout.addView(partnerIDEdit); // Notice this is an add method

    // Add another TextView here for the "Description" label
        final EditText partnerPassEdit = new EditText(this);
        partnerPassEdit.setHint("Passkey");
        layout.addView(partnerPassEdit); // Another add method

        // Add another TextView here for the "Description" label
        final EditText partnerNumberEdit = new EditText(this);
        partnerNumberEdit.setHint("Partner Phone num");
        layout.addView(partnerNumberEdit); // Another add method

        alert.setView(layout); // Again this is a set method, not add


        alert.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //What ever you want to do with the value
                Editable name = partnerIDEdit.getText();
                Editable pass = partnerPassEdit.getText();
                Editable number = partnerNumberEdit.getText();

                writeNewPartner(name, pass, number);

                //OR
                //String text = edittext.getText().toString();
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // what ever you want to do with No option.

            }
        });

        alert.show();

    }

    private void writeNewPartner(Editable name, Editable pass, Editable num) {
        String partnerID = "PARTNER"+shopID+name.toString();
        String password = pass.toString();
        String number = num.toString();
        mDatabaseReference.child("PARTNER").child(shopID).child(partnerID).child("NAME").setValue(partnerID);
        mDatabaseReference.child("PARTNER").child(shopID).child(partnerID).child("PASS").setValue(password);
        mDatabaseReference.child("PARTNER").child(shopID).child(partnerID).child("LATITUDE").setValue(shopLat);
        mDatabaseReference.child("PARTNER").child(shopID).child(partnerID).child("LONGITUDE").setValue(shopLon);
        mDatabaseReference.child("PARTNER").child(shopID).child(partnerID).child("NUMBER").setValue(number);


        Log.e("PARTNERWRITE","new partner added" + partnerID);
        retreivePartnerData();
    }

    public void dialUserIntent(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null));
        startActivity(intent);
    }
}
/* © 2020 All rights reserved. abilash432@gmail.com/@thenextbiggeek® Extending to Water360*/

