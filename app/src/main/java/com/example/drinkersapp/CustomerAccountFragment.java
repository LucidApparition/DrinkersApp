package com.example.drinkersapp;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.google.firebase.auth.FirebaseAuth;
/*
* Customer UI page for user to interact with settings and logout
*/
public class CustomerAccountFragment extends Fragment {
    // UI button
    Button logout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View display = inflater.inflate(R.layout.fragment_customer_account, container, false);

        // functionality for logout button
        logout = (Button) display.findViewById(R.id.buttonLogOut);
        logout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            }
        });
        return display;
    }
}