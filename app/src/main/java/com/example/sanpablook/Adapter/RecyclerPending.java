package com.example.sanpablook.Adapter;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sanpablook.ProfileSettingsActivity;
import com.example.sanpablook_establishment.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RecyclerPending extends RecyclerView.Adapter<RecyclerPending.BookingViewHolder> {

    private List<Map<String, Object>> bookings;

    static FirebaseFirestore db;

    public RecyclerPending(List<Map<String, Object>> bookings) {
        this.bookings = new ArrayList<>();
        for (Map<String, Object> booking : bookings) {
            if (booking.get("status").toString().equals("Pending")) {
                this.bookings.add(booking);
            }
        }
        db = FirebaseFirestore.getInstance();
        Log.d("RecyclerPending", "Number of bookings: " + this.bookings.size());
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_pending_booking, parent, false);
        return new BookingViewHolder(view, bookings);
    }

    public interface OnBookingSelectedListener {
        void onBookingSelected(long dateInMillis);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Map<String, Object> booking = bookings.get(position);
        Log.d("RecyclerPending", "onBindViewHolder called for position: " + position);
        holder.txtPendingBooking.setText(booking.get("bookingID").toString());
        holder.txtPendingCustomerName.setText(booking.get("fullName").toString());
        holder.txtPendingDate.setText(booking.get("date").toString());
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }

    static class BookingViewHolder extends RecyclerView.ViewHolder {

        Button btnAccept, btnDecline;
        TextView txtPendingBooking, txtPendingCustomerName, txtPendingDate;
        List<Map<String, Object>> bookings;

        public BookingViewHolder(@NonNull View itemView, List<Map<String, Object>> bookings) {
            super(itemView);
            this.bookings = bookings;

            // Find other TextViews
            txtPendingBooking = itemView.findViewById(R.id.txtPendingBooking);
            txtPendingCustomerName = itemView.findViewById(R.id.txtPendingCustomerName);
            txtPendingDate = itemView.findViewById(R.id.txtPendingDate);

            // Accept booking btn
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDialogConfirm(view);
                }

                //dialog for confirm
                private void showDialogConfirm(View view) {
                    final Dialog dialog = new Dialog(itemView.getContext());
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.dialog_confirm);

                    Button buttonConfirm = dialog.findViewById(R.id.buttonConfirm);
                    Button buttonCancel = dialog.findViewById(R.id.buttonCancel);

                    buttonConfirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            acceptBooking(view);
                            Toast makeText = Toast.makeText(view.getContext(), "You accepted this booking", Toast.LENGTH_SHORT);
                        }
                    });

                    buttonCancel.setOnClickListener(new View.OnClickListener() {
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

                //accept booking
                private void acceptBooking(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Map<String, Object> clickedBooking = bookings.get(position);
                        String bookingID = clickedBooking.get("bookingID").toString();

                        Log.d("RecyclerPending", "Booking ID: " + bookingID); // Add this line

                        DocumentReference bookingRef = db.collection("BookingPending").document(bookingID);
                        bookingRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        bookingRef.update("status", "Confirmed")
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(view.getContext(), "Booking confirmed", Toast.LENGTH_SHORT).show();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(view.getContext(), "Error updating document", Toast.LENGTH_SHORT).show();
                                                        Log.w("RecyclerPending", "Error updating document", e);
                                                    }
                                                });
                                    } else {
                                        Log.d("RecyclerPending", "No such document");
                                    }
                                } else {
                                    Log.d("RecyclerPending", "get failed with ", task.getException());
                                }
                            }
                        });
                    }
                }
            });

            // Decline booking btn
            btnDecline = itemView.findViewById(R.id.btnDecline);
            btnDecline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDialogDecline(view);
                }

                //dialog for decline
                private void showDialogDecline(View view) {
                    final Dialog dialog = new Dialog(itemView.getContext());
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.dialog_decline);

                    Button buttonDecline = dialog.findViewById(R.id.buttonDecline);
                    Button buttonCancel = dialog.findViewById(R.id.buttonCancel);

                    buttonDecline.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            declineBooking(view);
                            Toast.makeText(view.getContext(), "You declined this booking", Toast.LENGTH_SHORT).show();
                        }
                    });

                    buttonCancel.setOnClickListener(new View.OnClickListener() {
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

                private void declineBooking(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Map<String, Object> clickedBooking = bookings.get(position);
                        String bookingID = clickedBooking.get("bookingID").toString();

                        DocumentReference bookingRef = db.collection("BookingPending").document(bookingID);
                        bookingRef.update("status", "Cancelled")
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(view.getContext(), "Booking cancelled", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(view.getContext(), "Error updating document", Toast.LENGTH_SHORT).show();
                                        Log.w("RecyclerPending", "Error updating document", e);
                                    }
                                });
                    }
                }
            });


        }
    }
}