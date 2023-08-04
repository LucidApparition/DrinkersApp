package com.example.drinkersapp;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;


public class DriverHelpFragment extends Fragment {

    Button logout;
    String number;
    String callTo;
    private Button contactSupport;
    private Button contactCustomer;
    private Button contactStore;
    private static final int PERMISSION_CODE=100;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_driver_help, container, false);

        logout = (Button) v.findViewById(R.id.buttonLogOut);
        contactSupport = (Button) v.findViewById(R.id.btnContactSupport);
        contactCustomer = (Button) v.findViewById(R.id.btnContactCustomer);
        contactStore = (Button) v.findViewById(R.id.btnContactStore);

        logout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            }

        });


        contactSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callTo = "Calling Driver support";
                number = "8882816906";
                makePhoneCall();
            }
        });

        contactCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callTo = "Contacting the Customer";
                number = "4015684717";
                makePhoneCall();
            }
        });

        contactStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callTo = "Calling to the Store";
                number = "6315654856";
                makePhoneCall();
            }
        });

        return v;
    }

    private void makePhoneCall() {
        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(),new String[]{android.Manifest.permission.CALL_PHONE},PERMISSION_CODE);

        } else {
            Toast.makeText(getActivity(), callTo, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + Uri.encode(number))));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                makePhoneCall();
            }else {
                Toast.makeText(getActivity(), "You must allow permision to make a call", Toast.LENGTH_SHORT).show();
            }
        }
    }
}