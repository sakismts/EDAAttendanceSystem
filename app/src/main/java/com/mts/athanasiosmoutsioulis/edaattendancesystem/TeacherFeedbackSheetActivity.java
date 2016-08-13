package com.mts.athanasiosmoutsioulis.edaattendancesystem;

import android.Manifest;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class TeacherFeedbackSheetActivity extends AppCompatActivity {
    String moduleId;
    String startDate;
    String endDate;
    String moduleType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_feedback_sheet);
        Intent intent = getIntent();
        moduleId=intent.getStringExtra("moduleID");
        startDate=intent.getStringExtra("startDate");
        endDate=intent.getStringExtra("endDate");
        moduleType=intent.getStringExtra("moduleType");
        FragmentManager fragmentManager= getFragmentManager();
        TeacherFeedbackSheetFragment fragment= (TeacherFeedbackSheetFragment) fragmentManager.findFragmentById(R.id.list_studentsFD_fragment);
        fragment.updateStudentAttendances(moduleId,moduleType, startDate, endDate);

    }

    @Override
    protected void onResume() {
        super.onResume();
        int hasReadExternalPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (hasReadExternalPermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                    123);
            return;
        }
        int hasWriteExternalPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasWriteExternalPermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    124);
            return;
        }
    }
}
