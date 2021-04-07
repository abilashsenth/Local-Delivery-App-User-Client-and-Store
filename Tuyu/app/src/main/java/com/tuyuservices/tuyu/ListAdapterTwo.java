package com.tuyuservices.tuyu;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class ListAdapterTwo extends RecyclerView.Adapter<ListAdapterTwo.MyViewHolder> {
    /* © 2020 All rights reserved. abilash432@gmail.com/@thenextbiggeek® Extending to Water360*/

    private final SharedPreferences sharedPreferences;
    public Context mContext;
    private DatabaseReference mDatabaseReference;
    private String userID, shopID;
    int cartCount=0;

    public interface ClickListener {

        void onPositionClicked(int position);
        void onLongClicked(int position);
    }
    public List<Product> productList;
    private ClickListener listener;
    boolean isCartEmpty = true;
    public ArrayList<Product> cartList;

    public ListAdapterTwo(List<Product> mList, ClickListener listener, Context context, String shopID){
        productList = mList;
        this.listener =listener;
        this.shopID = shopID;
        cartList = new ArrayList<>();
        mContext = context;
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        userID =   sharedPreferences.getString("NUMBER", "NULL");


    }

    @NonNull
    @Override
    public ListAdapterTwo.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_type_two, parent, false);
        return new ListAdapterTwo.MyViewHolder(itemView, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ListAdapterTwo.MyViewHolder holder, int position) {
        Product product = productList.get(position);
        String thumbnailUrl = product.getThumbnailUrl();
        holder.name.setText(product.getProductName());
        holder.price.setText(String.valueOf(product.getPrice())+"₹");
        ImageLoadTask imageLoadTask = new ImageLoadTask(thumbnailUrl,holder.productThumbnail,mContext);
       // imageLoadTask.execute();
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    ValueEventListener valueEventListener3;
    private void getCount(final Product s) {

        // Read from the database
        valueEventListener3 = mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               int count = (int) dataSnapshot.child("USERCART").child(userID).child(shopID).getChildrenCount();
                mDatabaseReference.child("USERCART").child(userID).child(shopID).
                        child(String.valueOf(count+1)).setValue(s.getProductCode());
                mDatabaseReference.removeEventListener(valueEventListener3);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }


    boolean integrity;
    private ValueEventListener valueEventListener;
    //checks whether theres existing cart data for the user, and if the existing data is fit
    private void checkCartIntegrity() {

            // Read from the database
            valueEventListener = mDatabaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    int shopCount = (int) dataSnapshot.child("USERCART").child(userID).getChildrenCount();
                    Log.e("shopCount", String.valueOf(shopCount));
                    if(shopCount==0){
                        integrity= true;
                    }else if(shopCount ==1){
                        if(dataSnapshot.child("USERCART").child(userID).child(shopID).exists()){
                            integrity =  true;
                        }else{
                            for(DataSnapshot ds : dataSnapshot.child("USERCART").child(userID).getChildren()){
                                if(ds.getKey() != shopID){
                                    mDatabaseReference.child("USERCART").child(userID).child(ds.getKey()).removeValue();
                                }
                            }
                        }
                    }else{
                        for(DataSnapshot ds : dataSnapshot.child("USERCART").child(userID).getChildren()){
                            if(ds.getKey() != shopID){
                                mDatabaseReference.child("USERCART").child(userID).child(ds.getKey()).removeValue();
                            }
                        }
                    }
                    mDatabaseReference.removeEventListener(valueEventListener);
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w(TAG, "Failed to read value.", error.toException());
                }
            });

    }
    private ValueEventListener valueEventListener2;

    private void deleteProductValue(final String productCode) {
        // Read from the database
        valueEventListener2 = mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.child("USERCART").child(userID).child(shopID).getChildren()){
                    if(ds.getValue()==productCode){
                        mDatabaseReference.child("USERCART").child(userID).child(shopID).child(ds.getKey()).setValue("null");
                        break;
                    }
                }
                mDatabaseReference.removeEventListener(valueEventListener2);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });


    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener{
        ImageView productThumbnail;
        public TextView name,price, indicator;
        LinearLayout list_display_one, list_display_two;
        public ImageView addToCart, plus, minus;
        public WeakReference<ClickListener> listenerRef;

        public MyViewHolder(View view, ClickListener listener) {
            super(view);
            listenerRef = new WeakReference<>(listener);

            productThumbnail = (ImageView) view.findViewById(R.id.list_item_type_two_thumbnail);
            name = (TextView) view.findViewById(R.id.list_item_type_two_text);
            price = (TextView) view.findViewById(R.id.list_item_type_two_price);

            list_display_one = (LinearLayout) view.findViewById(R.id.list_display_one);
            list_display_two = (LinearLayout) view.findViewById(R.id.list_display_two);


            indicator = (TextView) view.findViewById(R.id.indicator_number);

            addToCart =  view.findViewById(R.id.add_button_list);
            plus = view.findViewById(R.id.plus_button_list);
            minus = view.findViewById(R.id.minus_button_list);

            addToCart.setOnClickListener(this);
            plus.setOnClickListener(this);
            minus.setOnClickListener(this);

        }

        //count is used for indication purposes only
        int count = 1;


        @Override
        public void onClick(View v) {

            checkCartIntegrity();

            if (v.getId() == addToCart.getId()) {
                //one instance of the order is added to the class


                list_display_one.setVisibility(View.GONE);
                list_display_two.setVisibility(View.VISIBLE);
                indicator.setText(String.valueOf(count));

                Product s = productList.get(getAdapterPosition());
                mDatabaseReference = FirebaseDatabase.getInstance().getReference();
                getCount(s);
                cartCount++;

                ((ListActivitySecondary)mContext).refreshCartPreview();




            }else if(v.getId() == plus.getId()){


                if(count <8){
                    count++;
                    indicator.setText(String.valueOf(count));
                    //Maximum of only 8 orders allowed per service
                    Log.e("TAG", String.valueOf(getAdapterPosition()));

                    Product s = productList.get(getAdapterPosition());
                    getCount(s);
                    ((ListActivitySecondary)mContext).refreshCartPreview();


                }

            }else if(v.getId() == minus.getId()){
                if(count>0){
                    count =count-1;
                    indicator.setText(String.valueOf(count));
                    Log.e("TAG", String.valueOf(getAdapterPosition()));

                    Product s = productList.get(getAdapterPosition());
                    deleteProductValue(s.getProductCode());
                    ((ListActivitySecondary)mContext).refreshCartPreview();

                }else{
                    ((ListActivitySecondary)mContext).refreshCartPreview();
                }

            }

            listenerRef.get().onPositionClicked(getAdapterPosition());
        }
    }

    /* © 2020 All rights reserved. abilash432@gmail.com/@thenextbiggeek® Extending to Water360*/

}
