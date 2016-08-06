package com.mts.athanasiosmoutsioulis.edaattendancesystem;

import android.Manifest;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.estimote.sdk.SystemRequirementsChecker;

public class TeacherAttendanceSheet extends AppCompatActivity {
    String moduleId;
    String startDate;
    String endDate;


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
