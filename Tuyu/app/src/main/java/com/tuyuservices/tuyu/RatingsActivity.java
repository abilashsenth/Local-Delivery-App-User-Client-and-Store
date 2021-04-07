package com.tuyuservices.tuyu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import java.util.Arrays;

/* © 2020 All rights reserved. abilash432@gmail.com/@thenextbiggeek® Extending to Water360*/

public class RatingsActivity extends AppCompatActivity implements RatingDialogListener {

    private String date;
    private String time;
    private String UID, uniqueIdentifierDate, OID;
    double rating;

    //OID - ORDER ID
    //UID - USERID

    DatabaseReference databaseReference;
    String fBaseURL = "https://tuyuservices.firebaseio.com/";
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ratings);
        Intent intent = getIntent();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();

        date = intent.getStringExtra("date");
        time = intent.getStringExtra("time");
        uniqueIdentifierDate = date+"/"+time;
        UID = intent.getStringExtra("UID");
        OID = intent.getStringExtra("OID");

        showDialog();
    }


        private void showDialog() {
            new AppRatingDialog.Builder()
                    .setPositiveButtonText("Submit")
                    .setNegativeButtonText("Cancel")
                    .setNeutralButtonText("Later")
                    .setNoteDescriptions(Arrays.asList("Very Bad", "Not good", "Quite ok", "Very Good", "Excellent !!!"))
                    .setDefaultRating(2)
                    .setTitle("Rate your TUYU service")
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
                    .create(RatingsActivity.this)
                    .show();
        }


    @Override
    public void onPositiveButtonClicked(int rate, String comment) {
        // interpret results, send it to analytics etc...
        Log.e("RATING", "Rating "+rate+ " "+ "comment"+comment);
        databaseReference.child("RATING").child(OID).child(UID).child(uniqueIdentifierDate).child(String.valueOf(rate)).setValue(comment);
        goToMain();
    }

    @Override
    public void onNegativeButtonClicked() {
        databaseReference.child("RATING").child(OID).child(UID).child(uniqueIdentifierDate).child("NULL").setValue("NULL");
        Log.e("RATING", "rating cancelled");
        goToMain();

    }

    @Override
    public void onNeutralButtonClicked() {
        databaseReference.child("RATING").child(uniqueIdentifierDate).child(UID).child("NEUTRAL").setValue("NEUTRAL");
        Log.e("RATING", "NEUTRAL");
        goToMain();
    }

    public void goToMain(){
        Intent intent = new Intent(RatingsActivity.this, HomeActivity.class);
        startActivity(intent);
    }
}
/* © 2020 All rights reserved. abilash432@gmail.com/@thenextbiggeek® Extending to Water360*/

