package com.example.sanpablook_establishment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class RegistrationProcessActivity extends AppCompatActivity {

    TextView btnBackLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_process);
        btnBackLogin = findViewById(R.id.btnBackLogIn);
        btnBackLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegistrationProcessActivity.this, RegistrationCompleteActivity.class);
                startActivity(intent);
            }
        });



    }
}