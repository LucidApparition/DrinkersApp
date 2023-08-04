package com.example.drinkersapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import org.checkerframework.checker.units.qual.A;

import java.lang.ref.Reference;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class StoreOwnerOrdersFragment extends Fragment {
    private static final String TAG = "Store";

    // Implement Firebase tools
    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseFirestore db;
    FirebaseStorage storage;

    // recyclerview variables
    RecyclerView recyclerView;
    ArrayList<RecyclerViewStoreOrder> orderArrayList;
    RecyclerViewStoreOrder testOrder;
    RecyclerViewStoreOrderAdapter orderAdapter;

    ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View display = inflater.inflate(R.layout.fragment_store_owner_orders, container, false);

        // show progress bar while getting data from database
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching data");
        progressDialog.show();

        // make sure user is logged in
        auth = FirebaseAuth.getInstance();

        user = auth.getCurrentUser();
        // check if there is no user
        if (user == null){
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
            //finish();
        }

        // bind recyclerview variables to UI elements
        recyclerView = display.findViewById(R.id.rcyCurrentUnfulfilledOrders);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // get database instances
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        // create list for recyclerview and bind it
        orderArrayList = new ArrayList<RecyclerViewStoreOrder>();
        orderAdapter = new RecyclerViewStoreOrderAdapter(getActivity(), orderArrayList);
        recyclerView.setAdapter(orderAdapter);

        // get data from database for the recyclerview
        EventChangeListener();

        return display;
    }

    private void EventChangeListener() {
        /*
         * Get data from database and add it to recyclerview's list
         */
        db.collection("StoreOrders")
                .get()
                .addOnCompleteListener( task -> {
                   if (task.isSuccessful()) {
                       for(DocumentSnapshot doc : task.getResult()) {
                           String id = doc.getId();
                           testOrder = new RecyclerViewStoreOrder();
                           testOrder.setUid(id);
                           orderArrayList.add(testOrder);
                           orderAdapter.notifyDataSetChanged();
                       }

                       if(progressDialog.isShowing()) {
                           progressDialog.dismiss();
                       }
                   }
                   else
                       Log.d(TAG, "Error getting documents: ", task.getException());
                });
    }

    public static void startViewOrderActivity(Context context, String uid) {
        /*
         * helper method for a recyclerview item to start the ViewOrderPage when clicked
         * User in RecyclerViewStoreOrderAdapter
         */
        Intent intent = new Intent(context, StoreOwnerViewOrderPage.class);
        intent.putExtra("uid", uid);
        context.startActivity(intent);
    }
}

