package com.tuyuservices.tuyumain;

import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/* © 2020 All rights reserved. abilash432@gmail.com/@thenextbiggeek® Extending to Water360*/

public class ListAdapterManagement extends RecyclerView.Adapter<ListAdapterManagement.MyViewHolder>  {

    private final String shopID;
    private final Context mContext;
    private DatabaseReference mDatabaseReference;

    public interface ClickListener {

        void onPositionClicked(int position);
        void onLongClicked(int position);
    }


    private ClickListener listener;
    boolean isCartEmpty = true;
    public ArrayList<Partner> partnerList;

    public ListAdapterManagement(ArrayList<Partner> mList, ClickListener listener, Context context, String shopID){
        partnerList = mList;
        this.listener =listener;
        this.shopID = shopID;
        mContext = context;
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();



    }


    @NonNull
    @Override
    public ListAdapterManagement.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_manage_partner, parent, false);
        return new ListAdapterManagement.MyViewHolder(itemView, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ListAdapterManagement.MyViewHolder holder, int position) {
        Partner partner = partnerList.get(position);
        holder.nameTextView.setText(partner.getName());
        holder.passTextView.setText(partner.getPass());
        holder.numberTextView.setText(partner.getNumber());


    }

    @Override
    public int getItemCount() {
        return partnerList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener{
        public TextView nameTextView, passTextView, numberTextView;
        public Button  delete, callPartner;
        public WeakReference<ClickListener> listenerRef;

        public MyViewHolder(View view, ClickListener listener) {
            super(view);
            listenerRef = new WeakReference<>(listener);
            delete = (Button) view.findViewById(R.id.list_item_manage_partner_delete);
            callPartner = (Button) view.findViewById(R.id.list_item_manage_partner_call_partner);
            numberTextView = (TextView) view.findViewById(R.id.list_item_manage_partner_number);
            nameTextView = (TextView) view.findViewById(R.id.list_item_manage_partner_name);
            passTextView = (TextView) view.findViewById(R.id.list_item_manage_partner_pass);
            delete.setOnClickListener(this);
            callPartner.setOnClickListener(this);

        }

        //count is used for indication purposes only
        int count = 1;


        @Override
        public void onClick(View v) {


            if (v.getId() == delete.getId()) {
                //one instance of the order is added to the class

                Log.e("DELETE", String.valueOf(getAdapterPosition()));
                Partner s = partnerList.get(getAdapterPosition());
                //show an alert dialog saying the partner entry shall be deleted
                showAlertDialog(s, mDatabaseReference);
            }else if(v.getId() == callPartner.getId()){
                Log.e("CALL PARTNER", String.valueOf(getAdapterPosition()));
                Partner s = partnerList.get(getAdapterPosition());
                String number = s.getNumber();
                Log.e("Callbutton", "called" );
                ((ManagePartnerActivity)mContext).dialUserIntent(number);


            }

            listenerRef.get().onPositionClicked(getAdapterPosition());
        }
    }

    private void showAlertDialog(final Partner s, final DatabaseReference mDatabaseReference) {
        AlertDialog.Builder alert = new AlertDialog.Builder(mContext);

        alert.setMessage("Action is irrreversible. Type the partner name and pass and press delete");
        alert.setTitle("Delete Partner?");

        LinearLayout layout = new LinearLayout(mContext);
        layout.setOrientation(LinearLayout.VERTICAL);

        // Add a TextView here for the "Title" label, as noted in the comments
        final EditText partnerIDEdit = new EditText(mContext);
        partnerIDEdit.setHint("PARTNERID");
        layout.addView(partnerIDEdit); // Notice this is an add method

        // Add another TextView here for the "Description" label
        final EditText partnerPassEdit = new EditText(mContext);
        partnerPassEdit.setHint("Passkey");
        layout.addView(partnerPassEdit); // Another add method

        alert.setView(layout); // Again this is a set method, not add


        alert.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //What ever you want to do with the value
                Editable name = partnerIDEdit.getText();
                Editable pass = partnerPassEdit.getText();
                if(name.toString().equals(s.getName()) && pass.toString().equals(s.getPass()) ){
                    Log.e("NAMEX", s.getName());
                    mDatabaseReference.child("PARTNER").child(shopID).child(s.getName()).removeValue();
                    ((ManagePartnerActivity)mContext).retreivePartnerData();


                }

            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // what ever you want to do with No option.

            }
        });

        alert.show();

    }
    /* © 2020 All rights reserved. abilash432@gmail.com/@thenextbiggeek® Extending to Water360*/

}
