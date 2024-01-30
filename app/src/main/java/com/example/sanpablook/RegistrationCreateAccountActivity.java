package com.example.sanpablook;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.sanpablook_establishment.R;
import com.example.sanpablook_establishment.databinding.ActivityBottomNavBarBinding;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class RegistrationCreateAccountActivity extends AppCompatActivity {
    ImageButton btnReturn;
    Button btnConfirmAccount;

    String [] item = {"Hotel", "Bed & Breakfast", "Resort", "Restaurant", "Cafe", "Al fresco", "Products"};
    AutoCompleteTextView autoCompleteTextView;
    ArrayAdapter<String> adapterItems;
    FirebaseAuth mAuth;
    FirebaseFirestore fStore;
    EditText editTextBusinessName, editTextBusinessPermit, editTextBusinessEmail, editTextTypeOfBusiness, editTextBusinessAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_create_account);

        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        TextInputLayout textInputLayoutTypeOfBusiness = findViewById(R.id.editTextTypeOfBusiness);
        editTextTypeOfBusiness = textInputLayoutTypeOfBusiness.getEditText();

        //BACK BUTTON
        btnReturn = findViewById(R.id.btnReturn);
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getOnBackPressedDispatcher();
            }
        });

        //BUTTON CREATE ACCOUNT
        btnConfirmAccount = findViewById(R.id.btnConfirmCreateAccount);
        btnConfirmAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the AutoCompleteTextView from the TextInputLayout
                AutoCompleteTextView autoCompleteTextView = findViewById(R.id.dropdownBizType);
                String selectedItem = autoCompleteTextView.getText().toString();
                editTextTypeOfBusiness.setText(selectedItem);

                editTextBusinessName = findViewById(R.id.editTextBusinessName);
                editTextBusinessPermit = findViewById(R.id.editTextBusinessPermit);
                editTextBusinessEmail = findViewById(R.id.editTextBusinessEmail);
                editTextBusinessAddress = findViewById(R.id.editTextBusinessAddress);

                // Check if the EditText fields are not empty
                if (TextUtils.isEmpty(editTextBusinessName.getText().toString().trim())) {
                    editTextBusinessName.setError("Please fill out this field.");
                    editTextBusinessName.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(editTextBusinessPermit.getText().toString().trim())) {
                    editTextBusinessPermit.setError("Please fill out this field.");
                    editTextBusinessPermit.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(editTextBusinessEmail.getText().toString().trim())) {
                    editTextBusinessEmail.setError("Please fill out this field.");
                    editTextBusinessEmail.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(editTextTypeOfBusiness.getText().toString().trim())) {
                    editTextTypeOfBusiness.setError("Please fill out this field.");
                    editTextTypeOfBusiness.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(editTextBusinessAddress.getText().toString().trim())) {
                    editTextBusinessAddress.setError("Please fill out this field.");
                    editTextBusinessAddress.requestFocus();
                    return;
                }

                //Call registerEstabUser
                registerEstabUser(editTextBusinessName, editTextBusinessPermit, editTextBusinessEmail, editTextTypeOfBusiness, editTextBusinessAddress);
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

    private void registerEstabUser(EditText editTextBusinessName, EditText editTextBusinessPermit, EditText editTextBusinessEmail, EditText editTextTypeOfBusiness, EditText editTextBusinessAddress) {
        //EditText fields
        String businessName = editTextBusinessName.getText().toString().trim();
        String businessPermit = editTextBusinessPermit.getText().toString().trim();
        String businessEmail = editTextBusinessEmail.getText().toString().trim();
        String businessType = editTextTypeOfBusiness.getText().toString().trim();
        String businessAddress = editTextBusinessAddress.getText().toString().trim();

        // Map of valid business permits and corresponding establishment IDs
        Map<String, String> permitToEstablishmentId = new HashMap<>();
        permitToEstablishmentId.put("111111", "casaDine");
        permitToEstablishmentId.put("222222", "palmerasHotel");
        permitToEstablishmentId.put("333333", "casaHotel");
        permitToEstablishmentId.put("444444", "palmerasDine");
        permitToEstablishmentId.put("555555", "sulyapDine");
        permitToEstablishmentId.put("666666", "tahananMedingHotel");

        // Check if the business permit is in the map
        String establishmentID = permitToEstablishmentId.get(businessPermit);
        if (establishmentID == null) {
            editTextBusinessPermit.setError("Invalid code");
            editTextBusinessPermit.requestFocus();
            return;
        }

        // Check if the EditText fields are not empty
        if (TextUtils.isEmpty(businessName)) {
            editTextBusinessName.setError("Please fill out this field.");
            editTextBusinessName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(businessPermit)) {
            editTextBusinessPermit.setError("Please fill out this field.");
            editTextBusinessPermit.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(businessEmail)) {
            editTextBusinessEmail.setError("Please fill out this field.");
            editTextBusinessEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(businessType)) {
            editTextTypeOfBusiness.setError("Please fill out this field.");
            editTextTypeOfBusiness.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(businessAddress)) {
            editTextBusinessAddress.setError("Please fill out this field.");
            editTextBusinessAddress.requestFocus();
            return;
        }

        //Generate random password
        String password = generateRandomPassword();

        // Use Firebase Authentication to create a new user with the email and generated password
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(businessEmail, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Get the user's ID
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        String userId = user.getUid();

                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        Map<String, Object> updates = new HashMap<>();
                        updates.put("businessName", businessName);
                        updates.put("businessPermit", businessPermit);
                        updates.put("businessEmail", businessEmail);
                        updates.put("businessType", businessType);
                        updates.put("businessAddress", businessAddress);
                        updates.put("password", password);
                        updates.put("establishmentID", establishmentID);
                        updates.put("userID", userId);

                        // Store the user's details in the 'usersEstablishment' collection
                        db.collection("usersEstablishment").document(userId)
                                .set(updates)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(RegistrationCreateAccountActivity.this, "Account Created", Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, "DocumentSnapshot added with ID: " + userId);

                                    // Start SignInActivity
                                    Intent intent = new Intent(RegistrationCreateAccountActivity.this, SignInActivity.class);
                                    startActivity(intent);
                                    finish();
                                })
                                .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));
                    } else {
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(RegistrationCreateAccountActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public String generateRandomPassword() {
        String possibleCharacters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder password = new StringBuilder();

        for (int i = 0; i < 6; i++) {
            int randomIndex = random.nextInt(possibleCharacters.length());
            password.append(possibleCharacters.charAt(randomIndex));
        }
        return password.toString();
    }
}