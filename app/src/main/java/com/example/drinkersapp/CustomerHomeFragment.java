package com.example.drinkersapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
/*
 * Customer UI page for user to view all products or search for them by name, and to add items to their cart
 */
public class CustomerHomeFragment extends Fragment {
    private static final String TAG = "CustomerFragment";
    // Implement Firebase tools
    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseFirestore db;
    FirebaseStorage storage;

    // recyclerview variables
    RecyclerView recyclerView;
    ArrayList<RecyclerViewItems> itemsArrayList;
    RecyclerViewItemsAdapter itemsAdapter;

    ProgressDialog progressDialog;

    // search variables
    SearchView searchBar;
    Query searchQuery;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_customer_home, container, false);

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
        recyclerView = v.findViewById(R.id.rcyItems);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // get database instances
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        // functionality for search bar
        searchBar = v.findViewById(R.id.searchView);

        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {return false;}
            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty())
                    // If search bar is empty, show all items
                    searchQuery = db.collection("TestLiquor")
                            .orderBy("name", Query.Direction.ASCENDING);
                else {
                    searchQuery = db.collection("TestLiquor")
                            .whereGreaterThanOrEqualTo("name", newText)
                            .whereLessThanOrEqualTo("name", newText + "\uf8ff");

            }
                // Refresh the RecyclerView with the filtered data
                searchQuery
                        .get()
                        .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        itemsArrayList.clear();
                        for (DocumentSnapshot document : task.getResult()) {
                            RecyclerViewItems item = document.toObject(RecyclerViewItems.class);
                            itemsArrayList.add(item);

                        }
                        itemsAdapter.notifyDataSetChanged();
                    }
                    else
                        Log.e(TAG, "Error getting documents: ", task.getException());
                })
                        .addOnFailureListener(task -> Log.w(TAG, "Failure"));
                return false;
            }
        });

        // create list for recyclerview and bind it
        itemsArrayList = new ArrayList<>();
        itemsAdapter = new RecyclerViewItemsAdapter(getActivity(), itemsArrayList);
        recyclerView.setAdapter(itemsAdapter);

        // get data from database for the recyclerview
        EventChangeListener();

        return v;
    }

    private void EventChangeListener() {
        /*
         * Get data from database and add it to recyclerview's list
         */
        // Use search query instead of regular query
        searchQuery = db.collection("TestLiquor")
                .orderBy("name", Query.Direction.ASCENDING);

        searchQuery.addSnapshotListener((value, error) -> {
            if(error != null){
                if(progressDialog.isShowing())
                    progressDialog.dismiss();

                Log.e(TAG,"Firestore error", error);
                return;
            }
            itemsArrayList.clear();
            assert value != null;
            for(DocumentSnapshot document : value.getDocuments()) {
                RecyclerViewItems item = document.toObject(RecyclerViewItems.class);
                itemsArrayList.add(item);
            }
            itemsAdapter.notifyDataSetChanged();

            if (progressDialog.isShowing())
                progressDialog.dismiss();
        });
    }
}