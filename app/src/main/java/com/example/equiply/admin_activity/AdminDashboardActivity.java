package com.example.equiply.admin_activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.equiply.R;
import com.example.equiply.shared_activity.ToolListActivity;
import com.example.equiply.student_activity.ProfileActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.equiply.adapter.BrokenToolsAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class AdminDashboardActivity extends AppCompatActivity {

    private BottomNavigationView adminNavView;

    // Header items
    private TextView tvGreeting, tvAdminName, tvTime, tvDate;

    // Stat cards
    private TextView tvTotalBorrowed, tvTotalBroken;

    // RecyclerView
    private RecyclerView rvBrokenTools;
    private BrokenToolsAdapter brokenToolsAdapter;
    private final ArrayList<String> brokenToolsList = new ArrayList<>();

    private final Handler timeHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_dashboard);

        View root = findViewById(android.R.id.content);
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top,
                    systemBars.right, systemBars.bottom);
            return insets;
        });

        tvGreeting = findViewById(R.id.tvGreeting);
        tvAdminName = findViewById(R.id.tvAdminName);
        tvTime = findViewById(R.id.tvTime);
        tvDate = findViewById(R.id.tvDate);

        tvTotalBorrowed = findViewById(R.id.tvTotalBorrowed);
        tvTotalBroken = findViewById(R.id.tvTotalBroken);

        rvBrokenTools = findViewById(R.id.rvBrokenTools);
        adminNavView = findViewById(R.id.adminNavView);

        adminNavView.setSelectedItemId(R.id.admin_nav_home);

        adminNavView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Intent intent;

            if (itemId == R.id.admin_nav_home) {
                return true;

            } else if (itemId == R.id.admin_nav_tools) {
                intent = new Intent(AdminDashboardActivity.this, ToolListActivity.class);
                startActivity(intent);
                return true;

            } else if (itemId == R.id.admin_add_item) {   // use the correct menu id
                intent = new Intent(AdminDashboardActivity.this, AddToolActivity.class);
                startActivity(intent);
                return true;

            } else if (itemId == R.id.admin_nav_report) {
                // TODO: Open admin report page
                return true;

            } else if (itemId == R.id.admin_nav_profil) {
                intent = new Intent(AdminDashboardActivity.this, ProfileActivity.class);
                startActivity(intent);
                return true;
            }

            return false;
        });


        tvAdminName.setText("Admin123");
        tvTotalBorrowed.setText("3");
        tvTotalBroken.setText("3");

        loadBrokenToolsDummy();
        setupBrokenToolsRecycler();
        startLiveClock();
    }


    private void startLiveClock() {
        timeHandler.post(new Runnable() {
            @Override
            public void run() {
                String time = new SimpleDateFormat("h:mm a", Locale.getDefault())
                        .format(new Date());
                String date = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
                        .format(new Date());

                tvTime.setText(time);
                tvDate.setText(date);

                timeHandler.postDelayed(this, 1000);
            }
        });
    }


    private void loadBrokenToolsDummy() {
        brokenToolsList.clear();
        brokenToolsList.add("Obeng Rusak");
        brokenToolsList.add("Martil Gagang Patah");
        brokenToolsList.add("Kunci Inggris Aus");
    }


    private void setupBrokenToolsRecycler() {
        rvBrokenTools.setLayoutManager(new LinearLayoutManager(this));
        brokenToolsAdapter = new BrokenToolsAdapter(brokenToolsList);
        rvBrokenTools.setAdapter(brokenToolsAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timeHandler.removeCallbacksAndMessages(null);
    }
}
