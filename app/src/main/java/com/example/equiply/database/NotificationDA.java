package com.example.equiply.database;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.equiply.model.Notification;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class NotificationDA {
    private final DatabaseReference mDatabase;

    public NotificationDA() {
        this.mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public interface NotificationCallback {
        void onCallback(List<Notification> list);
        void onError(String error);
    }

    public void addNotification(String userId, String toolName, String loanId, String borrowId) {
        String notificationId = mDatabase.child("users").child(userId).child("notifications").push().getKey();

        if (notificationId == null) return;

        Map<String, Object> notif = new HashMap<>();
        notif.put("title", "Return Reminder");
        notif.put("message", "Your borrowed '" + toolName + "' is due soon.");
        notif.put("timestamp", System.currentTimeMillis());
        notif.put("loanId", loanId);
        notif.put("borrowId", borrowId);

        mDatabase.child("users")
                .child(userId)
                .child("notifications")
                .child(notificationId)
                .setValue(notif)
                .addOnSuccessListener(aVoid -> {
                    System.out.println("Notification added successfully");
                })
                .addOnFailureListener(e -> {
                    System.err.println("Error adding notification: " + e.getMessage());
                });
    }

    public void getNotifications (String userId, final NotificationCallback callback) {
        mDatabase.child("users").child(userId).child("notifications")
                .orderByChild("timestamp")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Notification> notifList = new ArrayList<>();

                        for (DataSnapshot data : snapshot.getChildren()) {
                            String title = data.child("title").getValue(String.class);
                            String message = data.child("message").getValue(String.class);
                            Long timeLong = data.child("timestamp").getValue(Long.class);
                            String dateStr = "";
                            if (timeLong != null) {
                                Date date = new Date(timeLong);
                                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault());
                                dateStr = sdf.format(date);
                            }
                            String borrowId = data.child("borrowId").getValue(String.class);

                            notifList.add(new Notification(title, message, dateStr, borrowId));
                        }

                        Collections.reverse(notifList);

                        callback.onCallback(notifList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onError(error.getMessage());
                    }
                });
    }

    public void deleteNotificationByBorrowId(String userId, String borrowId) {
        if (userId == null || borrowId == null) return;

        mDatabase.child("users").child(userId).child("notifications")
                .orderByChild("borrowId")
                .equalTo(borrowId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot data : snapshot.getChildren()) {
                            data.getRef().removeValue()
                                    .addOnSuccessListener(aVoid -> System.out.println("Notification deleted for borrowId: " + borrowId))
                                    .addOnFailureListener(e -> System.err.println("Failed to delete notification: " + e.getMessage()));
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    public void checkAndNotify(Context context, String userId) {
        long now = System.currentTimeMillis();
        long twoDaysInMillis = 2L * 24 * 60 * 60 * 1000;

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        mDatabase.child("history").orderByChild("userId").equalTo(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot loan : snapshot.getChildren()) {
                            String status = loan.child("status").getValue(String.class);
                            String returnDateStr = loan.child("returnDate").getValue(String.class);
                            String toolName = loan.child("toolName").getValue(String.class);
                            String borrowId = loan.child("id").getValue(String.class);

                            String loanId = loan.getKey();

                            Boolean notified = loan.child("isNotified").getValue(Boolean.class);

                            if ("approved".equalsIgnoreCase(status) && returnDateStr != null && (notified == null || !notified)) {
                                Date returnDate = null;
                                try {
                                    returnDate = sdf.parse(returnDateStr);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                    continue;
                                }
                                if (returnDate != null) {
                                    long returnDateMillis = returnDate.getTime();
                                    long diff = returnDateMillis - now;

                                    if (diff < twoDaysInMillis) {
                                        addNotification(userId, toolName, loanId, borrowId);
                                        showSystemNotification(context, toolName);
                                        loan.getRef().child("isNotified").setValue(true);
                                    }
                                }

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
    }

    private void showSystemNotification(Context context, String toolName) {
        android.app.NotificationManager notificationManager = (android.app.NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        String channelId = "loan_alerts";

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            android.app.NotificationChannel channel = new android.app.NotificationChannel(
                    channelId, "Loan Alerts", android.app.NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        androidx.core.app.NotificationCompat.Builder builder =
                new androidx.core.app.NotificationCompat.Builder(context, channelId)
                        .setSmallIcon(android.R.drawable.ic_dialog_alert)
                        .setContentTitle("Tool Return Reminder")
                        .setContentText("Please return: " + toolName)
                        .setPriority(androidx.core.app.NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true);

        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }
}
