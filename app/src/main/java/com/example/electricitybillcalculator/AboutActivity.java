package com.example.electricitybillcalculator;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.graphics.Color;

public class AboutActivity extends AppCompatActivity {

    private Button btnBack;
    private TextView txtFullName, txtStudentId, txtCourse, txtGitHubLink, txtCopyright;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // Initialize views
        txtFullName = findViewById(R.id.txtFullName);
        txtStudentId = findViewById(R.id.txtStudentId);
        txtCourse = findViewById(R.id.txtCourse);
        txtGitHubLink = findViewById(R.id.txtGitHubLink);
        txtCopyright = findViewById(R.id.txtCopyright);
        btnBack = findViewById(R.id.btnBack);

        txtFullName.setText("Full Name: Nur Syaamin Syaamira Binti Mahmud");
        txtStudentId.setText("Student ID: 2025137201");
        txtCourse.setText("Course: ICT602 - Mobile Technology");
        txtGitHubLink.setText("https://github.com/Syaamin/ElectricityBillCalculator.git");
        txtCopyright.setText("© 2026 Syaamin. All Rights Reserved.");

        // Make GitHub link clickable
        txtGitHubLink.setClickable(true);
        txtGitHubLink.setFocusable(true);

        // Back button
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}