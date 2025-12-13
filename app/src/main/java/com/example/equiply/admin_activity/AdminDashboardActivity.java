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
import com.example.equiply.shared_activity.ProfileActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.equiply.adapter.BrokenToolsAdapter;
import com.example.equiply.adapter.BorrowedToolsAdapter;
import com.example.equiply.helper.RealtimeDatabaseFirebase;
import com.google.firebase.auth.FirebaseAuth;
import com.example.equiply.model.Tool;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class AdminDashboardActivity extends AppCompatActivity {

    private BottomNavigationView adminNavView;
    private RealtimeDatabaseFirebase db;

    // Header items
    private TextView tvAdminName, tvTime, tvDate;

    // Stat cards
    private TextView tvTotalBorrowed, tvTotalBroken;
    private TextView tvSeeAllBroken, tvSeeAllBorrowed;

    // RecyclerView BROKEN TOOLS
    private RecyclerView rvBrokenTools;
    private BrokenToolsAdapter brokenToolsAdapter;
    private final ArrayList<Tool> brokenToolsList = new ArrayList<>();

    // RecyclerView BORROWED TOOLS
    private RecyclerView rvBorrowedTools;
    private BorrowedToolsAdapter borrowedToolsAdapter;
    private final ArrayList<Tool> borrowedToolsList = new ArrayList<>();

    private final Handler timeHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_dashboard);
        db = new RealtimeDatabaseFirebase(this);

        View root = findViewById(android.R.id.content);
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top,
                    systemBars.right, systemBars.bottom);
            return insets;
        });

        tvAdminName = findViewById(R.id.tvAdminName);
        tvTime = findViewById(R.id.tvTime);
        tvDate = findViewById(R.id.tvDate);

        tvTotalBorrowed = findViewById(R.id.tvTotalBorrowed);
        tvTotalBroken = findViewById(R.id.tvTotalBroken);

        rvBrokenTools = findViewById(R.id.rvBrokenTools);
        rvBorrowedTools = findViewById(R.id.rvBorrowedTools);
        adminNavView = findViewById(R.id.adminNavView);
        tvSeeAllBroken = findViewById(R.id.tvBrokenSeeMore);
        tvSeeAllBorrowed = findViewById(R.id.tvSeeAllBorrowed);

        tvSeeAllBroken.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, ToolListActivity.class);
            intent.putExtra("OPEN_FILTER", "RUSAK");
            startActivity(intent);
        });

        tvSeeAllBorrowed.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, ToolListActivity.class);
            intent.putExtra("FILTER_MODE", "BORROWED");
            startActivity(intent);
        });

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

            } else if (itemId == R.id.admin_add_item) {
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


        // Load data dan setup Recycler
        loadAdminName();
        loadStatistics();
        setupBrokenToolsRecycler();
        setupBorrowedToolsRecycler();
        loadDashboardTools();
        startLiveClock();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adminNavView.getSelectedItemId() != R.id.navigation_home){
            adminNavView.setSelectedItemId(R.id.navigation_home);
        }
    }

    private void loadAdminName() {
        String uid = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : null;

        if (uid == null) return;

        db.getUserByID(uid, user -> {
            if (user != null && user.getName() != null) {
                tvAdminName.setText(user.getName());
            } else {
                tvAdminName.setText("Admin");
            }
        });
    }

    private void loadStatistics() {
        db.getBorrowedToolsCount(count -> {
            tvTotalBorrowed.setText(String.valueOf(count));
        });

        db.getBrokenToolsCount(count -> {
            tvTotalBroken.setText(String.valueOf(count));
        });
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
    private void loadDashboardTools() {
        db.getAllTools(tools -> {

            if (tools == null || tools.isEmpty()) {
                brokenToolsAdapter.notifyDataSetChanged();
                borrowedToolsAdapter.notifyDataSetChanged();
                return;
            }

            brokenToolsList.clear();
            borrowedToolsList.clear();

            for (Tool tool : tools) {

                if (tool == null) continue;

                if ("Rusak".equalsIgnoreCase(tool.getToolStatus())) {
                    brokenToolsList.add(tool);
                }

                if ("Dipinjam".equalsIgnoreCase(tool.getStatus())) {
                    borrowedToolsList.add(tool);
                }
            }

            brokenToolsAdapter.notifyDataSetChanged();
            borrowedToolsAdapter.notifyDataSetChanged();
        });
    }



    private void setupBrokenToolsRecycler() {
        rvBrokenTools.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        brokenToolsAdapter = new BrokenToolsAdapter(brokenToolsList);
        rvBrokenTools.setAdapter(brokenToolsAdapter);
    }

    private void setupBorrowedToolsRecycler() {
        rvBorrowedTools.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        borrowedToolsAdapter = new BorrowedToolsAdapter(borrowedToolsList);
        rvBorrowedTools.setAdapter(borrowedToolsAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timeHandler.removeCallbacksAndMessages(null);
    }
}