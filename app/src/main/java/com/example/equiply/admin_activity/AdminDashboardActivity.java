package com.example.equiply.admin_activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.equiply.R;
import com.example.equiply.student_activity.HistoryActivity;
import com.example.equiply.student_activity.ProfileActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

public class AdminDashboardActivity extends AppCompatActivity {

    private MaterialButton goToAddToolPage;
    private BottomNavigationView adminNavView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_dashboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        goToAddToolPage = findViewById(R.id.goToAddToolPage);
        goToAddToolPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminDashboardActivity.this,AddToolActivity.class);
                startActivity(intent);
            }
        });

        adminNavView = findViewById(R.id.adminNavView);
        adminNavView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Intent intent;
            if(itemId == R.id.admin_nav_home){
                return true;

            } else if (itemId == R.id.admin_nav_tools) {
                intent = new Intent(AdminDashboardActivity.this, AdminToolListActivity.class);
                startActivity(intent);
                return true;

            } else if (itemId == R.id.admin_nav_addTools) {
                intent = new Intent(AdminDashboardActivity.this, AddToolActivity.class);
                startActivity(intent);
                return true;

            } else if (itemId == R.id.admin_nav_report) {
                // TODO: add new class for report page
//                intent = new Intent(AdminDashboardActivity.this, AdminReportActivity.class);
//                startActivity(intent);
                return true;

            } else if (itemId == R.id.admin_nav_profil) {
                intent = new Intent(AdminDashboardActivity.this, ProfileActivity.class);
                startActivity(intent);
                return true;
            }

            return false;

        });

    }


}