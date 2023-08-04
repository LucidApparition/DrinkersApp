package com.example.drinkersapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";
    // Google authentication for sign in
    GoogleSignInClient gsc;
    private static final int G_SIGN_IN = 9001;

    // Facebook variables
    CallbackManager callbackManager;
    private static final int F_SIGN_IN = 201;

    // Firebase authentication for sign in
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    // instantiating the UI elements for use in different classes
    private EditText txtEmail, txtPassword;

    private Intent intent;
    private LoginButton loginButton;

    // switch statement for actions to take based on what button the user presses
    @Override
    public void onClick(View v) {
        String email, password;
        email = String.valueOf(txtEmail.getText());
        password = String.valueOf((txtPassword.getText()));

        switch (v.getId()) {
            case R.id.btnSignIn:
                // Sign in with email
                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    assert user != null;
                                    String uid = user.getUid();
                                    db.collection("Users").document(uid)
                                            .get()
                                            .addOnCompleteListener(task1 -> {
                                                if (task1.isSuccessful()) {
                                                    DocumentSnapshot document = task1.getResult();
                                                    if (document.exists()) {
                                                        String type = document.getString("viewType");
                                                        // Use the viewType here
                                                        assert type != null;
                                                        viewType(type);
                                                    } else {
                                                        Log.d(TAG, "No such document");
                                                    }
                                                } else {
                                                    Log.d(TAG, "get failed with ", task1.getException());
                                                }
                                            });

                                }
                            });
                }
                else {
                    // Check if email is empty
                    if (TextUtils.isEmpty(email)) {
                        Toast.makeText(MainActivity.this, "Email field empty", Toast.LENGTH_SHORT).show();
                    }
                    // Check if password is empty
                    else if (TextUtils.isEmpty(password)) {
                        Toast.makeText(MainActivity.this, "Password field empty", Toast.LENGTH_SHORT).show();
                    }

                }
                break;
            case R.id.btnGmailSignIn:
                Toast.makeText(this, "Gmail sign in button clicked", Toast.LENGTH_SHORT).show();
                googleSignIn();
                break;
            case R.id.btnFacebookSignIn:
                Toast.makeText(this, "Facebook sign in button clicked", Toast.LENGTH_SHORT).show();
                facebookSignIn();
                break;
            case R.id.btnTwitterSignIn:
                Toast.makeText(this, "Twitter sign in button clicked", Toast.LENGTH_SHORT).show();
                //TODO add functionality to sign in via Twitter
                break;
            case R.id.txtForgotPassword:
                Toast.makeText(this, "Forgot password button clicked", Toast.LENGTH_SHORT).show();
                intent = new Intent(this, ResetPassword.class);
                startActivity(intent);
                break;
            case R.id.txtSignUp:
                // changes to Sign Up page
                intent = new Intent(this, SignUpPage.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
    public void viewType(String vType) {
        Log.d(TAG, "vType = " + vType);
        switch(vType) {
            case "Customer View":
                intent = new Intent(getApplicationContext(), CustomerUIContainer.class);
                Log.d(TAG, "Customer View Switch" + intent);
                startActivity(intent);
                finish();
                break;
            case "Driver View":
                intent = new Intent(getApplicationContext(), DriverUIContainer.class);
                Log.d(TAG, "Driver View Switch" + intent);
                startActivity(intent);
                finish();
                break;
            case "Store Owner View":
                intent = new Intent(getApplicationContext(), StoreOwnerUIContainer.class);
                Log.d(TAG, "Store Owner View Switch" + intent);
                startActivity(intent);
                finish();
                break;
            default:
                break;
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Google sign-in
        if (requestCode == G_SIGN_IN) {
            try {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                Toast.makeText(MainActivity.this, "Google sign in failed", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            String uid = user.getUid();
                            db.collection("Users").document(uid)
                                    .get()
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            DocumentSnapshot document = task1.getResult();
                                            if (document.exists()) {
                                                String type = document.getString("viewType");
                                                // Use the viewType here
                                                assert type != null;
                                                viewType(type);
                                            } else {
                                                Log.d(TAG, "No such document");
                                                // takes user to the continued Sign Up page
                                                intent = new Intent(MainActivity.this, SignUpPageContinued.class);
                                                startActivity(intent);
                                            }
                                        } else {
                                            Log.d(TAG, "get failed with ", task1.getException());
                                        }
                                    });
                        }

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(MainActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void googleSignIn() {
        Intent signInIntent = gsc.getSignInIntent();
        startActivityForResult(signInIntent, G_SIGN_IN);

    }
    private void facebookSignIn() {
        LoginButton btnFacebookSignIn = findViewById(R.id.btnFacebookSignIn);
        callbackManager = CallbackManager.Factory.create();
        btnFacebookSignIn.setReadPermissions("email", "public_profile");
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Toast.makeText(MainActivity.this, "Facebook login canceled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(@NonNull FacebookException error) {
                Log.e(TAG, "Facebook login error", error);
                Toast.makeText(MainActivity.this, "Facebook login error", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        Intent intent = new Intent(MainActivity.this, SignUpPageContinued.class);
                        startActivity(intent);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(MainActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            db.collection("Users").document(uid)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String type = document.getString("viewType");
                                // Use the viewType here
                                viewType(type);
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    });
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // For Google sign-in
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();
        gsc = GoogleSignIn.getClient(MainActivity.this, gso);

        // For Facebook sign-in
        LoginButton btnFacebookSignIn = findViewById(R.id.btnFacebookSignIn);
        btnFacebookSignIn.setOnClickListener(this);

        // initializes the buttons and texts and tells them to look to the switch statement (this class) for what to do once clicked
        Button btnSignIn = findViewById(R.id.btnSignIn);
        btnSignIn.setOnClickListener(this);

        SignInButton btnGmailSignIn = findViewById(R.id.btnGmailSignIn);
        btnGmailSignIn.setOnClickListener(this);

        Button btnTwitterSignIn = findViewById(R.id.btnTwitterSignIn);
        btnTwitterSignIn.setOnClickListener(this);

        TextView txtForgotPassword = findViewById(R.id.txtForgotPassword);
        txtForgotPassword.setOnClickListener(this);
        //TODO add way to change text color/appearance when being clicked on similar to how a button reacts

        TextView txtSignUp = findViewById(R.id.txtSignUp);
        txtSignUp.setOnClickListener(this);
        //TODO add way to change text color/appearance when being clicked on similar to how a button reacts

        // declaring the UI elements previously only instantiated
        txtEmail = findViewById(R.id.edtTxtEmail);
        txtPassword = findViewById(R.id.edtTxtPassword);
    }
}