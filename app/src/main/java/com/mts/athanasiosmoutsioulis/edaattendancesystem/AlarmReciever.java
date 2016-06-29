package com.mts.athanasiosmoutsioulis.edaattendancesystem;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.test.ActivityTestCase;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by AthanasiosMoutsioulis on 11/06/16.
 */
public class AlarmReciever extends BroadcastReceiver implements AttendanceModel.OnCheckBeaconListener
{
    AttendanceModel model;
    ArrayList<Beacon> myBeaconList = new ArrayList<Beacon>();
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;
    @Override
    public void onReceive(Context context, Intent intent)
    {
        // TODO Auto-generated method stub
        model= AttendanceModel.getOurInstance();
        if (model==null)
        model = new AttendanceModel(context);

        model.setCheckBeaconUpdateListener(this);
        load_today_lectures();
        myBeaconList.clear();
        sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        Log.i("Alarm","alarm");


        // Show the toast  like in above screen shot
        Toast.makeText(context, "Alarm Triggered", Toast.LENGTH_LONG).show();
    }

    private void load_today_lectures() {
        Log.i("Alarm", "download beacons UUID");
       model.load_today_lectures();
        if (!model.getLectures_list_today().isEmpty()){
        for (Lecture tmp: model.getLectures_list_today()){
            model.checkBeacon(tmp.getLocation());

        }
        Log.i("today ", "the size of today lectures is : " + Integer.toString(model.getLectures_list_today().size()));
        System.out.println("the size of today lectures is : " + model.getLectures_list_today().size());
        }
        //System.out.println("the location is:"+model.getLectures_list_today().get(0).getLocation());

    }

    @Override
    public void oncheckBeaconListener(String UUID, String major, String minor, String location) {

        System.out.println(UUID.toLowerCase() + " " + major + " " + minor + " " + location);
        com.mts.athanasiosmoutsioulis.edaattendancesystem.Beacon tmp_Beacon= new com.mts.athanasiosmoutsioulis.edaattendancesystem.Beacon(UUID.toLowerCase(),major,minor,location);
        myBeaconList.add(tmp_Beacon);

        JSONArray jsonArray = new JSONArray();
        for (int i=0; i < myBeaconList.size(); i++) {
            jsonArray.put(getJSONObject(myBeaconList.get(i)));
        }
        SharedPreferences.Editor prefsEditor = sharedpreferences.edit();
        prefsEditor.putString("BeaconList",jsonArray.toString());
        prefsEditor.commit();
        System.out.println(jsonArray.toString());


    }

    public JSONObject getJSONObject(com.mts.athanasiosmoutsioulis.edaattendancesystem.Beacon tmp_Beacon) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("UUID", tmp_Beacon.getUUID());
            obj.put("major", tmp_Beacon.getMajor());
            obj.put("minor", tmp_Beacon.getMinor());
            obj.put("location", tmp_Beacon.getLocation());
        } catch (JSONException e) {
            Log.i("JSONException: ", e.getMessage());
        }
        return obj;
    }
}
