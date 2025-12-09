package com.example.equiply.student_activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.equiply.R;
import com.example.equiply.helper.AuthFirebase;
import com.example.equiply.helper.RealtimeDatabaseFirebase;
import com.example.equiply.helper.SessionManager;
import com.example.equiply.shared_activity.ToolListActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseUser;

public class HomeDashboardActivity extends AppCompatActivity {
    private SessionManager session;
    private BottomNavigationView bottomNavigationView;
    private MaterialCardView activeLoansCard, dueTodayCard;
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

        session = new SessionManager(this);
        activeLoansCard = findViewById(R.id.activeLoansCard);
        dueTodayCard = findViewById(R.id.dueTodayCard);
        userName = findViewById(R.id.userName);

        userName.setText(session.getName());

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(this::setBottomNavigationView);

        activeLoansCard.setOnClickListener(v -> {
            Intent intent = new Intent(HomeDashboardActivity.this, HistoryActivity.class);
            startActivity(intent);
        });

        dueTodayCard.setOnClickListener(v -> {
            Intent intent = new Intent(HomeDashboardActivity.this, NotificationActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bottomNavigationView.getSelectedItemId() != R.id.navigation_home){
            bottomNavigationView.setSelectedItemId(R.id.navigation_home);
        }
    }

    private boolean setBottomNavigationView(MenuItem item){
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
    }
}