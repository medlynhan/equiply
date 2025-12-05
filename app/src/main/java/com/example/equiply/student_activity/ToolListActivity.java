package com.example.equiply.student_activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.equiply.R;
import com.example.equiply.adapter.ToolAdapter;
import com.example.equiply.helper.RealtimeDatabaseFirebase;
import com.example.equiply.model.Tool;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class ToolListActivity extends AppCompatActivity {

    private RecyclerView rvTools;
    private ToolAdapter toolAdapter;
    private RealtimeDatabaseFirebase database;
    private EditText etSearch;
    private FloatingActionButton fabQrCode;
    private BottomNavigationView bottomNavigationView;
    private ArrayList<Tool> toolList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tool_list);

        initializeViews();
        database = new RealtimeDatabaseFirebase(this);

        setupRecyclerView();
        loadTools();
        setupSearch();
        setupFabQrCode();
        setupBottomNavigation();
    }

    private void initializeViews() {
        rvTools = findViewById(R.id.rvTools);
        etSearch = findViewById(R.id.etSearch);
        fabQrCode = findViewById(R.id.fabQrCode);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
    }

    private void setupRecyclerView() {
        toolList = new ArrayList<>();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        rvTools.setLayoutManager(gridLayoutManager);

        toolAdapter = new ToolAdapter(this, toolList, tool -> {
            openToolDetail(tool);
        });

        rvTools.setAdapter(toolAdapter);
    }

    private void loadTools() {
        // TODO: add a progress bar maybe
        database.getAllTools(tools -> {
            if (tools != null && !tools.isEmpty()) {
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

    private void setupFabQrCode() {
        fabQrCode.setOnClickListener(v -> {
            // TODO: open QR code scanner activity
            // Intent intent = new Intent(this, QRCodeScannerActivity.class);
            // startActivity(intent);
            Toast.makeText(this, "QR Code Scanner - Coming Soon!", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupBottomNavigation() {
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Intent intent;
            if(itemId == R.id.navigation_home){
                intent = new Intent(ToolListActivity.this, HomeDashboardActivity.class);
                startActivity(intent);
                return true;

            } else if (itemId == R.id.navigation_history) {
                intent = new Intent(ToolListActivity.this, HistoryActivity.class);
                startActivity(intent);
                return true;

            } else if (itemId == R.id.navigation_box) {
                return true;

            } else if (itemId == R.id.navigation_notification) {
                intent = new Intent(ToolListActivity.this, NotificationActivity.class);
                startActivity(intent);
                return true;

            } else if (itemId == R.id.navigation_profile) {
                intent = new Intent(ToolListActivity.this, ProfileActivity.class);
                startActivity(intent);
                return true;
            }

            return false;

        });
    }

    private void openToolDetail(Tool tool) {
        // TODO: navigate to tool detail activity
        // Intent intent = new Intent(this, ToolDetailActivity.class);
        // intent.putExtra("TOOL_ID", tool.getId());
        // intent.putExtra("TOOL_NAME", tool.getName());
        // startActivity(intent);

        Toast.makeText(this, "Clicked: " + tool.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTools();
    }
}