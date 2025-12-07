package com.example.equiply.helper;

import androidx.annotation.NonNull;

import com.example.equiply.model.BorrowRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.function.Consumer;

public class BorrowRequestDA {
    private final DatabaseReference mDatabase;

    public BorrowRequestDA() {
        this.mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void addNewRequest(String toolId, String toolName, String userId, String borrowDate, String returnDate, String reason, Consumer<Boolean> callback) {
        String requestId = mDatabase.child("borrow_requests").push().getKey();

        if (requestId == null) {
            callback.accept(false);
            return;
        }

        BorrowRequest request = new BorrowRequest(
                requestId,
                toolId,
                toolName,
                userId,
                borrowDate,
                returnDate,
                reason,
                "pending",
                System.currentTimeMillis()
        );

        mDatabase.child("borrow_requests")
                .child(requestId)
                .setValue(request)
                .addOnSuccessListener(unused -> callback.accept(true))
                .addOnFailureListener(e -> callback.accept(false));
    }

    public void hasPendingRequest(String userId, String toolId, Consumer<Boolean> callback) {
        mDatabase.child("borrow_requests")
                .orderByChild("userId")
                .equalTo(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean hasPending = false;

                        for (DataSnapshot data : snapshot.getChildren()) {
                            BorrowRequest request = data.getValue(BorrowRequest.class);

                            if (request != null &&
                                    request.getToolId().equals(toolId) &&
                                    request.getStatus().equalsIgnoreCase("pending")) {
                                hasPending = true;
                                break;
                            }
                        }

                        callback.accept(hasPending);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.accept(false);
                    }
                });
    }

    //functions utk admin
    public void getBorrowRequestsByUserId(String userId, Consumer<ArrayList<BorrowRequest>> callback) {
        mDatabase.child("borrow_requests")
                .orderByChild("userId")
                .equalTo(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<BorrowRequest> list = new ArrayList<>();

                        for (DataSnapshot data : snapshot.getChildren()) {
                            BorrowRequest request = data.getValue(BorrowRequest.class);
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

    public void getAllPendingRequests(Consumer<ArrayList<BorrowRequest>> callback) {
        mDatabase.child("borrow_requests")
                .orderByChild("status")
                .equalTo("pending")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<BorrowRequest> list = new ArrayList<>();

                        for (DataSnapshot data : snapshot.getChildren()) {
                            BorrowRequest request = data.getValue(BorrowRequest.class);
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

    public void approveRequest(String requestId, String toolId, Consumer<Boolean> callback) {
        mDatabase.child("borrow_requests")
                .child(requestId)
                .child("status")
                .setValue("approved")
                .addOnSuccessListener(unused -> {

                    mDatabase.child("tools")
                            .child(toolId)
                            .child("status")
                            .setValue("dipinjam")
                            .addOnSuccessListener(unused2 -> callback.accept(true))
                            .addOnFailureListener(e -> callback.accept(false));

                })
                .addOnFailureListener(e -> callback.accept(false));
    }

    public void rejectRequest(String requestId, Consumer<Boolean> callback) {
        mDatabase.child("borrow_requests")
                .child(requestId)
                .child("status")
                .setValue("rejected")
                .addOnSuccessListener(unused -> callback.accept(true))
                .addOnFailureListener(e -> callback.accept(false));
    }
}
