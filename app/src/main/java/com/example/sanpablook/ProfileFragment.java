package com.example.sanpablook;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.sanpablook_establishment.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProfileFragment extends Fragment {

    ImageButton btnSettings;

    StorageReference storageReference;
    FirebaseUser user;
    FirebaseAuth auth;
    FirebaseFirestore fStore;
    String userID;
    String establishmentID;

    TextView textHostName;
    TextView textEstablishmentName;

    ShapeableImageView profilePictureEstablishment, imageviewFeaturedImage, profilePictureHost;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        //Firebase Auth
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        fStore = FirebaseFirestore.getInstance();
        userID = auth.getCurrentUser().getUid();
        storageReference = FirebaseStorage.getInstance().getReference();

        profilePictureHost = view.findViewById(R.id.profilePictureHost);
        imageviewFeaturedImage = view.findViewById(R.id.imageviewFeaturedImage);
        profilePictureEstablishment = view.findViewById(R.id.profilePictureEstablishment);
        textHostName = view.findViewById(R.id.textHostName);
        textEstablishmentName = view.findViewById(R.id.textEstablishmentName);

        FirebaseFirestore fireStore = FirebaseFirestore.getInstance();
        DocumentReference establishmentReference = fireStore.collection("establishment").document(userID);
        DocumentReference documentReference = fStore.collection("usersEstablishment").document(userID);

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

        // Get user's establishment name
        documentReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String establishmentName = document.getString("businessName");
                    if (establishmentName != null && !establishmentName.isEmpty()) {
                        textEstablishmentName.setText(establishmentName);
                    } else {
                        textEstablishmentName.setText("No Establishment Name yet");
                    }
                } else {
                    Log.d(TAG, "No such document");
                    textEstablishmentName.setText("No Establishment Name yet");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
                textEstablishmentName.setText("No Establishment Name yet");
            }
        });

        //Get user's host name
        documentReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String hostName = document.getString("establishmentHostName");
                    if (hostName != null && !hostName.isEmpty()) {
                        textHostName.setText(hostName);
                    } else {
                        textHostName.setText("No Host Name yet");
                    }
                } else {
                    Log.d(TAG, "No such document");
                    textHostName.setText("No Host Name yet");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
                textHostName.setText("No Host Name yet");
            }
        });

        // Get user's bio
        documentReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String bio = document.getString("bio");
                    if (bio != null && !bio.isEmpty()) {
                        TextView bioTextView = view.findViewById(R.id.textContentBio);
                        bioTextView.setText(bio);
                    } else {
                        TextView bioTextView = view.findViewById(R.id.textContentBio);
                        bioTextView.setText("No Bio yet");
                    }
                } else {
                    Log.d(TAG, "No such document");
                    TextView bioTextView = view.findViewById(R.id.textContentBio);
                    bioTextView.setText("No Bio yet");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
                TextView bioTextView = view.findViewById(R.id.textContentBio);
                bioTextView.setText("No Bio yet");
            }
        });

        // For profile picture
        StorageReference estabPicRef = FirebaseStorage.getInstance().getReference().child("estabProfilePictures/" + establishmentID + ".jpg");

        // Glide Profile
        estabPicRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(ProfileFragment.this)
                        .load(uri)
                        .into(profilePictureEstablishment);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Toast.makeText(getActivity(), "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        });


        // For featured image
        StorageReference featuredPicRef = FirebaseStorage.getInstance().getReference().child("estabFeaturedPictures/" + establishmentID + ".jpg");

        // Glide for featured
        featuredPicRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(ProfileFragment.this)
                        .load(uri)
                        .into(imageviewFeaturedImage);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Toast.makeText(getActivity(), "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        });

        //For Host
        StorageReference hostPicRef = FirebaseStorage.getInstance().getReference().child("estabHostPictures/" + establishmentID + ".jpg");

        // Glide for Host
        hostPicRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getActivity())
                        .load(uri)
                        .into(profilePictureHost);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Toast.makeText(getActivity(), "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        });

        btnSettings = view.findViewById(R.id.settingsButton);

        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToSettings(view);
            }
        });

        return view;
    }

    private void goToSettings(View view) {
        Intent intent = new Intent(getActivity(), ProfileSettingsActivity.class);
        startActivity(intent);
    }
}