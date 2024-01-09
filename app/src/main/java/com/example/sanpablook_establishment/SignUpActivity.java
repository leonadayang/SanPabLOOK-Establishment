package com.example.sanpablook_establishment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class SignUpActivity extends AppCompatActivity {

    Button verifyAcc;

    ImageButton backBtn;

    TextView redirectSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //BACK BUTTON
        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();

            }
        });

        //BUTTON Register/ Verify  Account
        verifyAcc = findViewById(R.id.verifyAccntBtn);
        verifyAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Intent VerfifyAccintent = new Intent(SignUpActivity.this, EmailConfirmationSent.class);
                    startActivity(VerfifyAccintent);
            }
        });

        // redirect back to Sign In
        redirectSignIn = findViewById(R.id.alreadyRegisteredBtn);
        redirectSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Shift to another activity here
                Intent signUpIntent = new Intent(SignUpActivity.this, SignInActivity.class);
                startActivity(signUpIntent);
            }
        });
    }
}