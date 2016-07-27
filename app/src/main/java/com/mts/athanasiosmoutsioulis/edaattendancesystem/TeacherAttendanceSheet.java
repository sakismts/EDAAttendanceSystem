package com.mts.athanasiosmoutsioulis.edaattendancesystem;

import android.app.FragmentManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

public class TeacherAttendanceSheet extends AppCompatActivity {
    String moduleId;
    String startDate;
    String endDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_attendance_sheet);
        Intent intent = getIntent();
        moduleId=intent.getStringExtra("moduleID");
        startDate=intent.getStringExtra("startDate");
        endDate=intent.getStringExtra("endDate");
        FragmentManager fragmentManager= getFragmentManager();
        AttendanceSheetFragment fragment= (AttendanceSheetFragment) fragmentManager.findFragmentById(R.id.list_studentsids_fragment);
        fragment.updateStudentAttendances(moduleId,startDate,endDate);


    }


}
