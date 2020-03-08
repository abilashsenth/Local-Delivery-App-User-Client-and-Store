package com.tuyuservices.tuyu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class TrialDatabaseActivity extends AppCompatActivity {


    private static final String TAG = "Fbase" ;
    DatabaseReference databaseReference;
    String fBaseURL = "https://tuyuservices.firebaseio.com/";
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trial_database);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();



        /*
        writeNewService("electrical", "Wiring", "mcb", 120);
        writeNewService("electrical", "Wiring", "new electric point", 150);
        writeNewService("electrical","Wiring", "3 Phase panel board", 150);
        writeNewService("electrical","fan", "ceiling fan", 150);
        writeNewService("electrical","fan", "exhaust fan", 160);
        writeNewService("electrical","Light", "Tube Lights", 150);
        writeNewService("electrical","Light", "Fancy Light", 160);
        writeNewService("electrical","Light", "Sockets and holders", 170);
        writeNewService("electrical","Inverter","Basic Inverters", 699);
        writeNewService("plumbing","Tap Washbasin Sink","Tap", 160);
        writeNewService("plumbing","Tap Washbasin Sink","Washbasin ", 550);
        writeNewService("plumbing","Tap Washbasin Sink","Kitchen Sink", 550);
        writeNewService("plumbing","Blocks and Leakage","Kitchen Sink", 402);
        writeNewService("plumbing","Blocks and Leakage","Western Toilet", 300);
        writeNewService("plumbing","Tap","Basic Tap fitting", 182);
        writeNewService("Toilet & Sanitary","Bathroom Fitting","Shower", 210);
        writeNewService("Toilet & Sanitary","Water Tank","Basic WaterTank", 1350);
        writeNewService("Toilet & Sanitary","Pipeline and pumps","Basic Pipeline and pumps", 1000);
        writeNewService("Airconditioner","Basic AC Services","Installation", 699);
        writeNewService("Airconditioner","Basic AC Services","Gas Refill", 1700);
        writeNewService("Airconditioner","Basic AC Services","Gas Topup", 250);
        writeNewService("Airconditioner","Basic AC Services","Uninstallation", 499);
        writeNewService("Airconditioner","Basic AC Services","Dry Servicing", 350);
        writeNewService("Airconditioner","Basic AC Services","Wet Servicing", 400);
        writeNewService("Airconditioner","Basic AC Services","Repair", 250);
        writeNewService("CCTV Services","IP NVR Camera","New Installation", 250);
        writeNewService("CCTV Services","IP NVR Camera","NVR Not Working", 250);
        writeNewService("CCTV Services","IP NVR Camera","Camera Not Working", 250);
        writeNewService("CCTV Services","IP NVR Camera","POE Poblem", 250);
        writeNewService("CCTV Services","IP NVR Camera","Camera Repair", 250);
        writeNewService("CCTV Services","HD DVR Camera","New Installation", 250);
        writeNewService("CCTV Services","HD DVR Camera","Camera Not working", 250);
        writeNewService("CCTV Services","HD DVR Camera","Powersupply", 250);
        writeNewService("CCTV Services","HD DVR Camera","Cable", 250);
        writeNewService("CCTV Services","HD DVR Camera","Repair", 250);
        writeNewService("Computers","Desktops","CPU Repair", 250);
        writeNewService("Computers","Desktops","Display Problem", 250);
        writeNewService("Computers","Laptops","Display Problem", 250);
        writeNewService("Computers","Laptops","No Power On", 250);
        writeNewService("Computers","Laptops","Keyboard  Mouse", 250);
        writeNewService("Computers","Laptops","Hard Disk Problem", 250);
        writeNewService("Computers","Laptops","Laptop Broken", 250);
        writeNewService("Computers","Printers","HP  Canon", 250);
        writeNewService("Computers","Printers","Toner Refilling", 350);
        writeNewService("Computers","Printers","Toner Replacement", 250);
        writeNewService("Computers","Printers","No power on", 250);
        writeNewService("Computers","Printers","Paper Jammed", 250);
        writeNewService("Computers","Printers","USB Port Problem", 250);
        writeNewService("Carpentry", "General Carpentry", "Wooden Partition", 199);
        writeNewService("Carpentry", "General Carpentry", "Mesh", 199);
        writeNewService("Carpentry", "General Carpentry", "Making a new sofa", 199);
        writeNewService("Carpentry", "General Carpentry", "Making a wooden chair", 199);
        writeNewService("Carpentry", "General Carpentry", "Assembling Bed", 199);
        writeNewService("Carpentry", "General Carpentry", "other carpentry services", 199);
        */





        checkDbChange();



    }

    private void checkDbChange() {


        // Read from the database
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for(DataSnapshot ds : dataSnapshot.child("electrical").getChildren()){
                    String value = (String) ds.getKey();
                    Log.d(TAG, "Value is: " + value);

                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });



    }

    private void writeNewService (String maincat, String subcat, String name, int price){
        Service electricalService = new Service(name, price);
        databaseReference.child(maincat).child(subcat).child(name).setValue(price);



    }
}
