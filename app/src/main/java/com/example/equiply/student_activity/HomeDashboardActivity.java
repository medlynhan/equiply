package com.example.equiply.student_activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.equiply.BaseNavigationActivity;
import com.example.equiply.R;
import com.example.equiply.database.NotificationDA;
import com.example.equiply.helper.SessionManager;

public class HomeDashboardActivity extends BaseNavigationActivity {
    private SessionManager session;
    private TextView userName;
    private static final int NOTIFICATION_PERMISSION_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_dashboard);

        session = new SessionManager(this);

        userName = findViewById(R.id.userName);
        userName.setText(session.getName());
    }

    @Override
    protected void onStart() {
        super.onStart();
        handleNotification();
    }

    @Override
    protected int getNavigationMenuItemId() {
        return R.id.navigation_home;
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