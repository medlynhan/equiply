package com.example.equiply.admin_activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.equiply.R;
import com.example.equiply.adapter.ToolAdapter;
import com.example.equiply.helper.RealtimeDatabaseFirebase;
import com.example.equiply.model.Tool;
import com.example.equiply.student_activity.HistoryActivity;
import com.example.equiply.student_activity.HomeDashboardActivity;
import com.example.equiply.student_activity.NotificationActivity;
import com.example.equiply.student_activity.ProfileActivity;
import com.example.equiply.student_activity.ToolListActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;

public class AdminToolListActivity extends AppCompatActivity {

    private RecyclerView rvTools;
    private ToolAdapter toolAdapter;
    private RealtimeDatabaseFirebase database;
    private EditText etSearch;
    private ChipGroup chipGroupFilters;
    private Chip chipAll;
    private BottomNavigationView adminNavigationView;
    private ArrayList<Tool> toolList;
    private ArrayList<Tool> toolListFull;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_tool_list);

        initializeViews();
        database = new RealtimeDatabaseFirebase(this);

        setupRecyclerView();
        loadTools();
        setupSearch();
        setupFilters();
        setupBottomNavigation();
    }

    private void initializeViews() {
        rvTools = findViewById(R.id.rvTools);
        etSearch = findViewById(R.id.etSearch);
        chipGroupFilters = findViewById(R.id.chipGroupFilters);
        chipAll = findViewById(R.id.chipAll);
        adminNavigationView = findViewById(R.id.adminNavView);
    }

    private void setupRecyclerView() {
        toolList = new ArrayList<>();
        toolListFull = new ArrayList<>();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        rvTools.setLayoutManager(gridLayoutManager);

        toolAdapter = new ToolAdapter(this, toolList, tool -> {
            openToolDetail(tool);
        });

        rvTools.setAdapter(toolAdapter);
    }

    private void loadTools() {
        database.getAllTools(tools -> {
            if (tools != null && !tools.isEmpty()) {
                toolList.clear();
                toolList.addAll(tools);
                toolListFull.clear();
                toolListFull.addAll(tools);
                toolAdapter.updateData(tools);
            } else {
                Toast.makeText(this, "No tools available", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // not needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (toolAdapter != null) {
                    toolAdapter.filter(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // not needed
            }
        });
    }

    private void setupFilters() {
        chipGroupFilters.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                chipAll.setChecked(true);
                return;
            }

            int checkedId = checkedIds.get(0);
            filterTools(checkedId);
        });
    }

    private void filterTools(int chipId) {
        ArrayList<Tool> filteredList = new ArrayList<>();

        if (chipId == R.id.chipAll) {
            filteredList.addAll(toolListFull);
        } else if (chipId == R.id.chipGoodCondition) {
            for (Tool tool : toolListFull) {
                if ("Baik".equalsIgnoreCase(tool.getToolStatus())) {
                    filteredList.add(tool);
                }
            }
        } else if (chipId == R.id.chipBadCondition) {
            for (Tool tool : toolListFull) {
                if ("Rusak".equalsIgnoreCase(tool.getToolStatus())) {
                    filteredList.add(tool);
                }
            }
        } else if (chipId == R.id.chipAll) {
            for (Tool tool : toolListFull) {
                if ("Tersedia".equalsIgnoreCase(tool.getStatus())) {
                    filteredList.add(tool);
                }
            }
        }

        toolAdapter.updateData(filteredList);
    }

    private void setupBottomNavigation() {
        adminNavigationView.setSelectedItemId(R.id.admin_nav_tools);
        adminNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Intent intent;
            if(itemId == R.id.admin_nav_home){
                intent = new Intent(AdminToolListActivity.this, AdminDashboardActivity.class);
                startActivity(intent);
                return true;

            } else if (itemId == R.id.admin_nav_tools) {
                return true;

            } else if (itemId == R.id.admin_nav_addTools) {
                intent = new Intent(AdminToolListActivity.this, AddToolActivity.class);
                startActivity(intent);
                return true;

            } else if (itemId == R.id.admin_nav_report) {
                //TODO: make intent for report
//                intent = new Intent(AdminToolListActivity.this, ReportActivity.class);
//                startActivity(intent);
                return true;

            } else if (itemId == R.id.admin_nav_profil) {
                intent = new Intent(AdminToolListActivity.this, ProfileActivity.class);
                startActivity(intent);
                return true;
            }

            return false;

        });
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
    protected void onResume() {
        super.onResume();
        loadTools();
    }
}