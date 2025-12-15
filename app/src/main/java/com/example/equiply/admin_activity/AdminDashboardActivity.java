package com.example.equiply.admin_activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.equiply.BaseNavigationActivity;
import com.example.equiply.R;
import com.example.equiply.database.UserDA;
import com.example.equiply.shared_activity.ToolListActivity;
import com.example.equiply.adapter.BrokenToolsAdapter;
import com.example.equiply.adapter.BorrowedToolsAdapter;
import com.example.equiply.database.ToolsDA;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.example.equiply.model.Tool;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class AdminDashboardActivity extends BaseNavigationActivity {
    private ToolsDA toolsDA;
    private UserDA userDA;
    // Header items
    private TextView tvAdminName, tvTime, tvDate;

    private MaterialCardView btnApproval;

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

        toolsDA = new ToolsDA(this);
        userDA = new UserDA();

        tvAdminName = findViewById(R.id.tvAdminName);
        tvTime = findViewById(R.id.tvTime);
        tvDate = findViewById(R.id.tvDate);
        btnApproval = findViewById(R.id.btnApproval);

        btnApproval.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminApprovalActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });

        tvTotalBorrowed = findViewById(R.id.tvTotalBorrowed);
        tvTotalBroken = findViewById(R.id.tvTotalBroken);

        rvBrokenTools = findViewById(R.id.rvBrokenTools);
        rvBorrowedTools = findViewById(R.id.rvBorrowedTools);
        tvSeeAllBroken = findViewById(R.id.tvBrokenSeeMore);
        tvSeeAllBorrowed = findViewById(R.id.tvSeeAllBorrowed);

        tvSeeAllBroken.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, ToolListActivity.class);
            intent.putExtra("OPEN_FILTER", "RUSAK");
            startActivity(intent);
            overridePendingTransition(0, 0);
        });

        tvSeeAllBorrowed.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, ToolListActivity.class);
            intent.putExtra("FILTER_MODE", "BORROWED");
            startActivity(intent);
            overridePendingTransition(0, 0);
        });

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
        loadStatistics();
        loadDashboardTools();
    }

    private void loadAdminName() {
        String uid = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : null;

        if (uid == null) return;

        userDA.getUserByID(uid, user -> {
            if (user != null && user.getName() != null) {
                tvAdminName.setText(user.getName());
            } else {
                tvAdminName.setText("Admin");
            }
        });
    }

    private void loadStatistics() {
        toolsDA.getBorrowedToolsCount(count -> {
            tvTotalBorrowed.setText(String.valueOf(count));
        });

        toolsDA.getBrokenToolsCount(count -> {
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
        toolsDA.getAllTools(tools -> {

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
        brokenToolsAdapter = new BrokenToolsAdapter(brokenToolsList, tool -> openToolDetail(tool));
        rvBrokenTools.setAdapter(brokenToolsAdapter);
    }

    private void setupBorrowedToolsRecycler() {
        rvBorrowedTools.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        borrowedToolsAdapter = new BorrowedToolsAdapter(borrowedToolsList, tool -> openToolDetail(tool));
        rvBorrowedTools.setAdapter(borrowedToolsAdapter);
    }

    private void openToolDetail(Tool tool) {
        Intent intent = new Intent(this, AdminToolDetailActivity.class);
        intent.putExtra("TOOL_ID", tool.getId());
        intent.putExtra("TOOL_NAME", tool.getName());
        intent.putExtra("TOOL_DESCRIPTION", tool.getDescription());
        intent.putExtra("TOOL_STATUS", tool.getStatus());
        intent.putExtra("TOOL_CONDITION", tool.getToolStatus());
        intent.putExtra("TOOL_IMAGE_URL", tool.getImageUrl());
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timeHandler.removeCallbacksAndMessages(null);
    }

    @Override
    protected int getNavigationMenuItemId() {
        return R.id.admin_nav_home;
    }
}