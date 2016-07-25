package com.mts.athanasiosmoutsioulis.edaattendancesystem;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

public class AvailableModules extends AppCompatActivity implements AttendanceModel.OnGetModulesListener{
    AttendanceModel model=AttendanceModel.getOurInstance();
    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedpreferences;
    private LinearLayoutManager mLayoutManager;
    AdapterAvailableModules mAdapter;
    Set<String> myModules;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available_modules);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        model.setGetModulesListener(this);
        RecyclerView modulesRecycler = (RecyclerView) findViewById(R.id.recycler_view);
        modulesRecycler.setHasFixedSize(true);


        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mLayoutManager.scrollToPosition(0);
        modulesRecycler.setLayoutManager(mLayoutManager);
        mAdapter = new AdapterAvailableModules(this);
        modulesRecycler.setAdapter(mAdapter);
        String uri = "http://greek-tour-guides.eu/ioannina/dissertation/getModules.php";
        Log.i("URI", uri.toString());
        model.getmodules(uri);
    }

    @Override
    public void getModules(boolean modules) {
        mAdapter.notifyDataSetChanged();


    }

    @Override
    public void onBackPressed() {

//        myModules=sharedpreferences.getStringSet("myModules", new HashSet<String>());
//        System.out.println("the size is " +myModules.size());
        System.out.println(getJSONArray(model.getMyModules()));
        SharedPreferences.Editor editor=sharedpreferences.edit();
        editor.putString("MyModulesString",getJSONArray(model.getMyModules()).toString());
        editor.commit();
        finish();
    }

    public JSONObject getJSONArray(Set tmp) {
        JSONArray JsonArray=new JSONArray();
        for(String tmpString:model.getMyModules()){
            JSONObject obj = new JSONObject();
            try {
                obj.put("moduleID", tmpString);
            } catch (JSONException e) {
                Log.i("JSONException: ", e.getMessage());
            }
            JsonArray.put(obj);
        }
        JSONObject modulesObj = new JSONObject();
        try {
            modulesObj.put("modules", JsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return modulesObj;
    }
}
