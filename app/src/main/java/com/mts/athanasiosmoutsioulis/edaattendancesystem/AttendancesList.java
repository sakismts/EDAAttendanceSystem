package com.mts.athanasiosmoutsioulis.edaattendancesystem;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

public class AttendancesList extends AppCompatActivity implements AttendanceModel.OnGetTeacherAttendances{
    AttendanceModel model=AttendanceModel.getOurInstance();
    String module;
    private RecyclerView.Adapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendances_list);
        RecyclerView attendRecycler = (RecyclerView) findViewById(R.id.at_recycler_view);
        attendRecycler.setHasFixedSize(true);
        model.getTeacherAttendances().clear();
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mLayoutManager.scrollToPosition(0);
        attendRecycler.setLayoutManager(mLayoutManager);
        mAdapter = new AdapterTeacherAttendances(this);

        attendRecycler.setAdapter(mAdapter);
        model.setGetTeacherAttendancesListener(this);
        Intent intent = getIntent();
        module=intent.getStringExtra("module");
        String uri = "http://greek-tour-guides.eu/ioannina/dissertation/getTeacherAttendances.php?module_id="+ module;
        Log.i("URI", uri.toString());
        model.getTeacherAttendaces(uri);
    }

    @Override
    public void onGetTeacherAttendances(boolean signed) {
        mAdapter.notifyDataSetChanged();

    }
}
