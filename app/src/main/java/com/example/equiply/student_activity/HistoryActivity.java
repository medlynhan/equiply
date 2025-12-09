package com.example.equiply.student_activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.equiply.R;
import com.example.equiply.adapter.HistoryAdapter;
import com.example.equiply.helper.RealtimeDatabaseFirebase;
import com.example.equiply.helper.SessionManager;
import com.example.equiply.model.History;
import com.example.equiply.shared_activity.ToolListActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;


public class HistoryActivity extends AppCompatActivity {
    private RecyclerView rvHistory;
    private HistoryAdapter historyAdapter;
    private RealtimeDatabaseFirebase database;
    private EditText etSearch;
    private ChipGroup chipGroupFilters;
    private Chip chipAll;
    private BottomNavigationView bottomNavigationView;

    private ArrayList<History> historyList;
    private ArrayList<History> historyListFull;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        initializeViews();

        database = new RealtimeDatabaseFirebase(this);
        SessionManager session = new SessionManager(this);

        setupBottomNavigation();

        setupRecyclerView();
        setupSearch();
        setupFilters();
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadHistories();
        updateBottomNavigationSelection();
    }


    private void initializeViews() {
        rvHistory = findViewById(R.id.rvHistory);
        etSearch = findViewById(R.id.etSearch);
        chipGroupFilters = findViewById(R.id.chipGroupFilters);
        chipAll = findViewById(R.id.chipAll);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
    }

    private void setupBottomNavigation() {
        bottomNavigationView.getMenu().clear();
        bottomNavigationView.inflateMenu(R.menu.bottom_menu);
        bottomNavigationView.setSelectedItemId(R.id.navigation_history);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Intent intent;

            if (itemId == R.id.navigation_home) {
                intent = new Intent(this, HomeDashboardActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.navigation_history) {
                return true;
            } else if (itemId == R.id.navigation_box) {
                intent = new Intent(this, ToolListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.navigation_notification) {
                intent = new Intent(this, NotificationActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.navigation_profile) {
                intent = new Intent(this, ProfileActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return true;
            }
            return false;
        });
    }

    private void updateBottomNavigationSelection() {
        if (bottomNavigationView != null) {
            Menu menu = bottomNavigationView.getMenu();
            MenuItem item;

            item = menu.findItem(R.id.navigation_history);

            if (item != null) {
                item.setChecked(true);
            }
        }
    }

    private void setupRecyclerView() {
        historyList = new ArrayList<>();
        historyListFull = new ArrayList<>();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        rvHistory.setLayoutManager(gridLayoutManager);

        historyAdapter = new HistoryAdapter(this, historyList);
        rvHistory.setAdapter(historyAdapter);
    }

    private void loadHistories() {
        // get current userid
        SessionManager session = new SessionManager(this);
        String userId = session.getUserId();

        if (userId == null || userId.isEmpty()) {
            Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show();
            return;
        }

        database.getHistoryByUserId(userId, histories -> {
            if (histories != null && !histories.isEmpty()) {
                historyList.clear();
                historyListFull.clear();

                historyList.addAll(histories);
                historyListFull.addAll(histories);

                applyFilters();
            } else {
                Toast.makeText(this, "No history available", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilters();
            }
        });
    }

    private void setupFilters() {
        chipGroupFilters.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                chipAll.setChecked(true);
            }
            applyFilters();
        });
    }

    private void applyFilters() {
        String searchText = etSearch.getText().toString().toLowerCase().trim();
        int checkedChipId = chipGroupFilters.getCheckedChipId();

        ArrayList<History> filteredList = new ArrayList<>();

        for (History history : historyListFull) {
            boolean matchesSearch = history.getToolName().toLowerCase().contains(searchText);

            boolean matchesCategory = true;
            if (checkedChipId == R.id.chipActive) {
                matchesCategory = "Dipinjam".equalsIgnoreCase(history.getStatus());
            } else if (checkedChipId == R.id.chipReturned) {
                matchesCategory = "Dikembalikan".equalsIgnoreCase(history.getStatus());
            }
            // if chipAll is selected, matchesCategory remains true

            if (matchesSearch && matchesCategory) {
                filteredList.add(history);
            }
        }
        historyAdapter.updateData(filteredList);
    }

    private void filterHistory(int chipID) {
        ArrayList<History> filteredList = new ArrayList<>();
        
        if (chipID == R.id.chipAll) {
            filteredList.addAll(historyListFull);
        } else if (chipID == R.id.chipActive) {
            for (History history : historyListFull) {
                if ("Dipinjam".equalsIgnoreCase(history.getStatus())) {
                    filteredList.add(history);
                }
            }
        } else if (chipID == R.id.chipReturned) {
            for (History history : historyListFull) {
                if ("Dikembalikan".equalsIgnoreCase(history.getStatus())) {
                    filteredList.add(history);
                }
            }
        }
        historyAdapter.updateData(filteredList);
    }
}