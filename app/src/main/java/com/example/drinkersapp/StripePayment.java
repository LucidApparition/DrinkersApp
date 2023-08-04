package com.example.drinkersapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class StripePayment extends AppCompatActivity {
    private static final String TAG = "Payment";

    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseFirestore db;
    RecyclerViewCartAdapter cartAdapter;

    Button payment;
    String PublishableKey = "pk_test_51MiVuWFN54x3gToeJoHPer1bM0LhRj5JyNbKqykeTrJLCBkUeLyXuTOubL9k3yYkQTvZFUuYJACnQPF2Y1irSggN00Ybqjm1SE";
    String SecretKey = "sk_test_51MiVuWFN54x3gToeDxSDpHj3u5FFPocFsFj71ZjyXgdyXT7OI5xX0Smjf42hLxlfLm4VcqA6rbZ8yNBecGpNcnVy00r9e4kSxp";
    String CustomerId;
    String EphericalKey;
    String ClientSecret;
    PaymentSheet paymentSheet;

    // variables from CustomerCheckout
    Double cartPrice;
    String total;
    StringTokenizer tokens;
    String beforeDecimal;
    String afterDecimal;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stripe_payment);

        payment = findViewById(R.id.payment);

        PaymentConfiguration.init(this,PublishableKey);

        paymentSheet = new PaymentSheet(this,paymentSheetResult -> {

            onPaymentResult(paymentSheetResult);


        });

        // get cartPrice from customer checkout fragment
        Bundle extras = getIntent().getExtras();
        cartPrice = extras.getDouble("cartPrice");
        Log.d(TAG, "payment " + cartPrice);

        // format price to pass to stripe payment --> separate price into a string of everything before the decimal and a string of everything after
        total = String.valueOf(cartPrice);
        tokens = new StringTokenizer(total, ".");
        beforeDecimal = tokens.nextToken();
        afterDecimal = tokens.nextToken();
        Log.d(TAG, "array " + beforeDecimal);
        Log.d(TAG, "array " + afterDecimal);
        if(Integer.valueOf(afterDecimal) < 10) {
            afterDecimal = afterDecimal + "0";
            Log.d(TAG, "add a zero " + afterDecimal);

        }

        auth = FirebaseAuth.getInstance();

        user = auth.getCurrentUser();
        // check if there is no user
        if (user == null){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            //finish();
        }
        db = FirebaseFirestore.getInstance();






        payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                paymentFlow();

            }
        });




        StringRequest request = new StringRequest(Request.Method.POST, "https://api.stripe.com/v1/customers",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject object = new JSONObject(response);

                            CustomerId = object.getString("id");

                            Toast.makeText( StripePayment.this,CustomerId, Toast.LENGTH_SHORT).show();

                            getEmphericalKey();

                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(StripePayment.this,error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

            }
        }){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> header = new HashMap<>();
                header.put("Authorization", "Bearer "+SecretKey);

                return header;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);


    }

    private void paymentFlow() {

        paymentSheet.presentWithPaymentIntent(ClientSecret,new PaymentSheet.Configuration("Drinkers", new PaymentSheet.CustomerConfiguration(
                CustomerId,
                EphericalKey
        )));
    }

    private void onPaymentResult(PaymentSheetResult paymentSheetResult) {

        if (paymentSheetResult instanceof PaymentSheetResult.Completed){
            Toast.makeText(this, "Payment Success", Toast.LENGTH_SHORT).show();


            // Use this, when Jeff Commit viewType code....
            //String type = document.getString("viewType");
            // Use the viewType here
            // viewType(type);


            // transfers user's cart to the OrdersForStore collection, and then deletes the user's unique cart collection
            db.collection(user.getUid())
                    .addSnapshotListener(((value, error) -> {

                        if(error != null){

                            Log.e(TAG,"Firestore error", error);
                            return;
                        }

                        for(DocumentSnapshot doc : value.getDocuments()){

                            RecyclerViewCart cartItem = doc.toObject(RecyclerViewCart.class);
                            Log.d(TAG,  "In cart is "+ cartItem.getName());
                            Map<String, Object> cartData = new HashMap<>();
                            Map<String, Object> cartItems = new HashMap<>();
                            cartItems.put("name", cartItem.getName());
                            cartItems.put("price", cartItem.getPrice());
                            cartItems.put("image", cartItem.getImage());
                            cartData.put(cartItem.getName(), cartItems);


                            db.collection("StoreOrders")
                                    .document(user.getUid())
                                    .set(cartData, SetOptions.merge());



                            db.collection(user.getUid())
                                    .document(cartItem.getName())
                                    .delete()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d(TAG,  "Deleted " + cartItem.getName());
                                            } else {

                                            }
                                        }
                                    });
                        }

                    }));


            // goes back to CustomerUIContainer after successful payment
            Intent intent = new Intent(this, CustomerUIContainer.class);
            startActivity(intent);
        }

    }

    private void getEmphericalKey() {

        StringRequest request = new StringRequest(Request.Method.POST, "https://api.stripe.com/v1/ephemeral_keys",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject object = new JSONObject(response);

                            EphericalKey = object.getString("id");

                            Toast.makeText( StripePayment.this,CustomerId, Toast.LENGTH_SHORT).show();

                            getClientSecret(CustomerId, EphericalKey);

                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(StripePayment.this,error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

            }
        }){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> header = new HashMap<>();
                header.put("Authorization", "Bearer "+SecretKey);
                header.put("Stripe-Version", "2022-11-15");
                return header;

            }

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("customer",CustomerId);

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);

    }

    private void getClientSecret(String customerId, String ephericalKey) {


        StringRequest request = new StringRequest(Request.Method.POST, "https://api.stripe.com/v1/payment_intents",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject object = new JSONObject(response);

                            ClientSecret = object.getString("client_secret");

                            Toast.makeText( StripePayment.this,ClientSecret, Toast.LENGTH_SHORT).show();


                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(StripePayment.this,error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

            }
        }){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> header = new HashMap<>();
                header.put("Authorization", "Bearer "+SecretKey);
                return header;

            }

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("customer", CustomerId);
                params.put("amount", beforeDecimal+afterDecimal);
                params.put("currency","USD");
                params.put("automatic_payment_methods[enabled]", "true");


                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

}
