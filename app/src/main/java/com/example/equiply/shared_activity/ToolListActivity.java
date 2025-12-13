package com.example.equiply.shared_activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.equiply.R;
import com.example.equiply.adapter.ToolAdapter;
import com.example.equiply.admin_activity.AddToolActivity;
import com.example.equiply.admin_activity.AdminDashboardActivity;
import com.example.equiply.admin_activity.AdminToolDetailActivity;
import com.example.equiply.helper.QRCodeScanner;
import com.example.equiply.helper.RealtimeDatabaseFirebase;
import com.example.equiply.helper.SessionManager;
import com.example.equiply.model.Tool;
import com.example.equiply.student_activity.HistoryActivity;
import com.example.equiply.student_activity.HomeDashboardActivity;
import com.example.equiply.student_activity.NotificationActivity;
import com.example.equiply.student_activity.ToolDetailActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;

public class ToolListActivity extends AppCompatActivity {
    private RecyclerView rvTools;
    private ToolAdapter toolAdapter;
    private RealtimeDatabaseFirebase database;
    private EditText etSearch;
    private ChipGroup chipGroupFilters;
    private Chip chipAll;
    private FloatingActionButton fabQrCode;
    private BottomNavigationView bottomNavigationView;

    private ArrayList<Tool> toolList;
    private ArrayList<Tool> toolListFull;

    private boolean isAdmin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tool_list);

        initializeViews();
        database = new RealtimeDatabaseFirebase(this);
        SessionManager session = new SessionManager(this);

        isAdmin = session.isAdmin();

        if (isAdmin) {
            setupAdminUI();
        } else {
            setupStudentUI();
        }

        setupRecyclerView();
        setupSearch();
        setupFilters();
        setupFabQrCode();
        loadTools();
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadTools();
        updateBottomNavigationSelection();
    }

    private void initializeViews() {
        rvTools = findViewById(R.id.rvTools);
        etSearch = findViewById(R.id.etSearch);
        chipGroupFilters = findViewById(R.id.chipGroupFilters);
        chipAll = findViewById(R.id.chipAll);
        fabQrCode = findViewById(R.id.fabQrCode);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        chipGroupFilters.setVisibility(View.GONE);
    }

    private void setupStudentUI() {
        bottomNavigationView.getMenu().clear();
        bottomNavigationView.inflateMenu(R.menu.bottom_menu);
        bottomNavigationView.setSelectedItemId(R.id.navigation_box);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Intent intent;

            if (itemId == R.id.navigation_home) {
                intent = new Intent(this, HomeDashboardActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.navigation_history) {
                intent = new Intent(this, HistoryActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.navigation_box) {
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

    private void setupAdminUI() {
        chipGroupFilters.setVisibility(View.VISIBLE);

        bottomNavigationView.getMenu().clear();
        bottomNavigationView.inflateMenu(R.menu.admin_menu);
        bottomNavigationView.setSelectedItemId(R.id.admin_nav_tools);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Intent intent;

            if (itemId == R.id.admin_nav_home) {
                intent = new Intent(this, AdminDashboardActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.admin_nav_tools) {
                return true;
            } else if (itemId == R.id.admin_add_item) {
                intent = new Intent(this, AddToolActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.admin_nav_report) {
                // intent = new Intent(this, ReportActivity.class);
                // startActivity(intent);
                return true;
            } else if (itemId == R.id.admin_nav_profil) {
                intent = new Intent(this, ProfileActivity.class);
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

            if (isAdmin) {
                item = menu.findItem(R.id.admin_nav_tools);
            } else {
                item = menu.findItem(R.id.navigation_box);
            }

            if (item != null) {
                item.setChecked(true);
            }
        }
    }

    private void setupRecyclerView() {
        toolList = new ArrayList<>();
        toolListFull = new ArrayList<>();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        rvTools.setLayoutManager(gridLayoutManager);

        toolAdapter = new ToolAdapter(this, toolList, this::openToolDetail);

        rvTools.setAdapter(toolAdapter);
    }

    private void loadTools() {
        database.getAllTools(tools -> {
            if (tools != null && !tools.isEmpty()) {
                toolList.clear();
                toolListFull.clear();

                if (isAdmin) {
                    toolList.addAll(tools);
                    toolListFull.addAll(tools);
                } else {
                    for (Tool tool : tools) {
                        if ("Tersedia".equalsIgnoreCase(tool.getStatus().trim())) {
                            toolList.add(tool);
                            toolListFull.add(tool);
                        }
                    }
                }

                toolAdapter.updateData(toolList);
                int checkedId = chipGroupFilters.getCheckedChipId();
                if (checkedId != -1) {
                    filterTools(checkedId);
                }

            } else {
                Toast.makeText(this, "No tools available", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (toolAdapter != null) {
                    toolAdapter.filter(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupFilters() {
        chipGroupFilters.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                chipAll.setChecked(true);
                return;
            }
            filterTools(checkedIds.get(0));
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
        }
        toolAdapter.updateData(filteredList);
    }

    private void setupFabQrCode() {
        fabQrCode.setOnClickListener(v -> {
            IntentIntegrator intentIntegrator = new IntentIntegrator(this);

            intentIntegrator.setCaptureActivity(QRCodeScanner.class);
            intentIntegrator.setPrompt("Scan a tool QR Code");
            intentIntegrator.setOrientationLocked(false);
            intentIntegrator.setBeepEnabled(false);
            intentIntegrator.initiateScan();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (intentResult != null) {
            if (intentResult.getContents() == null) {
                Toast.makeText(getBaseContext(), "Cancelled", Toast.LENGTH_SHORT).show();
            } else {
                String toolId = intentResult.getContents();

                if (toolId.contains(".") || toolId.contains("#") || toolId.contains("$") ||
                        toolId.contains("[") || toolId.contains("]") || toolId.contains("/")) {
                    Toast.makeText(getBaseContext(), "Invalid QR Code format.", Toast.LENGTH_SHORT).show();
                    return;
                }

                database.getToolById(toolId, tool -> {
                    if (tool != null) {
                        openToolDetail(tool);
                    } else {
                        Toast.makeText(getBaseContext(), "QR is not Valid", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void openToolDetail(Tool tool) {
        Intent intent;
        if (isAdmin) {
            intent = new Intent(this, AdminToolDetailActivity.class);
            intent.putExtra("TOOL_ID", tool.getId());
            intent.putExtra("TOOL_NAME", tool.getName());
            intent.putExtra("TOOL_DESCRIPTION", tool.getDescription());
            intent.putExtra("TOOL_STATUS", tool.getStatus());
            intent.putExtra("TOOL_CONDITION", tool.getToolStatus());
            intent.putExtra("TOOL_IMAGE_URL", tool.getImageUrl());
        } else {
            intent = new Intent(this, ToolDetailActivity.class);
            intent.putExtra("TOOL_ID", tool.getId());
            intent.putExtra("TOOL_NAME", tool.getName());
        }
        startActivity(intent);
    }
}