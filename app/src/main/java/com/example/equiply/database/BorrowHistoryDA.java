package com.example.equiply.database;

import androidx.annotation.NonNull;

import com.example.equiply.model.BorrowHistory;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.function.Consumer;

public class BorrowHistoryDA {
    private final DatabaseReference mDatabase;

    public BorrowHistoryDA() {
        this.mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void addBorrowHistory(BorrowHistory borrowHistory, Consumer<Boolean> callback){
        mDatabase.child("history").push().setValue(borrowHistory).addOnCompleteListener(task -> {
            callback.accept(task.isSuccessful());
        });

    }

    public void hasPendingRequest(String userId, String toolId, Consumer<Boolean> callback) {
        mDatabase.child("history")
                .orderByChild("userId")
                .equalTo(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean hasPending = false;

                        for (DataSnapshot data : snapshot.getChildren()) {
                            BorrowHistory history = data.getValue(BorrowHistory.class);

                            if (history != null &&
                                    history.getToolId().equals(toolId) &&
                                    history.getStatus().equalsIgnoreCase("pending")) {
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

    public void getHistoryByUserId(String userId, Consumer<ArrayList<BorrowHistory>> callback) {
        mDatabase.child("history")
                .orderByChild("userId")
                .equalTo(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ArrayList<BorrowHistory> borrowHistoryList = new ArrayList<>();
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                BorrowHistory borrowHistory = snapshot.getValue(BorrowHistory.class);
                                if (borrowHistory != null) {
                                    borrowHistoryList.add(borrowHistory);
                                }
                            }
                            Collections.reverse(borrowHistoryList);
                        }
                        callback.accept(borrowHistoryList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.accept(new ArrayList<>());
                    }
                });
    }

    public void getAllPendingRequests(Consumer<ArrayList<BorrowHistory>> callback) {
        mDatabase.child("history")
                .orderByChild("status")
                .equalTo("pending")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<BorrowHistory> list = new ArrayList<>();

                        for (DataSnapshot data : snapshot.getChildren()) {
                            BorrowHistory request = data.getValue(BorrowHistory.class);
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

    public void getAllHistories(Consumer<ArrayList<BorrowHistory>> callback) {
        mDatabase.child("history").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<BorrowHistory> borrowHistoryList = new ArrayList<>();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        BorrowHistory borrowHistory = snapshot.getValue(BorrowHistory.class);
                        if (borrowHistory != null) {
                            borrowHistoryList.add(borrowHistory);
                        }
                    }
                    Collections.reverse(borrowHistoryList);
                }
                callback.accept(borrowHistoryList);
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
