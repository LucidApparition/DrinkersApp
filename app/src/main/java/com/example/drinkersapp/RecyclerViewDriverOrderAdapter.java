package com.example.drinkersapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/*
 * Recyclerview list adapter for items in recyclerview
 * Used for Driver accept/decline UI
 */
public class RecyclerViewDriverOrderAdapter extends RecyclerView.Adapter<RecyclerViewDriverOrderAdapter.RecyclerViewDriverOrderViewHolder>  {
    private static final String TAG = "RecyclerViewDriverAdapter";

    // constructor variables
    Context context;
    ArrayList<RecyclerViewStoreOrder> orderArrayList;

    // firebase variable
    FirebaseFirestore db;

    public RecyclerViewDriverOrderAdapter(Context context, ArrayList<RecyclerViewStoreOrder> orderArrayList) {
        this.context = context;
        this.orderArrayList = orderArrayList;
    }


    @NonNull
    @Override
    public RecyclerViewDriverOrderAdapter.RecyclerViewDriverOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.driver_order_card_items, parent, false);

        return new RecyclerViewDriverOrderViewHolder(v);


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewDriverOrderViewHolder holder, int position) {
        // get firebase data
        RecyclerViewStoreOrder order = orderArrayList.get(position);
        db = FirebaseFirestore.getInstance();

        // bind database data to card xml item placeholders
        holder.txtCustomerIDPlaceholder.setText(order.uid);

        // deletes order from database collection
        holder.btnMarkAsDelivered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                db.collection("DriverOrders")
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
                orderArrayList.remove(holder.getAbsoluteAdapterPosition());
                notifyDataSetChanged();
            }
        });
    }


    @Override
    public int getItemCount() {
        return orderArrayList.size();

    }

    public static class RecyclerViewDriverOrderViewHolder extends RecyclerView.ViewHolder{
        TextView txtCustomerIDPlaceholder;
        Button btnMarkAsDelivered;

        public RecyclerViewDriverOrderViewHolder(@NonNull View orderView) {
            super(orderView);
            // declares and initializes the card item placeholders
            txtCustomerIDPlaceholder = orderView.findViewById(R.id.txtCustomerIDPlaceholder);
            btnMarkAsDelivered = orderView.findViewById(R.id.btnMarkAsDelivered);

        }
    }

}
