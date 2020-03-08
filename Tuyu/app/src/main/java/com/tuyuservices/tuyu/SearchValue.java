package com.tuyuservices.tuyu;

import android.os.Build;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class SearchValue {

    private DatabaseReference dbRef;
    private int lengthCount;
    private FirebaseDatabase mFirebaseDatabase;



    String queryText;
    List<Service> services;
    List<Service> searchedList;

    //requires querytext
    public SearchValue(String query){
        queryText = query;
        services = new ArrayList<Service>();
        searchedList = new ArrayList<Service>();
        services.clear();
        searchedList.clear();
        searchFirstLayer();
    }


     public void searchFirstLayer(){

        Log.e("QueryRef", queryText);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        dbRef = mFirebaseDatabase.getReference();
        // Read from the database
        dbRef.addValueEventListener(new ValueEventListener() {



            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                int count = 0;
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    String value = (String) ds.getKey();
                    //Log.e(" ", value);
                    if(!value.equals("ACCEPTED")&&
                            !value.equals("ASSIGNED")&&
                            !value.equals("STATUS")&&
                            !value.equals("FINISHED")&&
                            !value.equals("LOCATION")&&
                            !value.equals("ORDERSPLACED")&&
                            !value.equals("PARTNER")&&
                            !value.equals("RATING")){
                    searchSecondLayer(value);
                    count++;
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

    private void searchSecondLayer(final String value) {

        // Read from the database
        dbRef.addValueEventListener(new ValueEventListener() {


            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                int count = 0;
                for(DataSnapshot ds : dataSnapshot.child(value).getChildren()){
                    String value2 = (String) ds.getKey();
                    //Log.e("VALUE", value+ "subcat "+ value2);

                    searchThirdLayer(value, value2);
                    count++;
                }


            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }


    private void searchThirdLayer(final String value, final String value2) {

        // Read from the database
        dbRef.addValueEventListener(new ValueEventListener() {


            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                int count = 0;
                for(DataSnapshot ds : dataSnapshot.child(value).child(value2).getChildren()){
                    String key = (String) ds.getKey();
                    int val = Math.toIntExact( (Long) ds.getValue());
                    Service s = new Service(key,val);
                    services.add(s);

                }
                searchForVal();


            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    //general format for query
    String q = "(?i)("+queryText+").";

    public List<Service> searchForVal() {
        for(Service service : services){
            if(service.getServiceName().contains(queryText)){
                //the query matches the title of the item of the last child node.
                //now make a separatearraylist of the content containing services and return them
                searchedList.add(service);
                Log.e("ServiceTag2", service.getServiceName());


            }
        }




        return searchedList;

    }



}
