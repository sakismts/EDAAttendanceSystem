package com.mts.athanasiosmoutsioulis.edaattendancesystem;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

public class TeacherAttendanceSheet extends AppCompatActivity implements AttendanceModel.OnGetTeacherSingleAttendance{
    String moduleId;
    String startDate;
    String endDate;
    AttendanceModel model=AttendanceModel.getOurInstance();
    private RecyclerView.Adapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        moduleId=intent.getStringExtra("moduleID");
        startDate=intent.getStringExtra("startDate");
        endDate=intent.getStringExtra("endDate");
        setContentView(R.layout.activity_teacher_attendance_sheet);
        model.setGetTeacherSingleAttendanceListener(this);
        String uri = "http://greek-tour-guides.eu/ioannina/dissertation/getTeacherSingleAttendances.php?module_id="+ moduleId+"&startDate="+startDate+"&endDate="+endDate;
        Log.i("URI", uri.toString());
        model.getStudents_Attendance_list().clear();
        model.getTeacherSingleAttendace(uri);

        RecyclerView attendRecycler = (RecyclerView) findViewById(R.id.sheet_recycler_view);
        attendRecycler.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mLayoutManager.scrollToPosition(0);
        attendRecycler.setLayoutManager(mLayoutManager);
        mAdapter = new AdapterAttendanceSheet(this);
        attendRecycler.addItemDecoration(new DividerItemDecoration(getResources()));
        attendRecycler.setAdapter(mAdapter);
    }

    @Override
    public void onGetTeacherSingleAttendance(boolean signed) {
        mAdapter.notifyDataSetChanged();

    }
}
