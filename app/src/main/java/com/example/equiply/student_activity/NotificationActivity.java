package com.example.equiply.student_activity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.equiply.R;
import com.example.equiply.adapter.NotificationAdapter;
import com.example.equiply.helper.NotificationDA;
import com.example.equiply.helper.SessionManager;
import com.example.equiply.model.Notification;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {

    private NotificationDA notificationDA;
    private SessionManager sessionManager;
    private RecyclerView notifRV;
    private NotificationAdapter notifAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notification);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        notificationDA = new NotificationDA();
        sessionManager = new SessionManager(this);

        notifRV = findViewById(R.id.rvNotifications);
        notifRV.setLayoutManager(new LinearLayoutManager(this));

        loadData();
    }

    private void loadData() {
        notificationDA.getNotifications(sessionManager.getUserId(), new NotificationDA.NotificationCallback() {
            @Override
            public void onCallback(List<Notification> list) {
                notifAdapter = new NotificationAdapter(list);
                notifRV.setAdapter(notifAdapter);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(NotificationActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}