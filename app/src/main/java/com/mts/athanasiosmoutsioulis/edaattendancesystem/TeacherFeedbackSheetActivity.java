package com.mts.athanasiosmoutsioulis.edaattendancesystem;

import android.app.FragmentManager;
import android.content.Intent;
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
}
