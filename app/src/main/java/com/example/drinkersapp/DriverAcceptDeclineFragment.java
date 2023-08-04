package com.example.drinkersapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
/*
 * Driver UI page for driver to view orders, accept them to start Google Maps, and mark them as delivered
 */
public class DriverAcceptDeclineFragment extends Fragment {

    private static final String TAG = "Driver Accept Decline";

    // Implement Firebase tools
    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseFirestore db;
    FirebaseStorage storage;

    // recyclerview variables
    RecyclerView recyclerView;
    ArrayList<RecyclerViewStoreOrder> orderArrayList;
    RecyclerViewStoreOrder testOrder;
    RecyclerViewDriverOrderAdapter orderAdapter;

    ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View display = inflater.inflate(R.layout.fragment_driver_accept_decline, container, false);

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
        recyclerView = display.findViewById(R.id.rcyDriverOrders);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // get database instances
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        // create list for recyclerview and bind it
        orderArrayList = new ArrayList<RecyclerViewStoreOrder>();
        orderAdapter = new RecyclerViewDriverOrderAdapter(getActivity(), orderArrayList);
        recyclerView.setAdapter(orderAdapter);

        // get data from database for the recyclerview
        EventChangeListener();

        return display;
    }

    private void EventChangeListener() {
        /*
         * Get data from database and add it to recyclerview's list
         */
        db.collection("DriverOrders")
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


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView destinationLocation = view.findViewById(R.id.DestinationLocation);
        TextView sourceLocation = view.findViewById(R.id.SourceLocation);
        Button button = view.findViewById(R.id.AcceptButton);
        
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Start Google Map
                String source = sourceLocation.getText().toString();
                String destination = destinationLocation.getText().toString();
                
                if (source.equals("") && destination.equals("")){
                    Toast.makeText(getActivity(), "You do not have an order Destination yet", Toast.LENGTH_SHORT).show();

                }
                else {
                    Uri uri = Uri.parse("http://www.google.com/map/dir/" + source + "/" + destination);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    intent.setData(Uri.parse("http://www.google.com/maps/dir/" + source + "/" + destination));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }

            }
        });



    }
}