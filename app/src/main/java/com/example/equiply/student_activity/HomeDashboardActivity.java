package com.example.equiply.student_activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.equiply.R;
import com.example.equiply.helper.AuthFirebase;
import com.example.equiply.helper.RealtimeDatabaseFirebase;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseUser;

public class HomeDashboardActivity extends AppCompatActivity {
    private AuthFirebase auth;
    private RealtimeDatabaseFirebase db;
    private BottomNavigationView bottomNavigationView;

    private TextView userName;

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

        db = new RealtimeDatabaseFirebase(this);
        auth = new AuthFirebase(this);

        userName = findViewById(R.id.userName);

        FirebaseUser firebaseUser = auth.getTheCurrentUser();
        db.getUserByID(firebaseUser.getUid(),user -> {
            if (user !=null){
                userName.setText(user.getName());
            }
        });

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


}