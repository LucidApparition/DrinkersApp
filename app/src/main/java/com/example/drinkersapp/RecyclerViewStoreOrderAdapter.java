package com.example.drinkersapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.lang.ref.Reference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * Recyclerview list adapter for items in recyclerview
 * Used for Store Owner orders UI
 */
public class RecyclerViewStoreOrderAdapter extends RecyclerView.Adapter<RecyclerViewStoreOrderAdapter.RecyclerViewStoreOrderViewHolder>  {
    private static final String TAG = "RecyclerViewCartAdapter";

    // constructor variables
    Context context;
    ArrayList<RecyclerViewStoreOrder> orderArrayList;

    // firebase variable
    FirebaseFirestore db;
    String itemName;

    public RecyclerViewStoreOrderAdapter(Context context, ArrayList<RecyclerViewStoreOrder> orderArrayList) {
        this.context = context;
        this.orderArrayList = orderArrayList;
    }


    @NonNull
    @Override
    public RecyclerViewStoreOrderAdapter.RecyclerViewStoreOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.store_order_card_items, parent, false);

        return new RecyclerViewStoreOrderViewHolder(v);


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewStoreOrderViewHolder holder, int position) {
        // get firebase data
        RecyclerViewStoreOrder order = orderArrayList.get(position);
        db = FirebaseFirestore.getInstance();

        // bind database data to card xml item placeholders
        holder.txtCustomerIDPlaceholder.setText(order.uid);

        // when button is clicked, it opens a new page to view items in the order
        holder.btnViewOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StoreOwnerOrdersFragment.startViewOrderActivity(context, order.uid);
            }
        });

        // when the button is clicked, it transfers the order to a new collection and deletes it from the current one
        holder.btnReadyForPickup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                db.collection("StoreOrders").document(order.uid)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        Map<String, Object> itemMap = document.getData();
                                        for (Map.Entry<String, Object> entry : itemMap.entrySet()) {
                                            Map<String, Object> orderItem = (Map<String, Object>) entry.getValue();
                                            Map<String, Object> driverItem = new HashMap<>();
                                            for (Map.Entry<String, Object> e : orderItem.entrySet()) {
                                                if (e.getKey().equals("name")) {
                                                    itemName = (String) e.getValue();
                                                }
                                            }

                                            driverItem.put(itemName, orderItem);
                                            db.collection("DriverOrders")
                                                    .document(order.uid)
                                                    .set(driverItem, SetOptions.merge());
                                        }
                                    } else {
                                        Log.d(TAG, "Didn't add to DriverOrders");
                                    }
                                } else {
                                    Log.d(TAG, "get failed with ", task.getException());
                                }

                                db.collection("StoreOrders")
                                        .document(order.uid)
                                        .delete()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d(TAG,  "Deleted " + order.uid);
                                                } else {

                                                }
                                            }
                                        });
                                }
                        });
                orderArrayList.remove(holder.getAbsoluteAdapterPosition());
                notifyDataSetChanged();
            }
        });
    }


    @Override
    public int getItemCount() {
        return orderArrayList.size();

    }

    public static class RecyclerViewStoreOrderViewHolder extends RecyclerView.ViewHolder{
        TextView txtCustomerIDPlaceholder;
        Button btnViewOrder, btnReadyForPickup;

        public RecyclerViewStoreOrderViewHolder(@NonNull View orderView) {
            super(orderView);
            // declares and initializes the card item placeholders
            txtCustomerIDPlaceholder = orderView.findViewById(R.id.txtCustomerIDPlaceholder);
            btnViewOrder = orderView.findViewById(R.id.btnViewOrder);
            btnReadyForPickup = orderView.findViewById(R.id.btnReadyForPickup);
        }
    }
}
