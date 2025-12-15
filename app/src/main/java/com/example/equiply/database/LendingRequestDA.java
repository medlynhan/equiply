package com.example.equiply.database;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.example.equiply.helper.CloudinaryHelper;
import com.example.equiply.model.LendingRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.function.Consumer;

public class LendingRequestDA {
    private final DatabaseReference mDatabase;
    private final CloudinaryHelper cloudinaryHelper;

    public LendingRequestDA(Context context) {
        this.mDatabase = FirebaseDatabase.getInstance().getReference();
        this.cloudinaryHelper = new CloudinaryHelper(context);
    }

    public void addNewRequest(String toolId, String toolName, String userId, String condition, String returnDate, Uri proofPhotoUri, Consumer<Boolean> callback) {
        cloudinaryHelper.uploadImage(proofPhotoUri, imageUrl -> {
            String requestId = mDatabase.child("return_requests").push().getKey();
            if (requestId == null) {
                callback.accept(false);
                return;
            }

            saveRequest(requestId, toolId, toolName, userId, condition, returnDate, imageUrl, callback);
        },
        errorMessage -> {
            callback.accept(false);
        }
        );
    }

    public void saveRequest(String requestId, String toolId, String toolName, String userId, String condition, String returnDate, String imageUrl, Consumer<Boolean> callback) {
        LendingRequest request = new LendingRequest(
                requestId,
                toolId,
                toolName,
                userId,
                condition,
                returnDate,
                imageUrl,
                "pending",
                System.currentTimeMillis()
        );

        mDatabase.child("return_requests")
                .child(requestId)
                .setValue(request)
                .addOnSuccessListener(unused -> {
                    updateBorrowRequestStatus(userId, toolId, callback);
                })
                .addOnFailureListener(e -> callback.accept(false));
    }

    private void updateBorrowRequestStatus(String userId, String toolId, Consumer<Boolean> callback) {
        mDatabase.child("borrow_requests")
                .orderByChild("userId")
                .equalTo(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot data : snapshot.getChildren()) {
                            String currentToolId = data.child("toolId").getValue(String.class);
                            String currentStatus = data.child("status").getValue(String.class);

                            // Find the active borrow request for this tool
                            if (toolId.equals(currentToolId) &&
                                    (currentStatus.equalsIgnoreCase("approved") || currentStatus.equalsIgnoreCase("Dipinjam"))) {

                                // Set status to match what HistoryAdapter looks for
                                data.getRef().child("status").setValue("pending_return");
                            }
                        }
                        callback.accept(true);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.accept(true);
                    }
                });
    }

    // for admin
    public void getReturnRequestsByUserId(String userId, Consumer<ArrayList<LendingRequest>> callback) {
        mDatabase.child("return_requests")
                .orderByChild("userId")
                .equalTo(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<LendingRequest> list = new ArrayList<>();
                        for (DataSnapshot data : snapshot.getChildren()) {
                            LendingRequest request = data.getValue(LendingRequest.class);
                            if (request != null) {
                                list.add(request);
                            }
                        }
                        callback.accept(list);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.accept(new ArrayList<>());
                    }
                });
    }

    public void getAllPendingReturnRequests(Consumer<ArrayList<LendingRequest>> callback) {
        mDatabase.child("return_requests")
                .orderByChild("status")
                .equalTo("pending")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<LendingRequest> list = new ArrayList<>();
                        for (DataSnapshot data : snapshot.getChildren()) {
                            LendingRequest request = data.getValue(LendingRequest.class);
                            if (request != null) {
                                list.add(request);
                            }
                        }
                        callback.accept(list);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.accept(new ArrayList<>());
                    }
                });
    }

    public void approveReturn(String requestId, String toolId, String condition, Consumer<Boolean> callback) {
        mDatabase.child("return_requests")
                .child(requestId)
                .child("status")
                .setValue("approved")
                .addOnSuccessListener(unused -> {
                    DatabaseReference toolRef = mDatabase.child("tools").child(toolId);
                    toolRef.child("status").setValue("Tersedia");
                    toolRef.child("toolStatus").setValue(condition);

                    callback.accept(true);
                })
                .addOnFailureListener(e -> callback.accept(false));
    }

    // not final
    public void rejectReturn(String requestId, Consumer<Boolean> callback) {
        mDatabase.child("return_requests")
                .child(requestId)
                .child("status")
                .setValue("rejected")
                .addOnSuccessListener(unused -> callback.accept(true))
                .addOnFailureListener(e -> callback.accept(false));
    }
}