package com.example.sanpablook_establishment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.sanpablook_establishment.databinding.ActivityBottomNavBarBinding;

public class RegistrationCreateAccountActivity extends AppCompatActivity {
    ImageButton btnReturn;
    Button btnConfirmAccount;

    String [] item = {"Hotel", "Bed & Breakfast", "Resort", "Restaurant", "Cafe", "Al fresco", "Products"};
    AutoCompleteTextView autoCompleteTextView;
    ArrayAdapter<String> adapterItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_create_account);

        //BACK BUTTON
        btnReturn = findViewById(R.id.btnReturn);
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();

            }
        });

        //BUTTON CREATE ACCOUNT
        btnConfirmAccount = findViewById(R.id.btnConfirmCreateAccount);
        btnConfirmAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegistrationCreateAccountActivity.this, BottomNavBarActivity.class);
                startActivity(intent);
            }
        });

        autoCompleteTextView = findViewById(R.id.dropdownBizType);
        adapterItems = new ArrayAdapter<String>(this, R.layout.list_item, item);
        autoCompleteTextView.setAdapter(adapterItems);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item = adapterView.getItemAtPosition(i).toString();
                Toast.makeText(RegistrationCreateAccountActivity.this, "Business Type: " + item, Toast.LENGTH_SHORT).show();
            }
        });
    }
}