package com.example.sanpablook;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.sanpablook.Adapter.RecyclerFragmentBookings;
import com.example.sanpablook_establishment.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;


public class BookingsFragment extends Fragment {

    RecyclerView recyclerViewFragmentBookings;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String establishmentID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bookings, container, false);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("usersEstablishment").document(userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                establishmentID = document.getString("establishmentID");
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });
        //RECYCLER VIEW
        recyclerViewFragmentBookings = view.findViewById(R.id.recyclerViewFragmentBookings);
        recyclerViewFragmentBookings.setLayoutManager(new LinearLayoutManager(requireContext()));


        // Query Firestore
        db.collection("BookingPending")
                .whereEqualTo("establishmentID", establishmentID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Map<String, Object>> bookings = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            bookings.add(document.getData());
                        }
                        // Set adapter
                        RecyclerFragmentBookings adapter = new RecyclerFragmentBookings(bookings);
                        recyclerViewFragmentBookings.setAdapter(adapter);
                    } else {
                        Toast.makeText(requireContext(), "Error getting documents: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });

        Spinner monthSpinner = view.findViewById(R.id.monthSpinner);
        Spinner yearSpinner = view.findViewById(R.id.yearSpinner);
        int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int startingYear = 2010;

        List<String> yearList = new ArrayList<>();
        List<String> monthList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.monthArray)));

        // Populate the list with years from starting year to current year
        for (int year = startingYear; year <= currentYear; year++) {
            yearList.add(String.valueOf(year));
        }

        //month
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(requireContext(), R.layout.spinner_item_layout, monthList);
        monthAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        monthSpinner.setAdapter(monthAdapter);
        monthSpinner.setSelection(currentMonth - 1);

        //year
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(requireContext(), R.layout.spinner_item_layout, yearList);
        yearAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        yearSpinner.setAdapter(yearAdapter);

        // Set the default selection to the current year
        int defaultYearIndex = yearList.indexOf(String.valueOf(currentYear));
        if (defaultYearIndex != -1) {
            yearSpinner.setSelection(defaultYearIndex);
        }


        return view;
    }
}