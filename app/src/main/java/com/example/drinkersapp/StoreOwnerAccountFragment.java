package com.example.drinkersapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import android.content.Intent;
import android.widget.Button;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class StoreOwnerAccountFragment extends Fragment {

    // Implement Firebase tools
    FirebaseAuth auth;
    FirebaseUser user;

    // logout button UI element
    Button logout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_store_owner_account, container, false);

        // make sure user is logged in
        auth = FirebaseAuth.getInstance();

        user = auth.getCurrentUser();
        // check if there is no user
        if (user == null){
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
            //finish();
        }

        // functionality for logout button
        logout = (Button) v.findViewById(R.id.btnLogout);
        logout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                //finish();
            }
        });

        return v;
    }
}