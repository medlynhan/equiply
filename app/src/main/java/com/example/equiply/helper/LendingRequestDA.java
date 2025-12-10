package com.example.equiply.helper;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.example.equiply.model.LendingRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.function.Consumer;

public class LendingRequestDA {
    private final DatabaseReference mDatabase;
    private final StorageReference mStorage;

    public LendingRequestDA() {
        this.mDatabase = FirebaseDatabase.getInstance().getReference();
        this.mStorage = FirebaseStorage.getInstance().getReference();
    }

    public void addNewRequest(String toolId, String toolName, String userId, String condition, String returnDate, Uri proofPhotoUri, Consumer<Boolean> callback) {
        String requestId = mDatabase.child("return_requests").push().getKey();

        if (requestId == null) {
            callback.accept(false);
            return;
        }

        StorageReference photoRef = mStorage.child("return_proofs").child(requestId + ".jpg");

        photoRef.putFile(proofPhotoUri)
                .addOnSuccessListener(taskSnapshot -> {
                    photoRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();

                        saveRequest(requestId, toolId, toolName, userId, condition, returnDate, imageUrl, callback);
                    }).addOnFailureListener(e -> callback.accept(false)); // -> fail dpt url
                }).addOnFailureListener(e -> callback.accept(false)); // -> fail upload image
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
                    // --- MISSING PART: UPDATE OLD STATUS ---
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

    public void approveReturn(String requestId, String toolId, Consumer<Boolean> callback) {
        mDatabase.child("return_requests")
                .child(requestId)
                .child("status")
                .setValue("approved")
                .addOnSuccessListener(unused -> {
                    // update Tool status
                    mDatabase.child("tools")
                            .child(toolId)
                            .child("status")
                            .setValue("Tersedia")
                            .addOnSuccessListener(unused2 -> callback.accept(true))
                            .addOnFailureListener(e -> callback.accept(false));
                })
                .addOnFailureListener(e -> callback.accept(false));
    }

    // not final
    public void rejectReturn(String requestId, Consumer<Boolean> callback) {

    }
}