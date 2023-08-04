package com.example.drinkersapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Map;

public class StoreOwnerViewOrderPage extends AppCompatActivity {

    private static final String TAG = "ViewOrder";
    // Implement Firebase tools
    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseFirestore db;
    FirebaseStorage storage;

    // recyclerview variables
    RecyclerView recyclerView;
    ArrayList<RecyclerViewCart> cartArrayList;
    RecyclerViewCart cart;
    RecyclerViewStoreCartAdapter cartAdapter;


    ProgressDialog progressDialog;

    // UI variables
    String uid;
    TextView txtCustomerIDPlaceholder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_owner_view_order_page);

        // get data from StoreOwnerViewOrderAdapter
        Bundle extras = getIntent().getExtras();
        uid = extras.getString("uid");

        // show progress bar while getting data from database
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching data");
        progressDialog.show();

        // make sure user is logged in
        auth = FirebaseAuth.getInstance();

        user = auth.getCurrentUser();
        // check if there is no user
        if (user == null){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            //finish();
        }

        // bind UI placeholder and set the text
        txtCustomerIDPlaceholder = findViewById(R.id.txtCustomerIDPlaceholder);
        txtCustomerIDPlaceholder.setText(uid);

        // bind recyclerview variables to UI elements
        recyclerView = findViewById(R.id.rcyItemsOrdered);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // get database instances
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        // create list for recyclerview and bind it
        cartArrayList = new ArrayList<RecyclerViewCart>();
        cartAdapter = new RecyclerViewStoreCartAdapter(this, cartArrayList);
        recyclerView.setAdapter(cartAdapter);

        // get data from database for the recyclerview
        EventChangeListener();
    }

    private void EventChangeListener() {
        /*
         * Get data from database and add it to recyclerview's list
         */
        db.collection("StoreOrders").document(uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Object> itemMap = document.getData();
                        for (Map.Entry<String, Object> entry : itemMap.entrySet()) {
                            Map<String, Object> test = (Map<String, Object>) entry.getValue();
                            Log.d(TAG, "this is " + test);
                            cart = new RecyclerViewCart();
                            for (Map.Entry<String, Object> e : test.entrySet()) {
                                if (e.getKey().equals("name")) {
                                    cart.setName((String) e.getValue());
                                }
                                if (e.getKey().equals("price")) {
                                    cart.setPrice((Long) e.getValue());
                                }
                                if (e.getKey().equals("image")) {
                                    cart.setImage((String) e.getValue());
                                }
                            }
                            cartArrayList.add(cart);
                            cartAdapter.notifyDataSetChanged();
                        }
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        if(progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}