package com.example.drinkersapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPassword extends AppCompatActivity implements View.OnClickListener {
    private EditText txtEmail;
    FirebaseAuth mAuth;
    @Override
    public void onClick(View v) {
        String email = String.valueOf(txtEmail.getText());
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(ResetPassword.this, "Email field empty", Toast.LENGTH_SHORT).show();
        }
        else{
        switch (v.getId()) {
            case R.id.btnContinue:
                mAuth.getInstance()
                        .sendPasswordResetEmail(email)
                        .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                // Receive response from Firebase Console
                                Toast.makeText(ResetPassword.this, "Received in Firebase.", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                // Reset password email has been successfully sent to the email provided
                                Toast.makeText(ResetPassword.this, "Email successfully sent.", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(this, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Reset password request is failed with an exception
                                Toast.makeText(ResetPassword.this, "Request failed.", Toast.LENGTH_SHORT).show();
                            }
                        });
                break;
            case R.id.btnSignIn:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        mAuth = FirebaseAuth.getInstance();
        Button btnContinue = findViewById(R.id.btnContinue);
        btnContinue.setOnClickListener(this);
        txtEmail = findViewById(R.id.edtxtEmail);
        Button btnSignIn = findViewById(R.id.btnSignIn);
        btnSignIn.setOnClickListener(this);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}