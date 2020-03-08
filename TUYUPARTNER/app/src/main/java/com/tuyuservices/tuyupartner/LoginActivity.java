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
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mRef;

    EditText usernameEdittext, passwordEdittext;
    List<Partner> mList;
    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();


        getUserNamePasswords();


        boolean isLoggedIn = LoadBool();
        if(isLoggedIn){
            //user is already logged in, going straight to task
            Toast.makeText(getApplicationContext(), "Welcome", Toast.LENGTH_SHORT).show();
            Intent loginIntent = new Intent(LoginActivity.this, TaskActivity.class);
            startActivity(loginIntent);
        }

    }

    private void getUserNamePasswords() {

        mList= new ArrayList<>();
        // Read from the database
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for(DataSnapshot ds : dataSnapshot.child("PARTNER").getChildren()){
                   String userID = ds.getKey();
                   String password = String.valueOf(ds.getValue());
                   Partner p = new Partner(userID, password);
                   mList.add(p);
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

        for(Partner p : mList){
            if(tempUID.equals(p.getUserID()) && tempPASS.equals(p.getPassword())){
                Toast.makeText(getApplicationContext(), "Welcome " + p.getUserID().toString(), Toast.LENGTH_SHORT).show();
                SaveBool("login", true);
                SaveID("UID", p.getUserID().toString());
                Intent intent = new Intent(LoginActivity.this, TaskActivity.class);
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


}
