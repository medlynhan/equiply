package com.example.equiply.student_activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.equiply.BaseNavigationActivity;
import com.example.equiply.R;
import com.example.equiply.adapter.HistoryAdapter;
import com.example.equiply.helper.BorrowHistoryDA;
import com.example.equiply.helper.SessionManager;
import com.example.equiply.model.BorrowHistory;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;


public class HistoryActivity extends BaseNavigationActivity {
    private RecyclerView rvHistory;
    private HistoryAdapter historyAdapter;
    private BorrowHistoryDA borrowHistoryDA;
    private EditText etSearch;
    private ChipGroup chipGroupFilters;
    private Chip chipAll;
    private ArrayList<BorrowHistory> borrowHistoryList;
    private ArrayList<BorrowHistory> borrowHistoryListFull;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        initializeViews();

        borrowHistoryDA = new BorrowHistoryDA();

        setupRecyclerView();
        setupSearch();
        setupFilters();
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadHistories();
    }


    private void initializeViews() {
        rvHistory = findViewById(R.id.rvHistory);
        etSearch = findViewById(R.id.etSearch);
        chipGroupFilters = findViewById(R.id.chipGroupFilters);
        chipAll = findViewById(R.id.chipAll);
    }

    @Override
    protected int getNavigationMenuItemId() {
        return R.id.navigation_history;
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

            if (matchesSearch && matchesCategory) {
                filteredList.add(borrowHistory);
            }
        }
        historyAdapter.updateData(filteredList);
    }

}