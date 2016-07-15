package com.mts.athanasiosmoutsioulis.edaattendancesystem;

import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.ArrayList;

public class FeedBackList extends AppCompatActivity implements AdapterFeedback.MyListener, AttendanceModel.OnSendFeedBack{
    AttendanceModel model=AttendanceModel.getOurInstance();
    private RecyclerView.Adapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private Lecture tmp_lecture=null;
    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back_list);
        model.setFeedBackListener(this);

        try {
            model.opendb_read();
            model.readAttendances();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        RecyclerView attendRecycler = (RecyclerView) findViewById(R.id.fd_recycler_view);
        attendRecycler.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mLayoutManager.scrollToPosition(0);
        attendRecycler.setLayoutManager(mLayoutManager);
        mAdapter = new AdapterFeedback(this);

        attendRecycler.setAdapter(mAdapter);

    }

    @Override
    public void onupdateFeedback(String Uri,Lecture tmp_Lecture_feedback) {
        model.sendFeedBack(Uri);
        this.tmp_lecture=tmp_Lecture_feedback;

    }

    @Override
    public void onSendFeedBack() {
        Toast.makeText(this,"The Feedback was sent",Toast.LENGTH_SHORT).show();
        String dateStart = DateFormat.format("yyyyMMdd'T'HHmmss'Z'", this.tmp_lecture.getStart()).toString();
        String dateEnd = DateFormat.format("yyyyMMdd'T'HHmmss'Z'", this.tmp_lecture.getEnd()).toString();
        try {
            model.open();
            model.updateFeedback(this.tmp_lecture.getTitle(),this.tmp_lecture.getModule(),this.tmp_lecture.getType(),dateStart,dateEnd,this.tmp_lecture.getLocation(),"true");
            model.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            model.opendb_read();
            model.readAttendances();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        mAdapter.notifyDataSetChanged();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.android_action_bar_spinner_menu, menu);
        String[] state= {"All","January","February", "March", "April","May", "June","July","August","September","Oktober","November","December"};

        MenuItem item = menu.findItem(R.id.spinner);
        spinner = (Spinner) MenuItemCompat.getActionView(item);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               if (position==0){
                   try {
                       model.opendb_read();
                       model.readAttendances();
                   } catch (SQLException e) {
                       e.printStackTrace();
                   }
               }else{
                   try {
                       model.opendb_read();
                       model.readAttendances(position);
                   } catch (SQLException e) {
                       e.printStackTrace();
                   }
               }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
//                R.array.spinner_list_item_array, android.R.layout.simple_spinner_item);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,  android.R.layout.simple_spinner_item, state);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
        return true;
    }
}
