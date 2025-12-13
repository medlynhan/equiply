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
import com.example.equiply.helper.BorrowHistoryDA;
import com.example.equiply.helper.SessionManager;
import com.example.equiply.model.BorrowHistory;
import com.example.equiply.shared_activity.ToolListActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;


public class HistoryActivity extends AppCompatActivity {
    private RecyclerView rvHistory;
    private HistoryAdapter historyAdapter;
    private BorrowHistoryDA borrowHistoryDA;
    private EditText etSearch;
    private ChipGroup chipGroupFilters;
    private Chip chipAll;
    private MaterialCardView statusBadge;
    private BottomNavigationView bottomNavigationView;

    private ArrayList<BorrowHistory> borrowHistoryList;
    private ArrayList<BorrowHistory> borrowHistoryListFull;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        initializeViews();

        borrowHistoryDA = new BorrowHistoryDA();

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
        statusBadge = findViewById(R.id.statusBadge);
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
        String type = getIntent().getStringExtra("HISTORY_TYPE");

        if ("BROKEN".equals(type)) {
            // tampilkan history alat rusak
            loadBrokenHistory();
        } else if ("BORROWED".equals(type)) {
            // tampilkan history peminjaman
            loadBorrowedHistory();
        } else {
            // default (misalnya buka history semua)
            loadAllHistory();
        }
    }
    private void loadBrokenHistory() {
        // nanti ambil data alat rusak
    }

    private void loadBorrowedHistory() {
        // nanti ambil data peminjaman
    }

    private void loadAllHistory() {
        // fallback
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
        borrowHistoryList = new ArrayList<>();
        borrowHistoryListFull = new ArrayList<>();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        rvHistory.setLayoutManager(gridLayoutManager);

        historyAdapter = new HistoryAdapter(this, borrowHistoryList);
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

        borrowHistoryDA.getHistoryByUserId(userId, histories -> {
            if (histories != null && !histories.isEmpty()) {
                borrowHistoryList.clear();
                borrowHistoryListFull.clear();

                borrowHistoryList.addAll(histories);
                borrowHistoryListFull.addAll(histories);

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

        ArrayList<BorrowHistory> filteredList = new ArrayList<>();

        for (BorrowHistory borrowHistory : borrowHistoryListFull) {
            boolean matchesSearch = borrowHistory.getToolName().toLowerCase().contains(searchText);

            boolean matchesCategory = true;
            if (checkedChipId == R.id.chipActive) {
                matchesCategory = "Approved".equalsIgnoreCase(borrowHistory.getStatus());
            } else if (checkedChipId == R.id.chipReturned) {
                matchesCategory = "Returned".equalsIgnoreCase(borrowHistory.getStatus());
            }
            // if chipAll is selected, matchesCategory remains true

            if (matchesSearch && matchesCategory) {
                filteredList.add(borrowHistory);
            }
        }
        historyAdapter.updateData(filteredList);
    }

    private void filterHistory(int chipID) {
        ArrayList<BorrowHistory> filteredList = new ArrayList<>();
        
        if (chipID == R.id.chipAll) {
            filteredList.addAll(borrowHistoryListFull);
        } else if (chipID == R.id.chipActive) {
            for (BorrowHistory borrowHistory : borrowHistoryListFull) {
                if ("Dipinjam".equalsIgnoreCase(borrowHistory.getStatus())) {
                    filteredList.add(borrowHistory);
                }
            }
        } else if (chipID == R.id.chipReturned) {
            for (BorrowHistory borrowHistory : borrowHistoryListFull) {
                if ("Dikembalikan".equalsIgnoreCase(borrowHistory.getStatus())) {
                    filteredList.add(borrowHistory);
                }
            }
        }
        historyAdapter.updateData(filteredList);
    }
}