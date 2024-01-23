package com.example.sanpablook;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sanpablook_establishment.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class ReportsFragment extends Fragment {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    TextView valueTotalBookings, valuePendingBookings, valueCancelledBookings, valueConfirmedBookings;
    String establishmentID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reports, container, false);


        valueTotalBookings = view.findViewById(R.id.valueTotalBookings);
        valuePendingBookings = view.findViewById(R.id.valuePendingBookings);
        valueCancelledBookings = view.findViewById(R.id.valueCancelledBookings);
        valueConfirmedBookings = view.findViewById(R.id.valueConfirmedBookings);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();

        db.collection("usersEstablishment").document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            establishmentID = document.getString("establishmentID");
                            fetchAndDisplayBookings(view);
                            fetchAndDisplayTotalBookings();
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                });

        return view;
    }

    private void fetchAndDisplayTotalBookings() {
        db.collection("BookingPending")
                .whereEqualTo("establishmentID", establishmentID)
                .get()
                .addOnCompleteListener(task -> {
                    int bookingsCount = 0;
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            bookingsCount = querySnapshot.size();
                        }
                    } else {
                        Toast.makeText(requireContext(), "Error getting documents: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                    valueTotalBookings.setText(String.valueOf(bookingsCount));
                });
    }

    private void fetchAndDisplayBookings(View view) {
        String[] statuses = {"Pending", "Cancelled", "Confirmed"};
        TextView[] textViews = {
                valuePendingBookings,
                valueCancelledBookings,
                valueConfirmedBookings
        };

        for (int i = 0; i < statuses.length; i++) {
            int finalI = i;
            db.collection("BookingPending")
                    .whereEqualTo("establishmentID", establishmentID)
                    .whereEqualTo("status", statuses[i])
                    .get()
                    .addOnCompleteListener(task -> {
                        int bookingsCount = 0;
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot != null) {
                                bookingsCount = querySnapshot.size();
                            }
                        } else {
                            Toast.makeText(requireContext(), "Error getting documents: " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                        textViews[finalI].setText(String.valueOf(bookingsCount));

                        // Log the count of confirmed bookings
                        if (statuses[finalI].equals("Confirmed")) {
                            Log.d("ConfirmedBookings", "Count: " + bookingsCount);
                        }
                    });
        }
    }
}
