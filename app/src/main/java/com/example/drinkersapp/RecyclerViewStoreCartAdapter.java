package com.example.drinkersapp;

import android.content.Context;
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
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.lang.ref.Reference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
/*
 * Recyclerview list adapter for items in recyclerview
 * Used for Store Owner view order UI
 */

public class RecyclerViewStoreCartAdapter extends RecyclerView.Adapter<RecyclerViewStoreCartAdapter.RecyclerViewStoreCartViewHolder> {
    private static final String TAG = "RecyclerViewCartAdapter";

    // constructor variables
    Context context;
    ArrayList<RecyclerViewCart> cartArrayList;

    // firebase variables
    FirebaseFirestore db;
    FirebaseAuth auth;
    FirebaseUser user;


    public RecyclerViewStoreCartAdapter(Context context, ArrayList<RecyclerViewCart> cartArrayList) {
        this.context = context;
        this.cartArrayList = cartArrayList;
    }

    @NonNull
    @Override
    public RecyclerViewStoreCartAdapter.RecyclerViewStoreCartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.store_view_order_card_items, parent, false);

        return new RecyclerViewStoreCartViewHolder(v);


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewStoreCartAdapter.RecyclerViewStoreCartViewHolder holder, int position) {
        // get firebase data
        RecyclerViewCart cart = cartArrayList.get(position);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        String uid = user.getUid();

        // bind database data to card xml item placeholders
        holder.txtProductPlaceholder.setText(cart.name);
        holder.txtPricePlaceholder.setText(String.valueOf(cart.price));

        String imageUri = null;
        imageUri = cart.getImage();
        Picasso.get().load(imageUri).into(holder.imgProductImage);

    }

    @Override
    public int getItemCount() {
        return cartArrayList.size();
    }

    public static class RecyclerViewStoreCartViewHolder extends RecyclerView.ViewHolder{
        // declares and initializes the card item placeholders
        TextView txtProductPlaceholder, txtPricePlaceholder;
        ImageView imgProductImage;
        Button btnRemoveFromCart;

        public RecyclerViewStoreCartViewHolder(@NonNull View itemView) {
            super(itemView);
            // declares and initializes the card item placeholders
            txtProductPlaceholder = itemView.findViewById(R.id.txtProductPlaceholder);
            txtPricePlaceholder = itemView.findViewById(R.id.txtPricePlaceholder);
            imgProductImage = itemView.findViewById(R.id.imgProductImage);
            btnRemoveFromCart = itemView.findViewById(R.id.btnRemoveFromCart);
        }
    }
}

