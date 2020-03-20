package com.tuyuservices.tuyu;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class ListAdapterTwo extends RecyclerView.Adapter<ListAdapterTwo.MyViewHolder> {

    public Context mContext;
    public List<Service> services;
    private ClickListener listener;
    boolean isCartEmpty = true;
    public List<Service> cartList;
    public String shopUID;

    public ListAdapterTwo(List<Service> mList, ClickListener listener, Context context, String shopUID){
        services  = mList;
        this.listener =listener;
        cartList = new ArrayList<>();
        mContext = context;
        this.shopUID = shopUID;
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
        //TODO add image bitmap
        Service service = services.get(position);
        holder.title.setText(service.getServiceName());
        holder.price.setText(String.valueOf(service.getPrice()));
    }

    @Override
    public int getItemCount() {
        return services.size();
    }




    public class MyViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener{
        public TextView title,price, indicator;
        LinearLayout list_display_one, list_display_two;
        public Button addToCart, plus, minus;
        public WeakReference<ClickListener> listenerRef;

        public MyViewHolder(View view, ClickListener listener) {
            super(view);
            listenerRef = new WeakReference<>(listener);

            list_display_one = (LinearLayout) view.findViewById(R.id.list_display_one);
            list_display_two = (LinearLayout) view.findViewById(R.id.list_display_two);
            title = (TextView) view.findViewById(R.id.list_item_type_two_text);
            price = (TextView) view.findViewById(R.id.list_item_type_two_price);
            addToCart = (Button) view.findViewById(R.id.add_button_list);
            plus = (Button) view.findViewById(R.id.plus_button_list);
            minus = (Button) view.findViewById(R.id.minus_button_list);
            indicator = (TextView) view.findViewById(R.id.indicator_number);
            addToCart.setOnClickListener(this);
            plus.setOnClickListener(this);
            minus.setOnClickListener(this);

        }

        int count = 1;

        @Override
        public void onClick(View v) {
            if (v.getId() == addToCart.getId()) {
                list_display_one.setVisibility(View.GONE);
                list_display_two.setVisibility(View.VISIBLE);
                //one instance of the order is added to the class

                indicator.setText(String.valueOf(count));
                Service s = services.get(getAdapterPosition());
                cartList.add(s);
                //mini cart view is enabled through method in ListActivitySecondary
                isCartEmpty = false;
                if(mContext instanceof ListActivitySecondary){
                    ((ListActivitySecondary)mContext).addedToCart(true);
                }else if(mContext instanceof HomeActivity){
                    ((HomeActivity)mContext).addedToCart();
                }

            }else if(v.getId() == plus.getId()){
                if(count <8){
                    //Maximum of only 8 orders allowed per service
                    count++;
                    indicator.setText(String.valueOf(count));
                    Service s = services.get(getAdapterPosition());
                    cartList.add(s);
                   isCartEmpty = false;
                    if(mContext instanceof ListActivitySecondary){
                        ((ListActivitySecondary)mContext).addedToCart(true);
                    }else if(mContext instanceof HomeActivity){
                        ((HomeActivity)mContext).addedToCart();
                    }


                }

            }else if(v.getId() == minus.getId()){
                if(count>0){
                    count--;
                    indicator.setText(String.valueOf(count));
                    Service s = services.get(getAdapterPosition());
                    Log.e("TAG", String.valueOf(cartList.size()));
                    cartList.remove(count);
                    Log.e("TAG", String.valueOf(cartList.size()));
                    if(cartList.size()==0){
                        isCartEmpty =true;
                    }
                    if(mContext instanceof ListActivitySecondary){
                        ((ListActivitySecondary)mContext).addedToCart(true);
                    }else if(mContext instanceof HomeActivity){
                        ((HomeActivity)mContext).addedToCart();
                    }


                }

            }
            else {
                Toast.makeText(v.getContext(), "ROW PRESSED = " + String.valueOf(getAdapterPosition()), Toast.LENGTH_SHORT).show();
            }

            listenerRef.get().onPositionClicked(getAdapterPosition());
        }
    }

    //when the user presses back or proceeds to cart screen within an activity, the cart has to be maintained
    public List<Service> returnCart(){
        if(isCartEmpty) {
            return  null;
        }else{
            return cartList;

        }
    }


    public interface ClickListener {

        void onPositionClicked(int position);
        void onLongClicked(int position);
    }


}
