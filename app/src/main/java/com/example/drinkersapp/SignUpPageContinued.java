package com.example.drinkersapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpPageContinued extends AppCompatActivity implements View.OnClickListener {
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    String type;
    Intent intent;
    // Logger for debugging
    private static final String TAG = "MyActivity";

    // switch statement for actions to take based on what button the user presses
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnCustomer:
                //Toast.makeText(this, "Customer button clicked", Toast.LENGTH_SHORT).show();
                type = "customer";
                Log.v(TAG, "Customer Button Clicked");
                createUser(type);
                intent = new Intent(getApplicationContext(), CustomerUIContainer.class);
                startActivity(intent);
                finish();
                break;
            case R.id.btnDriver:
                //Toast.makeText(this, "Driver button clicked", Toast.LENGTH_SHORT).show();
                type = "driver";
                Log.v(TAG, "Driver Button Clicked");
                createUser(type);
                intent = new Intent(getApplicationContext(), DriverUIContainer.class);
                startActivity(intent);
                finish();
                break;
            case R.id.btnStoreOwner:
                //Toast.makeText(this, "Store owner button clicked", Toast.LENGTH_SHORT).show();
                Log.v(TAG, "Store Owner Button Clicked");
                type = "store owner";
                createUser(type);
                intent = new Intent(getApplicationContext(), StoreOwnerUIContainer.class);
                startActivity(intent);
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_page_continued);
        // initializes the buttons and tells them to look to the switch statement (this class) for what to do once clicked
        Button btnCustomer = findViewById(R.id.btnCustomer);
        btnCustomer.setOnClickListener(this);

        Button btnDriver = findViewById(R.id.btnDriver);
        btnDriver.setOnClickListener(this);

        Button btnStoreOwner = findViewById(R.id.btnStoreOwner);
        btnStoreOwner.setOnClickListener(this);

    }
    public void createUser(String profileType) {
        // Creating User Profile in Firebase
        String providerId = "";
        String uid = "";
        String email = "";
        Log.v(TAG, "Getting User");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null)
        {
            Log.v(TAG, "User is Signed in");
            for (UserInfo userInfo : user.getProviderData()) {
                providerId = userInfo.getProviderId();
            }
            uid = user.getUid();
            email = user.getEmail();
            Log.v(TAG, "Provider ID = "+providerId+" UID = "+uid+" Email = "+email);
        }
        else
            Log.v(TAG, "User is NOT Signed in");


        switch(profileType) {
            case "customer":
                Log.v(TAG, "Customer Data Being Created");

                Map<String, Object> customerData = new HashMap<>();
                customerData.put("uid", uid);
                customerData.put("email", email);
                customerData.put("providerID", providerId);
                customerData.put("viewType", "Customer View");
                db.collection("Users").document(uid)
                        .set(customerData);
                break;
            case "driver":
                Log.v(TAG, "Driver Data Being Created");

                Map<String, Object> driverData = new HashMap<>();
                driverData.put("uid", uid);
                driverData.put("email", email);
                driverData.put("providerID", providerId);
                driverData.put("viewType", "Driver View");
                db.collection("Users").document(uid)
                        .set(driverData);
                break;
            case "store owner":
                Log.v(TAG, "Store Owner Data Being Created");

                Map<String, Object> storeData = new HashMap<>();
                storeData.put("uid", uid);
                storeData.put("email", email);
                storeData.put("providerID", providerId);
                storeData.put("viewType", "Store Owner View");
                db.collection("Users").document(uid)
                        .set(storeData);
                break;
            default:
                break;
        }
    }
}