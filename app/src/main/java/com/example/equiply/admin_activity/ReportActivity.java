package com.example.equiply.admin_activity;

import android.os.Bundle;

import com.example.equiply.BaseNavigationActivity;
import com.example.equiply.R;

public class ReportActivity extends BaseNavigationActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

    }

    @Override
    protected int getNavigationMenuItemId() {
        return R.id.admin_nav_report;
    }
}