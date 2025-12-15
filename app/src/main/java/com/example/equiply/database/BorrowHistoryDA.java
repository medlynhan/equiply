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
        String historyId = mDatabase.child("history").push().getKey();

        if (historyId == null) {
            callback.accept(false);
            return;
        }

        borrowHistory.setId(historyId);

        mDatabase.child("history").child(historyId).setValue(borrowHistory).addOnCompleteListener(task -> {
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
                .equalTo("Pending")
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

    public void updateHistoryStatusToReturned(String userId, String toolId) {
        mDatabase.child("history")
                .orderByChild("userId")
                .equalTo(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot data : snapshot.getChildren()) {
                            String currentToolId = data.child("toolId").getValue(String.class);
                            String currentStatus = data.child("status").getValue(String.class);

                            if (toolId.equals(currentToolId) &&
                                    (currentStatus.equalsIgnoreCase("pending_return") ||
                                            currentStatus.equalsIgnoreCase("Approved") ||
                                            currentStatus.equalsIgnoreCase("Dipinjam"))) {
                                data.getRef().child("status").setValue("Returned");

                                data.getRef().child("actualReturnDate").setValue(System.currentTimeMillis());
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    public void approveRequest(String requestId, String toolId, String borrowerName, Consumer<Boolean> callback) {
        mDatabase.child("history")
                .child(requestId)
                .child("status")
                .setValue("Approved")
                .addOnSuccessListener(unused -> {

                    DatabaseReference toolRef = mDatabase.child("tools").child(toolId);
                    toolRef.child("status").setValue("Dipinjam");
                    toolRef.child("lastBorrower").setValue(borrowerName);

                    callback.accept(true);

                })
                .addOnFailureListener(e -> callback.accept(false));
    }

    public void rejectRequest(String requestId, Consumer<Boolean> callback) {
        mDatabase.child("history")
                .child(requestId)
                .child("status")
                .setValue("Rejected")
                .addOnSuccessListener(unused -> callback.accept(true))
                .addOnFailureListener(e -> callback.accept(false));
    }

}
