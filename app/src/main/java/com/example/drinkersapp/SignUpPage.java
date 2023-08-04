package com.example.drinkersapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpPage extends AppCompatActivity implements View.OnClickListener {
    FirebaseAuth mAuth;

    // instantiating email and password text inputs
    EditText txtEmail, txtPassword;
    // instantiating the checkbox for use in different classes
    private CheckBox chkTermsOfService;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(getApplicationContext(), CustomerUIContainer.class);
            startActivity(intent);
            finish();
        }
    }

    // switch statement for actions to take based on what button the user presses
    @Override
    public void onClick(View v) {
        // setting user input values
        String email, password;
        email = String.valueOf(txtEmail.getText());
        password = String.valueOf(txtPassword.getText());

        switch (v.getId()) {
            case R.id.btnContinue:
                if (chkTermsOfService.isChecked() && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
                    // creates user in Firebase
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    // If sign up successful, display a message to the user.
                                    Toast.makeText(SignUpPage.this, "SignUp Complete.",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(SignUpPage.this, "SignUp failed.",
                                            Toast.LENGTH_SHORT).show();

                                }
                            });

                    // takes user to the continued Sign Up page
                    Intent intent = new Intent(this, SignUpPageContinued.class);
                    startActivity(intent);
                } else {
                    // Check if email is empty
                    if (TextUtils.isEmpty(email)){
                        Toast.makeText(SignUpPage.this,"Email field empty", Toast.LENGTH_SHORT).show();
                    }
                    // Check if password is empty
                    else if (TextUtils.isEmpty(password)){
                        Toast.makeText(SignUpPage.this,"Password field empty", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(SignUpPage.this, "Continue clicked without checkbox", Toast.LENGTH_SHORT).show();
                        //TODO add functionality to tell user this and other fields are required to sign up
                    }
                }
                break;
            case R.id.txtSignIn:
                // takes user back to the Sign In page
                Intent intent = new Intent(SignUpPage.this, MainActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_page);
        mAuth = FirebaseAuth.getInstance();
        // email and password data
        txtEmail = findViewById(R.id.edtTxtEmail);
        txtPassword = findViewById(R.id.edtTxtPassword);
        // declares the checkbox and creates a listener to see if it's checked or not
        chkTermsOfService = findViewById(R.id.chkTermsOfService);
        chkTermsOfService.setOnCheckedChangeListener((compoundButton, b) -> {

        });

        // initializes the button and text and tells them to look to the switch statement (this class) for what to do once clicked
        Button btnContinue = findViewById(R.id.btnContinue);
        btnContinue.setOnClickListener(this);

        TextView txtSignIn = findViewById(R.id.txtSignIn);
        txtSignIn.setOnClickListener(this);

    }


}