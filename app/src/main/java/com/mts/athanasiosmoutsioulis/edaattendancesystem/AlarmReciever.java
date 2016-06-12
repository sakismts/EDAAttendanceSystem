package com.mts.athanasiosmoutsioulis.edaattendancesystem;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.sql.SQLException;

/**
 * Created by AthanasiosMoutsioulis on 11/06/16.
 */
public class AlarmReciever extends BroadcastReceiver
{
    AttendanceModel model = AttendanceModel.getOurInstance();
    @Override
    public void onReceive(Context context, Intent intent)
    {
        // TODO Auto-generated method stub

        load_today_lectures();


        // here you can start an activity or service depending on your need
        // for ex you can start an activity to vibrate phone or to ring the phone

        String message="Hi I will be there later, See You soon";// message to send
        Log.i("alarm","alarm");

        // Show the toast  like in above screen shot
        Toast.makeText(context, "Alarm Triggered and SMS Sent", Toast.LENGTH_LONG).show();
    }

    private void load_today_lectures() {
        try {
            model.opendb_read();
            model.read_db_today();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("the location is:"+model.getLectures_list_today().get(0).getLocation());

    }

}
