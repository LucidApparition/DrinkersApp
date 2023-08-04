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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.lang.ref.Reference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
/*
 * Recyclerview list adapter for items in recyclerview
 * Used for Customer home UI
 */
public class RecyclerViewItemsAdapter extends RecyclerView.Adapter<RecyclerViewItemsAdapter.RecyclerViewItemsViewHolder> {
    private static final String TAG = "RecylerViewItemsAdapter";
    // constructor variables
    Context context;
    ArrayList<RecyclerViewItems> itemsArrayList;

    // firebase variables
    FirebaseFirestore db;
    FirebaseAuth auth;
    FirebaseUser user;


    public RecyclerViewItemsAdapter(Context context, ArrayList<RecyclerViewItems> itemsArrayList) {
        this.context = context;
        this.itemsArrayList = itemsArrayList;
    }

    @NonNull
    @Override
    public RecyclerViewItemsAdapter.RecyclerViewItemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.card_items, parent, false);

        return new RecyclerViewItemsViewHolder(v);


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewItemsAdapter.RecyclerViewItemsViewHolder holder, int position) {
        // get firebase data
        RecyclerViewItems item = itemsArrayList.get(position);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        String uid = user.getUid();

        // bind database data to card xml item placeholders
        holder.txtProductPlaceholder.setText(item.name);
        holder.txtAbvPlaceholder.setText(item.abv);
        holder.txtSizePlaceholder.setText(item.size);
        holder.txtPricePlaceholder.setText(String.valueOf(item.price));

        String imageUri = null;
        imageUri = item.getImage();
        Picasso.get().load(imageUri).into(holder.imgProductImage);


        // adds items to the user's unique cart which is created based on their user id
        holder.btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Map<String, Object> cartItem = new HashMap<>();
                cartItem.put("name", item.name);
                cartItem.put("price", item.price);
                cartItem.put("image", item.image);

                DocumentReference docIdRef = db.collection(uid).document(item.name);
                docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Toast.makeText(context, "Item is already in your cart", Toast.LENGTH_SHORT).show();
                            } else {
                                db.collection(uid)
                                        .document(item.name)
                                        .set(cartItem);

                                db.collection("TestDelete")
                                        .document(item.name)
                                        .set(cartItem);
                                Toast.makeText(context, "Item added to cart", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.d(TAG, "Failed with: ", task.getException());
                        }
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemsArrayList.size();
    }


    public static class RecyclerViewItemsViewHolder extends RecyclerView.ViewHolder{

        TextView txtProductPlaceholder, txtPricePlaceholder, txtAbvPlaceholder, txtSizePlaceholder;
        ImageView imgProductImage;
        Button btnAddToCart;

        public RecyclerViewItemsViewHolder(@NonNull View itemView) {
            super(itemView);
            // declares and initializes the card item placeholders
            txtProductPlaceholder = itemView.findViewById(R.id.txtProductPlaceholder);
            txtPricePlaceholder = itemView.findViewById(R.id.txtPricePlaceholder);
            imgProductImage = itemView.findViewById(R.id.imgProductImage);
            txtAbvPlaceholder = itemView.findViewById(R.id.txtAbvPlaceholder);
            txtSizePlaceholder = itemView.findViewById(R.id.txtSizePlaceholder);
            btnAddToCart = itemView.findViewById(R.id.btnAddToCart);
        }
    }
}
