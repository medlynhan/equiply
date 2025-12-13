package com.example.equiply.student_activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.equiply.R;
import com.example.equiply.helper.NotificationDA;
import com.example.equiply.helper.SessionManager;
import com.example.equiply.shared_activity.ProfileActivity;
import com.example.equiply.shared_activity.ToolListActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeDashboardActivity extends AppCompatActivity {
    private SessionManager session;
    private BottomNavigationView bottomNavigationView;

    private TextView userName;

    private static final int NOTIFICATION_PERMISSION_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_dashboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        session = new SessionManager(this);

        userName = findViewById(R.id.userName);

        userName.setText(session.getName());

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Intent intent;
            if(itemId == R.id.navigation_home){
                return true;

            } else if (itemId == R.id.navigation_history) {
                intent = new Intent(HomeDashboardActivity.this, HistoryActivity.class);
                startActivity(intent);
                return true;

            } else if (itemId == R.id.navigation_box) {
                intent = new Intent(HomeDashboardActivity.this, ToolListActivity.class);
                startActivity(intent);
                return true;

            } else if (itemId == R.id.navigation_notification) {
                intent = new Intent(HomeDashboardActivity.this, NotificationActivity.class);
                startActivity(intent);
                return true;

            } else if (itemId == R.id.navigation_profile) {
                intent = new Intent(HomeDashboardActivity.this, ProfileActivity.class);
                startActivity(intent);
                return true;
            }

            return false;

        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        handleNotification();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bottomNavigationView.getSelectedItemId() != R.id.navigation_home){
            bottomNavigationView.setSelectedItemId(R.id.navigation_home);
        }
    }

    private void handleNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if(ActivityCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_CODE);
            } else {
                checkForDueLoan();
            }
        } else {
            checkForDueLoan();
        }
    }

    private void checkForDueLoan() {
        String userId = session.getUserId();
        if (userId != null) {
            NotificationDA dao = new NotificationDA();
            dao.checkAndNotify(this, userId);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkForDueLoan();
            } else {
                checkForDueLoan();
                Toast.makeText(this, "Notifications disabled. Check history for alerts.", Toast.LENGTH_LONG).show();
            }
        }
    }
}