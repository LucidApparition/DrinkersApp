package com.example.drinkersapp;

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
import android.widget.Toast;

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
import java.util.List;
import java.util.Map;
/*
* Customer UI page for user to view their cart, choose a tip, and checkout
*/
public class CustomerCheckoutFragment extends Fragment {

    private static final String TAG = "Checkout";
    // Implement Firebase tools
    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseStorage storage;
    FirebaseFirestore db;

    // recyclerview variables
    RecyclerView recyclerView;
    ArrayList<RecyclerViewCart> cartArrayList;
    RecyclerViewCartAdapter cartAdapter;


    ProgressDialog progressDialog;

    double cartPrice = 0;

    // UI text and buttons
    TextView txtTotalCalculated;
    TextView txtTipCalculated;

    Button checkout;
    Button btn5Percent;
    Integer tip = 15;
    Button btn10Percent;
    Button btn15Percent;
    Button btn20Percent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View display = inflater.inflate(R.layout.fragment_customer_checkout, container, false);

        // bind variables to UI elements
        txtTotalCalculated = display.findViewById(R.id.txtTotalCalculated);
        txtTipCalculated = display.findViewById(R.id.txtTipCalculated);

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
        recyclerView = display.findViewById(R.id.rcyItemsInCart);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // get database instances
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();


        // create list for recyclerview and bind it
        cartArrayList = new ArrayList<RecyclerViewCart>();
        cartAdapter = new RecyclerViewCartAdapter(getActivity(), cartArrayList);
        recyclerView.setAdapter(cartAdapter);

        // bind variables to UI elements
        btn5Percent = display.findViewById(R.id.btn5Percent);
        btn10Percent = display.findViewById(R.id.btn10Percent);
        btn15Percent = display.findViewById(R.id.btn15Percent);
        btn20Percent = display.findViewById(R.id.btn20Percent);

        // functionality for tip buttons
        btn5Percent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tip = 5;
                cartPrice = 0;
                calculateCartPrice();
            }
        });

        btn10Percent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tip = 10;
                cartPrice = 0;
                calculateCartPrice();
            }
        });

        btn15Percent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tip = 15;
                cartPrice = 0;
                calculateCartPrice();
            }
        });

        btn20Percent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tip = 20;
                cartPrice = 0;
                calculateCartPrice();
            }
        });

        // get data from database for the recyclerview
        EventChangeListener();


        // functionality for checkout button
        checkout = (Button) display.findViewById(R.id.btnCheckout);

        checkout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                // add if condition to make sure cartPrice isn't zero and make toast message
                if(cartPrice != 0.0) {
                    Intent intent = new Intent(getActivity(), StripePayment.class);
                    intent.putExtra("cartPrice", cartPrice);
                    startActivity(intent);
                } else {
                    Toast.makeText(getActivity(), "You must add an item to checkout", Toast.LENGTH_SHORT).show();
                }

            }
        });

        return display;
    }

    private void EventChangeListener() {
        /*
        * Get data from database and add it to recyclerview's list
        */
        db.collection(user.getUid()).orderBy("name", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {

                    if(error != null){

                        if(progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }

                        Log.e(TAG,"Firestore error", error);
                        return;
                    }

                    for(DocumentChange dc : value.getDocumentChanges()){

                        if(dc.getType() == DocumentChange.Type.ADDED) {

                            cartArrayList.add(dc.getDocument().toObject(RecyclerViewCart.class));

                        }
                        cartAdapter.notifyDataSetChanged();
                        if(progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    }
                    // after all items added to list, calculate the cartPrice
                    cartPrice = 0;
                    calculateCartPrice();
                });
        if(progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
    private void calculateCartPrice() {
        /*
        * Calculate cart price based on each item's price, tip, and tax
        */
        db.collection(user.getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();
                        for(DocumentSnapshot snapshot : snapshotList) {
                            cartPrice +=snapshot.getLong("price");
                            Log.d(TAG, "checkout " + cartPrice);


                        }

                        // add tax
                        cartPrice = (0.03*cartPrice) + cartPrice;

                        // add tip
                        switch(tip){
                            case 5:
                                cartPrice = (0.05*cartPrice) + cartPrice;
                                Log.d(TAG, "checkout 5 " + cartPrice);
                                txtTipCalculated.setText(tip + "%");
                                txtTotalCalculated.setText(String.format("%.2f", cartPrice));
                                break;
                            case 10:
                                cartPrice = (0.1*cartPrice) + cartPrice;
                                Log.d(TAG, "checkout 10 " + cartPrice);
                                txtTipCalculated.setText(tip + "%");
                                txtTotalCalculated.setText(String.format("%.2f", cartPrice));
                                break;
                            case 15:
                                cartPrice = (0.15*cartPrice) + cartPrice;
                                Log.d(TAG, "checkout 15 " + cartPrice);
                                txtTipCalculated.setText(tip + "%");
                                txtTotalCalculated.setText(String.format("%.2f", cartPrice));
                                break;
                            case 20:
                                cartPrice = (0.2*cartPrice) + cartPrice;
                                Log.d(TAG, "checkout 20 " + cartPrice);
                                txtTipCalculated.setText(tip + "%");
                                txtTotalCalculated.setText(String.format("%.2f", cartPrice));
                                break;
                            default:
                                cartPrice = (0.15*cartPrice) + cartPrice;
                                Log.d(TAG, "checkout 15 " + cartPrice);
                                txtTipCalculated.setText(tip + "%");
                                txtTotalCalculated.setText(String.format("%.2f", cartPrice));
                                break;
                        }

                        // round the price to 2 decimal places
                        BigDecimal bigDecimal = new BigDecimal(cartPrice).setScale(2, BigDecimal.ROUND_UP);
                        cartPrice = bigDecimal.doubleValue();
                        Log.d(TAG, "payment rounded" + cartPrice);
                    }
                });
    }
}