package com.example.sanpablook;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.sanpablook_establishment.R;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProfileSettingsActivity extends AppCompatActivity {

    private Dialog dialog;
    ImageButton btnBack;
    TextView txtPassword, txtBio, txtPhoneNumber, txtHostName;
    Button buttonLogout;

    //IMAGES
    FloatingActionButton fabEditEstablishmentProfilePicture, fabEditHostProfilePicture, fabEditFeatured;
    ShapeableImageView editEstablishmentProfilePicture, editHostProfilePicture, editFeatured;

    TextView valueOfPhoneNumber, valueOfHostName;

    //Firebase
    FirebaseStorage storage;

    private StorageReference storageReference;
    StorageReference storageRef;
    FirebaseUser user;
    FirebaseAuth auth;
    FirebaseFirestore fStore;
    String userID;

    String establishmentID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);

        valueOfHostName = findViewById(R.id.valueOfHostName);
        valueOfPhoneNumber = findViewById(R.id.valueOfPhoneNumber);
        btnBack = findViewById(R.id.buttonBackSettings);
        txtHostName = findViewById(R.id.editTheHostName);
        txtPassword = findViewById(R.id.editThePassword);
        txtBio = findViewById(R.id.editTheBio);
        txtPhoneNumber = findViewById(R.id.editThePhoneNumber);
        buttonLogout = findViewById(R.id.buttonLogout);

        //Firebase Auth
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        fStore = FirebaseFirestore.getInstance();
        userID = auth.getCurrentUser().getUid();
        storageReference = FirebaseStorage.getInstance().getReference();

        //IMAGES
        fabEditEstablishmentProfilePicture = findViewById(R.id.fabEditEstablishmentProfilePicture);
        editEstablishmentProfilePicture = findViewById(R.id.editEstablishmentProfilePicture);
        fabEditHostProfilePicture = findViewById(R.id.fabEditHostProfilePicture);
        editHostProfilePicture = findViewById(R.id.editHostProfilePicture);
        fabEditFeatured = findViewById(R.id.fabEditFeatured);
        editFeatured = findViewById(R.id.editFeatured);

        FirebaseFirestore fireStore = FirebaseFirestore.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        DocumentReference documentReference = fireStore.collection("usersEstablishment").document(userID);
        DocumentReference establishmentReference = fireStore.collection("establishment").document(userID);
        StorageReference profilePicRef = storage.getReference().child("estabProfilePictures/" + userID + ".jpg");

        // Fetch the document
        establishmentReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // Get the establishmentID
                    establishmentID = document.getString("establishmentID");
                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });

        // For profile picture
        StorageReference estabPicRef = FirebaseStorage.getInstance().getReference().child("estabProfilePictures/" + establishmentID + ".jpg");

        // Glide Profile
        estabPicRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(ProfileSettingsActivity.this)
                        .load(uri)
                        .into(editEstablishmentProfilePicture);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Toast.makeText(ProfileSettingsActivity.this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        });


        // For featured image
        StorageReference featuredPicRef = FirebaseStorage.getInstance().getReference().child("estabFeaturedPictures/" + establishmentID + ".jpg");

        // Glide for featured
        featuredPicRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(ProfileSettingsActivity.this)
                        .load(uri)
                        .into(editFeatured);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Toast.makeText(ProfileSettingsActivity.this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        });

        //For Host
        StorageReference hostPicRef = FirebaseStorage.getInstance().getReference().child("estabHostPictures/" + establishmentID + ".jpg");

        // Glide for Host
        hostPicRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(ProfileSettingsActivity.this)
                        .load(uri)
                        .into(editHostProfilePicture);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Toast.makeText(ProfileSettingsActivity.this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        });


        // Get user's phone number
        documentReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String phoneNumber = document.getString("estabPhoneNumber");
                    if (phoneNumber != null && !phoneNumber.isEmpty()) {
                        valueOfPhoneNumber.setText(phoneNumber);
                    } else {
                        valueOfPhoneNumber.setText("No Phone Number yet");
                    }
                } else {
                    Log.d(TAG, "No such document");
                    valueOfPhoneNumber.setText("No Phone Number yet");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
                valueOfPhoneNumber.setText("No Phone Number yet");
            }
        });

        // Get user's bio
        documentReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String bio = document.getString("bio");
                    if (bio != null && !bio.isEmpty()) {
                        TextView bioTextView = findViewById(R.id.valueOfBio);
                        bioTextView.setText(bio);
                    } else {
                        TextView bioTextView = findViewById(R.id.valueOfBio);
                        bioTextView.setText("No Bio yet");
                    }
                } else {
                    Log.d(TAG, "No such document");
                    TextView bioTextView = findViewById(R.id.valueOfBio);
                    bioTextView.setText("No Bio yet");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
                TextView bioTextView = findViewById(R.id.valueOfBio);
                bioTextView.setText("No Bio yet");
            }
        });

        //Get user's host name
        documentReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String hostName = document.getString("establishmentHostName");
                    if (hostName != null && !hostName.isEmpty()) {
                        valueOfHostName.setText(hostName);
                    } else {
                        valueOfHostName.setText("No Host Name yet");
                    }
                } else {
                    Log.d(TAG, "No such document");
                    valueOfHostName.setText("No Host Name yet");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
                valueOfHostName.setText("No Host Name yet");
            }
        });

        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Sign out the current user
                FirebaseAuth.getInstance().signOut();

                // Show a message to the user
                Toast.makeText(ProfileSettingsActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();

                // Redirect the user to the login activity (replace LoginActivity.class with your login activity class)
                Intent intent = new Intent(ProfileSettingsActivity.this, SignInActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });
        txtHostName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogEditHostName(view);
            }
        });
        txtPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogEditPassword(view);
            }
        });
        txtBio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogEditBio(view);
            }
        });
        txtPhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogEditPhoneNumber(view);
            }
        });

        //IMAGES
        fabEditEstablishmentProfilePicture.setOnClickListener(view1 -> {
            ImagePicker.Companion.with(this)
                    .crop()                 // Crop image(Optional), Check Customization for more option
                    .compress(1024)         // Final image size will be less than 1 MB(Optional)
                    .maxResultSize(1080, 1080)   // Final image resolution will be less than 1080 x 1080(Optional)
                    .start(1);
        });
        fabEditHostProfilePicture.setOnClickListener(view1 -> {
            ImagePicker.Companion.with(this)
                    .crop()                 // Crop image(Optional), Check Customization for more option
                    .compress(1024)         // Final image size will be less than 1 MB(Optional)
                    .maxResultSize(1080, 1080)   // Final image resolution will be less than 1080 x 1080(Optional)
                    .start(2);
        });
        fabEditFeatured.setOnClickListener(view1 -> {
            ImagePicker.Companion.with(this)
                    .crop()                 // Crop image(Optional), Check Customization for more option
                    .compress(1024)         // Final image size will be less than 1 MB(Optional)
                    .maxResultSize(1080, 1080)   // Final image resolution will be less than 1080 x 1080(Optional)
                    .start(3);
        });
    }

    //IMAGES
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Get the image URI
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                Uri imageUri = data.getData();
                editEstablishmentProfilePicture.setImageURI(imageUri);

                // Upload the image to Firebase Storage
                uploadEstablishmentImageToFirebase(imageUri);
            } else if (requestCode == 2) {
                Uri imageUri = data.getData();
                editHostProfilePicture.setImageURI(imageUri);

                uploadImageToFirebaseHost(imageUri);
            } else if (requestCode == 3) {
                Uri imageUri = data.getData();
                editFeatured.setImageURI(imageUri);

                // Upload the image to Firebase Storage
                uploadFeaturedImageToFirebase(imageUri);
            }
        }
    }

    private void uploadEstablishmentImageToFirebase(Uri imageUri) {
        // Create a reference to the location where we'll upload the image
        StorageReference imageRef = storageReference.child("estabProfilePictures/" + establishmentID + ".jpg");

        // Upload the image to Firebase Storage
        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Image uploaded successfully
                    Toast.makeText(ProfileSettingsActivity.this, "Image uploaded", Toast.LENGTH_SHORT).show();

                    // Refresh the activity
                    recreate();
                })
                .addOnFailureListener(e -> {
                    // Failed to upload the image
                    Toast.makeText(ProfileSettingsActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                });
    }

    private void uploadFeaturedImageToFirebase(Uri imageUri) {
        // Create a reference to the location where we'll upload the image
        StorageReference imageRef = storageReference.child("estabFeaturedPictures/" + establishmentID + ".jpg");

        // Upload the image to Firebase Storage
        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Image uploaded successfully
                    Toast.makeText(ProfileSettingsActivity.this, "Image uploaded", Toast.LENGTH_SHORT).show();

                    // Refresh the activity
                    recreate();
                })
                .addOnFailureListener(e -> {
                    // Failed to upload the image
                    Toast.makeText(ProfileSettingsActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                });
    }

    private void uploadImageToFirebaseHost(Uri imageUri) {
        // Create a reference to the location where we'll upload the image
        StorageReference imageRef = storageReference.child("estabHostPictures/" + establishmentID + ".jpg");

        // Upload the image to Firebase Storage
        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Image uploaded successfully
                    Toast.makeText(ProfileSettingsActivity.this, "Image uploaded", Toast.LENGTH_SHORT).show();

                    recreate();
                })
                .addOnFailureListener(e -> {
                    // Failed to upload the image
                    Toast.makeText(ProfileSettingsActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                });
    }

    private void showDialogEditHostName(View view) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_edit_host_name);

        Button btnSave = dialog.findViewById(R.id.buttonSave);
        Button btnCancel = dialog.findViewById(R.id.buttonCancel);
        EditText editTextHostName = dialog.findViewById(R.id.editTextHostName);

        btnSave.setEnabled(false);
        btnSave.setAlpha(0.5f);

        editTextHostName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Not needed for this case
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                btnSave.setEnabled(true);
                btnSave.setAlpha(1.0f);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Not needed for this case
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newHostName = editTextHostName.getText().toString();
                if (newHostName.isEmpty()) {
                    editTextHostName.requestFocus();
                    editTextHostName.setError("Field can't be empty");
                    return;
                }
                DocumentReference documentReference = fStore.collection("usersEstablishment").document(userID);
                documentReference.update("establishmentHostName", newHostName)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(ProfileSettingsActivity.this, "Host name has been updated", Toast.LENGTH_SHORT).show();
                            valueOfHostName.setText(newHostName);  // Update the TextView
                        })
                        .addOnFailureListener(e -> Log.w(TAG, "Error updating document", e));
                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private EditText editTextCurrentPasswordChange;

    private void changePassword() {
        // Get the current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // Get the current password, new password, and retype password from the EditText fields
        String currentPassword = editTextCurrentPasswordChange.getText().toString();
        String newPassword = editTextNew.getText().toString();
        String retypePassword = editTextRetype.getText().toString();

        // Log the values
        Log.d("ProfileSettingsActivity", "Current password: " + currentPassword);
        Log.d("ProfileSettingsActivity", "New password: " + newPassword);
        Log.d("ProfileSettingsActivity", "Retype password: " + retypePassword);

        // Sign in the user with their email and current password
        FirebaseAuth.getInstance().signInWithEmailAndPassword(user.getEmail(), currentPassword)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Sign in was successful, now re-authenticate the user
                        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);
                        user.reauthenticate(credential).addOnCompleteListener(reauthTask -> {
                            if (reauthTask.isSuccessful()) {
                                // Re-authentication was successful, now update the password
                                user.updatePassword(newPassword)
                                        .addOnSuccessListener(aVoid -> {
                                            // The password update was successful
                                            Toast.makeText(ProfileSettingsActivity.this, "Password updated", Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();  // Dismiss the dialog
                                        })
                                        .addOnFailureListener(e -> {
                                            // The password update failed
                                            Toast.makeText(ProfileSettingsActivity.this, "Failed to update password", Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();  // Dismiss the dialog
                                        });
                            } else {
                                // The re-authentication failed
                                Toast.makeText(ProfileSettingsActivity.this, "Please check your current password.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        // Sign in failed, show a message to the user
                        Toast.makeText(ProfileSettingsActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    //for edit password
    private EditText editTextNew;
    private EditText editTextRetype;
    private Button btnSave;

    private void showDialogEditPassword(View view) {
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_edit_password);

        btnSave = dialog.findViewById(R.id.buttonSave);
        Button btnCancel = dialog.findViewById(R.id.buttonCancel);
        editTextNew = dialog.findViewById(R.id.editTextNewPassword);
        editTextRetype = dialog.findViewById(R.id.editTextRetypePassword);
        editTextCurrentPasswordChange = dialog.findViewById(R.id.editTextCurrentPassword);

        btnSave.setEnabled(false);
        btnSave.setAlpha(0.5f);  // initial color is opaque

        editTextNew.addTextChangedListener(passwordTextWatcher);
        editTextRetype.addTextChangedListener(passwordTextWatcher);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePassword();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private TextWatcher passwordTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            // Not needed for this case
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            // Check if the text in both EditTexts match
            boolean passwordsMatch = editTextNew.getText().toString().equals(editTextRetype.getText().toString());

            // Enable or disable the Save button accordingly
            btnSave.setEnabled(passwordsMatch);
            btnSave.setAlpha(passwordsMatch ? 1.0f : 0.5f);
        }

        @Override
        public void afterTextChanged(Editable editable) {
            // Not needed for this case
        }
    };

    private void showDialogEditBio(View view) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_edit_bio);

        Button btnSave = dialog.findViewById(R.id.buttonSave);
        Button btnCancel = dialog.findViewById(R.id.buttonCancel);
        EditText editTextBio = dialog.findViewById(R.id.editTextBio);

        btnSave.setEnabled(false);
        btnSave.setAlpha(0.5f);

        editTextBio.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Not needed for this case
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                btnSave.setEnabled(true);
                btnSave.setAlpha(1.0f);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Not needed for this case
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newBio = editTextBio.getText().toString();
                if (newBio.isEmpty()) {
                    editTextBio.requestFocus();
                    editTextBio.setError("Field can't be empty");
                    return;
                }
                DocumentReference documentReference = fStore.collection("usersEstablishment").document(userID);
                documentReference.update("bio", newBio)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(ProfileSettingsActivity.this, "Bio has been updated", Toast.LENGTH_SHORT).show();
                            TextView bioTextView = findViewById(R.id.valueOfBio);
                            bioTextView.setText(newBio);  // Update the TextView
                        })
                        .addOnFailureListener(e -> Log.w(TAG, "Error updating document", e));
                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private void showDialogEditPhoneNumber(View view) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_edit_phone_number);

        final Button btnSave = dialog.findViewById(R.id.buttonSave);
        Button btnCancel = dialog.findViewById(R.id.buttonCancel);
        final EditText editTextPhoneNumber = dialog.findViewById(R.id.editTextPhoneNumber);

        btnSave.setEnabled(false);
        btnSave.setAlpha(0.5f);

        editTextPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                // Not needed for this case
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (validateMobile(editTextPhoneNumber.getText().toString())) {
                    btnSave.setEnabled(true);
                    btnSave.setAlpha(1.0f);
                } else {
                    btnSave.setEnabled(false);
                    editTextPhoneNumber.setError("Invalid phone number");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Not needed for this case
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newPhoneNumber = editTextPhoneNumber.getText().toString();
                if (newPhoneNumber.isEmpty()) {
                    editTextPhoneNumber.requestFocus();
                    editTextPhoneNumber.setError("Field can't be empty");
                    return;
                }
                DocumentReference documentReference = fStore.collection("usersEstablishment").document(userID);
                documentReference.update("estabPhoneNumber", newPhoneNumber)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(ProfileSettingsActivity.this, "Phone number has been updated", Toast.LENGTH_SHORT).show();
                            TextView phoneNumberTextView = findViewById(R.id.valueOfPhoneNumber);
                            phoneNumberTextView.setText(newPhoneNumber);  // Update the TextView
                        })
                        .addOnFailureListener(e -> Log.w(TAG, "Error updating document", e));
                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private void showDialogDeleteAccount(View view) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_delete_account);

        Button btnConfirm = dialog.findViewById(R.id.buttonConfirm);
        Button btnCancel = dialog.findViewById(R.id.buttonCancel);
        EditText txtDeleteAccount = dialog.findViewById(R.id.editTextDeleteAccount);

        btnConfirm.setEnabled(false);
        btnConfirm.setAlpha(0.5f);  // initial color is opaque

        txtDeleteAccount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Not needed for this case
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // if text is "Delete account"
                boolean isDeleteAccount = charSequence.toString().equals("Delete account");

                // Enable or disable button
                btnConfirm.setEnabled(isDeleteAccount);
                btnConfirm.setAlpha(isDeleteAccount ? 1.0f : 0.5f);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Not needed for this case
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ProfileSettingsActivity.this, "Your account has been deleted", Toast.LENGTH_SHORT).show();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    //for phone number
    boolean validateMobile(String input) {
        Pattern p = Pattern.compile("[0][9][0-9]{9}");
        Matcher m = p.matcher(input);
        return m.matches();
    }
}