package com.mts.athanasiosmoutsioulis.edaattendancesystem;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MyModules extends AppCompatActivity {
    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedpreferences;
    TextView tv_nothing;

    private LinearLayoutManager mLayoutManager;
    AdapterMyModules mAdapter;
    Spinner spinner;
    private AttendanceModel model = AttendanceModel.getOurInstance();

    @Override
    protected void onResume() {
        super.onResume();
        if (model.getMyModules().isEmpty()){

            tv_nothing.setVisibility(View.VISIBLE);
        }else{
            tv_nothing.setVisibility(View.GONE);
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_modules);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent availableModules=new Intent(getApplication(),AvailableModules.class);
                startActivity(availableModules);
            }
        });
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);


        tv_nothing=(TextView)findViewById(R.id.tv_modules);
        if (model.getMyModules().isEmpty()){

            tv_nothing.setVisibility(View.VISIBLE);
        }else{
            tv_nothing.setVisibility(View.GONE);
        }

        RecyclerView modulesRecycler = (RecyclerView) findViewById(R.id.recycler_view);
        modulesRecycler.setHasFixedSize(true);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mLayoutManager.scrollToPosition(0);
        modulesRecycler.setLayoutManager(mLayoutManager);
        mAdapter = new AdapterMyModules(this);
        modulesRecycler.setAdapter(mAdapter);
    }

    @Override
    public void onBackPressed() {
        System.out.println("Back the size is: " + model.getMyModules().size());
        finish();
    }


}
