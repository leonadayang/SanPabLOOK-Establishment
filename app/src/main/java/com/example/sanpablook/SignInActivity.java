package com.example.sanpablook;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.sanpablook_establishment.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SignInActivity extends AppCompatActivity {

    Button signIn;

    TextView redirectSignUp;
    TextView txtForgotPass;

    FirebaseAuth mAuth;
    FirebaseFirestore fStore;
    EditText businesseEmail, businessPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        //Objects
        fStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        businesseEmail = findViewById(R.id.editTextBusinessEmail);
        businessPassword = findViewById(R.id.editTextPassword);

        if (mAuth.getCurrentUser() != null) {
            redirectToMain();
            return;
        }

        //BUTTON Log In/Sign In Account
        signIn = findViewById(R.id.loginBtn);
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });



     // Don't have an account? Register now (text)
        redirectSignUp = findViewById(R.id.textRedirectRegisterNow);
        redirectSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Shift to another activity here
                Intent signUpIntent = new Intent(SignInActivity.this, RegistrationCreateAccountActivity.class);
                startActivity(signUpIntent);
            }
        });

    //Text Forgot Password
        txtForgotPass = findViewById(R.id.forgotPassword);
        txtForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignInActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
                finish();
            }
        });


    }

    private void signIn() {
        String email = businesseEmail.getText().toString().trim();
        String password = businessPassword.getText().toString().trim();

        if (email.isEmpty()) {
            businesseEmail.setError("Email is required");
            businesseEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            businessPassword.setError("Password is required");
            businessPassword.requestFocus();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();
                        redirectToMain();
                    } else {
                        Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void redirectToMain() {
        startActivity(new Intent(this, BottomNavBarActivity.class));
        finish();
    }

}