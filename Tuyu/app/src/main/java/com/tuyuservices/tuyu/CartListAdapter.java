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

import static androidx.constraintlayout.widget.Constraints.TAG;
/* © 2020 All rights reserved. abilash432@gmail.com/@thenextbiggeek® Extending to Water360*/

public class CartListAdapter extends RecyclerView.Adapter<CartListAdapter.MyViewHolder> {


    private final SharedPreferences sharedPreferences;
    public ArrayList<Product> cartList, unSortedList;
    Context mContext;
    ClickListener clickListener;
    private DatabaseReference mDatabaseReference;
    String userID, shopID;
    int cartCount=0;


    public interface ClickListener {

        void onPositionClicked(int position);
        void onLongClicked(int position);
    }



    public CartListAdapter(ArrayList<Product> mList,ClickListener clickListener, Context mContext, String shopID){
        unSortedList = mList;
        cartList = sizeSortArrayList(mList);
        this.clickListener = clickListener;
        this.mContext = mContext;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        userID =   sharedPreferences.getString("NUMBER", "NULL");
        this.shopID = shopID;
    }

    private ArrayList<Product> sizeSortArrayList(ArrayList<Product> mListx) {
        ArrayList<Product> list = mListx;
        int count = 0;
        for(Product p : mListx){
            for(Product q: mListx){
                if(p.getProductCode().equals(q.productCode)){
                    p.setCount(count);
                    count++;
                }
            }
            count =0;
        }
        for(Product p :mListx){
            Log.e("SSA", String.valueOf(p.getProductCount()));

        }

        for(int i=0;i<mListx.size();i++){
           for(int j =0;j<mListx.size();j++){
               if(mListx.get(i).getProductCode().equals(mListx.get(j).getProductCode()) &&(mListx.get(i).getPrimaryKey()!=mListx.get(j).getPrimaryKey())){
                   mListx.remove(j);
                   j--;
               }
           }
        }
        return  mListx;
    }




    @NonNull
    @Override
    public CartListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_cart, parent, false);
        return new  CartListAdapter.MyViewHolder(itemView, clickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CartListAdapter.MyViewHolder holder, int position) {
        Product product = cartList.get(position);
        Log.e("ONBINDVIEWHOLDER", product.getProductName());
        holder.title.setText(product.getProductName());
        holder.price.setText(String.valueOf(product.getPrice()*(product.getProductCount()+1)));
        holder.indicator.setText(String.valueOf(product.getProductCount()+1));
        ImageLoadTask imageLoadTask = new ImageLoadTask(String.valueOf(cartList.get(position).
                thumbnailUrl),holder.productThumbnail, mContext);
    }

    @Override                                       
    public int getItemCount() {
        return cartList.size();
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



    private ValueEventListener valueEventListener2;
    private void deleteProductValue(String productCode) {
        // Read from the database
        final String code = productCode;
        Log.e("PRODUCTCODE", code);

        valueEventListener2 = mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.child("USERCART").child(userID).child(shopID).getChildren()){
                    if(String.valueOf(ds.getValue()).equals(code)){
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

    //View Holder class
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView addToCart, plus, minus;
        TextView title, price, indicator;
        ImageView productThumbnail;
        public WeakReference<CartListAdapter.ClickListener> listenerRef;





        public MyViewHolder(View view, ClickListener clickListener) {
            super(view);
            listenerRef = new WeakReference<>(clickListener);

            title = (TextView) view.findViewById(R.id.list_item_cart_text);
            price = (TextView) view.findViewById(R.id.list_item_cart_price);
            productThumbnail = (ImageView) view.findViewById(R.id.list_item_cart_thumbnail);


            plus = (ImageView) view.findViewById(R.id.plus_button_list_cart);
            indicator = (TextView) view.findViewById(R.id.indicator_number_list_cart);
            minus = (ImageView) view.findViewById(R.id.minus_button_list_cart);



            plus.setOnClickListener(this);
            minus.setOnClickListener(this);
        }


        int count;
        @Override
        public void onClick(View v) {
            checkCartIntegrity();

            if(v.getId() == plus.getId()){
                Log.e("TAG", String.valueOf(getAdapterPosition()));
                count = Integer.parseInt(String.valueOf(indicator.getText()));
                count++;
                indicator.setText(String.valueOf(count));
                Product s = cartList.get(getAdapterPosition());
                getCount(s);
                price.setText(String.valueOf(s.getPrice()*count));

                //((CartActivity)mContext).refreshCartPreview();


            }else if(v.getId() == minus.getId()){
                count = Integer.parseInt(String.valueOf(indicator.getText()));

                if(count>0){
                    count = count-1;
                    indicator.setText(String.valueOf(count));
                    Log.e("TAG", String.valueOf(getAdapterPosition()));

                    Product s = cartList.get(getAdapterPosition());
                    deleteProductValue(s.getProductCode());
                    price.setText(String.valueOf(s.getPrice()*count));


                }
               // ((CartActivity )mContext).refreshCartPreview();

            }
            listenerRef.get().onPositionClicked(getAdapterPosition());
        }
    }

    /* © 2020 All rights reserved. abilash432@gmail.com/@thenextbiggeek® Extending to Water360*/


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
                                Log.e("CARTINTEGRITY", "compromised, removing val");
                            }
                        }
                    }
                }else{
                    for(DataSnapshot ds : dataSnapshot.child("USERCART").child(userID).getChildren()){
                        if(ds.getKey() != shopID){
                            mDatabaseReference.child("USERCART").child(userID).child(ds.getKey()).removeValue();
                            Log.e("CARTINTEGRITY", "compromised, removing val");
                        }
                    }
                }
                mDatabaseReference.removeEventListener(valueEventListener);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("CARTINTEGRITY", "Failed to read value.", error.toException());
            }
        });

    }



}
