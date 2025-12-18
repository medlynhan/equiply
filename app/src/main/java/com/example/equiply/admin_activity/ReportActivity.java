package com.example.equiply.admin_activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.equiply.BaseNavigationActivity;
import com.example.equiply.R;
import com.example.equiply.adapter.BorrowedToolsAdapter;
import com.example.equiply.adapter.BrokenToolsAdapter;
import com.example.equiply.database.ToolsDA;
import com.example.equiply.model.Tool;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class ReportActivity extends BaseNavigationActivity {

    private ToolsDA toolsDA;
    private TextView tvTotalTools, tvAvailableTools, tvBorrowedTools, tvBrokenTools;
    private RecyclerView rvBrokenReport, rvBorrowedReport;
    private TextView tvEmptyBorrowed, tvEmptyBroken;
    private TextInputEditText etSearchReport;
    private PieChart pieChart;
    private BrokenToolsAdapter brokenToolsAdapter;
    private BorrowedToolsAdapter borrowedToolsAdapter;
    private ArrayList<Tool> brokenToolsList;
    private ArrayList<Tool> borrowedToolsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        toolsDA = new ToolsDA(this);

        initializeViews();
        setupChart();
        setupRecyclerViews();
        setupSearch();
        loadReportData();
    }

    private void initializeViews() {
        tvTotalTools = findViewById(R.id.tvTotalTools);
        tvAvailableTools = findViewById(R.id.tvAvailableTools);
        tvBorrowedTools = findViewById(R.id.tvBorrowedTools);
        tvBrokenTools = findViewById(R.id.tvBrokenTools);
        rvBrokenReport = findViewById(R.id.rvBrokenReport);
        rvBorrowedReport = findViewById(R.id.rvBorrowedReport);
        tvEmptyBroken = findViewById(R.id.tvEmptyBroken);
        tvEmptyBorrowed = findViewById(R.id.tvEmptyBorrowed);
        pieChart = findViewById(R.id.pieChart);
        etSearchReport = findViewById(R.id.etSearchReport);
    }

    private void setupChart(){
        pieChart.getDescription().setEnabled(false);

        pieChart.setDragDecelerationFrictionCoef(0.95f);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleRadius(61f);
        pieChart.setHoleRadius(58f);

        pieChart.setDrawCenterText(true);
        pieChart.setCenterText("Status\nInventaris");
        pieChart.setCenterTextSize(16f);
        pieChart.setCenterTextColor(Color.parseColor("#333333"));

        pieChart.animateY(1000);

        pieChart.getLegend().setEnabled(true);
        pieChart.getLegend().setTextSize(12f);
        pieChart.getLegend().setFormSize(12f);
        pieChart.getLegend().setTextColor(Color.parseColor("#333333"));
    }

    private void setupRecyclerViews() {
        brokenToolsList = new ArrayList<>();
        borrowedToolsList = new ArrayList<>();

        brokenToolsAdapter = new BrokenToolsAdapter(brokenToolsList, tool -> openToolDetail(tool));
        rvBrokenReport.setLayoutManager(new LinearLayoutManager(this));
        rvBrokenReport.setAdapter(brokenToolsAdapter);

        borrowedToolsAdapter = new BorrowedToolsAdapter(borrowedToolsList, tool -> openToolDetail(tool));
        rvBorrowedReport.setLayoutManager(new LinearLayoutManager(this));
        rvBorrowedReport.setAdapter(borrowedToolsAdapter);
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

    private void setupSearch() {
        etSearchReport.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString();

                brokenToolsAdapter.filter(query);
                borrowedToolsAdapter.filter(query);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadReportData() {
        toolsDA.getAllTools(tools -> {
            if (tools != null) {
                int total = tools.size();
                int available = 0;
                int borrowed = 0;
                int broken = 0;

                ArrayList<Tool> tempBroken = new ArrayList<>();
                ArrayList<Tool> tempBorrowed = new ArrayList<>();

                for (Tool tool : tools) {
                    if ("Tersedia".equalsIgnoreCase(tool.getStatus())) {
                        available++;
                    } else if ("Dipinjam".equalsIgnoreCase(tool.getStatus())) {
                        borrowed++;
                        tempBorrowed.add(tool);
                    }

                    if ("Rusak".equalsIgnoreCase(tool.getToolStatus())) {
                        broken++;
                        tempBroken.add(tool);
                    }
                }

                if (tempBroken.isEmpty()) {
                    rvBrokenReport.setVisibility(View.GONE);
                    tvEmptyBroken.setVisibility(View.VISIBLE);
                } else {
                    rvBrokenReport.setVisibility(View.VISIBLE);
                    tvEmptyBroken.setVisibility(View.GONE);
                }

                if (tempBorrowed.isEmpty()) {
                    rvBorrowedReport.setVisibility(View.GONE);
                    tvEmptyBorrowed.setVisibility(View.VISIBLE);
                } else {
                    rvBorrowedReport.setVisibility(View.VISIBLE);
                    tvEmptyBorrowed.setVisibility(View.GONE);
                }

                tvTotalTools.setText(String.valueOf(total));
                tvAvailableTools.setText(String.valueOf(available));
                tvBorrowedTools.setText(String.valueOf(borrowed));
                tvBrokenTools.setText(String.valueOf(broken));

                brokenToolsAdapter.updateList(tempBroken);
                borrowedToolsAdapter.updateList(tempBorrowed);

                updatePieChartData(available, borrowed, broken);
            } else {
                rvBrokenReport.setVisibility(View.GONE);
                tvEmptyBroken.setVisibility(View.VISIBLE);
                rvBorrowedReport.setVisibility(View.GONE);
                tvEmptyBorrowed.setVisibility(View.VISIBLE);
            }
        });
    }

    private void updatePieChartData(int available, int borrowed, int broken) {
        ArrayList<PieEntry> entries = new ArrayList<>();

        if (available > 0) entries.add(new PieEntry(available, "Tersedia"));
        if (borrowed > 0) entries.add(new PieEntry(borrowed, "Dipinjam"));
        if (broken > 0) entries.add(new PieEntry(broken, "Rusak"));

        if (entries.isEmpty()) {
            pieChart.clear();
            return;
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        ArrayList<Integer> colors = new ArrayList<>();
        if (available > 0) colors.add(Color.parseColor("#4CAF50"));
        if (borrowed > 0) colors.add(Color.parseColor("#FF9800"));
        if (broken > 0) colors.add(Color.parseColor("#D32F2F"));
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setValueTextSize(14f);
        data.setValueTextColor(Color.WHITE);

        data.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);
            }
        });

        pieChart.setData(data);
        pieChart.invalidate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadReportData();
    }

    @Override
    protected int getNavigationMenuItemId() {
        return R.id.admin_nav_report;
    }
}