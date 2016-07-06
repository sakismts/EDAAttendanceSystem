package com.mts.athanasiosmoutsioulis.edaattendancesystem;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Messenger;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.facebook.internal.LockOnGetVariable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class ManualAttendance extends AppCompatActivity implements AttendanceModel.OnCheckAttendanceListener, AttendanceModel.OnSignAttendanceListener {
    private BeaconManager beaconManager;
    AttendanceModel  model;
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;
    private Lecture tmp_attendance_class;
    private Region region;
    CardView lecture;
    public TextView txtHeader, txtDate, txtTime, txtLocation;
    public ImageView type;
    Button btn_attendance;
    private final static int FADE_DURATION = 800; // in milliseconds
    private ProgressDialog Attendanceprogressdialog;
    final static String MY_ACTION = "MY_ACTION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_attendance);
        beaconManager = new BeaconManager(this);
        model= AttendanceModel.getOurInstance();

        sharedpreferences = getApplicationContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
         lecture = (CardView) findViewById(R.id.cv);
        lecture.setVisibility(View.GONE);
        region = new Region("ranged region",
                UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"), null, null);
        Attendanceprogressdialog=new ProgressDialog(this);
        Attendanceprogressdialog.setMessage("Sign Attendance, please wait.");
        model.setSignAttendanceListener(this);



        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<com.estimote.sdk.Beacon> list) {
                if (!list.isEmpty()) {
                    System.out.println(list);
                    // Deploy the Beacons that belongs to todays lectures
                    ArrayList<Beacon> todayBeaconList = new ArrayList<com.mts.athanasiosmoutsioulis.edaattendancesystem.Beacon>();
                    String BeaconKJsonListString = sharedpreferences.getString("BeaconList", "empty");
                    if (!BeaconKJsonListString.equals("empty")) {
                        try {
                            JSONArray jsonArrayBeacon = new JSONArray(BeaconKJsonListString);
                            for (int i = 0; i < jsonArrayBeacon.length(); i++) {
                                JSONObject object = jsonArrayBeacon.getJSONObject(i);
                                todayBeaconList.add(new com.mts.athanasiosmoutsioulis.edaattendancesystem.Beacon(object.getString("UUID"), object.getString("major"), object.getString("minor"), object.getString("location")));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        String location = checkLecturesTime().toString().toLowerCase();
                        Log.i("Service", location);


                        if (!todayBeaconList.isEmpty()) {
                            for (com.mts.athanasiosmoutsioulis.edaattendancesystem.Beacon tmp_todayBeacon : todayBeaconList) {
                                if (location.equals(tmp_todayBeacon.getLocation().toLowerCase())) {
                                    for (com.estimote.sdk.Beacon tmp : list) {
                                        String beaconUUID = tmp.getProximityUUID().toString().toLowerCase();
                                        String major = Integer.toString(tmp.getMajor());
                                        String minor = Integer.toString(tmp.getMinor());
                                        if (beaconUUID.equals(tmp_todayBeacon.getUUID().toString().toLowerCase()) && major.equals(tmp_todayBeacon.getMajor()) && minor.equals(tmp_todayBeacon.getMinor())) {
                                            Log.i("Beacon", "Lecture detected at location :" + tmp_todayBeacon.getLocation());



                                            beaconManager.stopRanging(region);


                                            System.out.println(tmp_attendance_class);
                                            //////check for attendance////
                                            String user_id = sharedpreferences.getString("id", "User");
                                            String module_id = tmp_attendance_class.getModule();
                                            String lecture_type = tmp_attendance_class.getType();
                                            String lect_location = tmp_attendance_class.getLocation();
                                            Calendar c_start = Calendar.getInstance();
                                            c_start.setTime(tmp_attendance_class.getStart());
                                            String startDate = c_start.get(Calendar.DAY_OF_MONTH) + "/" + String.valueOf(c_start.get(Calendar.MONTH) + 1) + "/" + c_start.get(Calendar.YEAR) + "T" + c_start.get(Calendar.HOUR_OF_DAY) + ":" + c_start.get(Calendar.MINUTE);

                                            Calendar c_end = Calendar.getInstance();
                                            c_end.setTime(tmp_attendance_class.getEnd());
                                            String endDate = c_end.get(Calendar.DAY_OF_MONTH) + "/" + String.valueOf(c_end.get(Calendar.MONTH) + 1) + "/" + c_end.get(Calendar.YEAR) + "T" + c_end.get(Calendar.HOUR_OF_DAY) + ":" + c_end.get(Calendar.MINUTE);

                                            String uri = "http://greek-tour-guides.eu/ioannina/dissertation/checkAttendance.php?student_id=" + user_id + "&module_id=" + module_id + "&lectureType=" + lecture_type + "&location=" + lect_location + "&startDate=" + startDate + "&endDate=" + endDate;
                                            Log.i("URI", uri.toString());
                                            model.CheckAttendance(uri);

                                        }

                                    }

                                }

                            }


                        }

                    }

                    //String current_lect_location= checkLecturesTime();

                    Log.i("Manual Json: ", sharedpreferences.getString("BeaconList", "empty"));


                }
            }
        });

    }

    public String checkLecturesTime(){
        Calendar c = Calendar.getInstance();
        int c_day = c.get(Calendar.DAY_OF_MONTH);
        int c_month = c.get(Calendar.MONTH)+1;
        int c_year = c.get(Calendar.YEAR);
        int c_hour = c.get(Calendar.HOUR_OF_DAY);
        int c_seconds = c.get(Calendar.SECOND);

        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        for (Lecture tmp: model.getLectures_list_today()){
            c1.setTime(tmp.getStart());
            c2.setTime(tmp.getEnd());
            int day = c1.get(Calendar.DAY_OF_MONTH);
            int month = c1.get(Calendar.MONTH)+1;
            int year = c1.get(Calendar.YEAR);

            if (day==c_day && month==c_month && year==c_year) {
                int c_hour1 = c1.get(Calendar.HOUR_OF_DAY);

                int c_hour2 = c2.get(Calendar.HOUR_OF_DAY);

                if (c_hour>=c1.get(Calendar.HOUR_OF_DAY) && c_hour< c2.get(Calendar.HOUR_OF_DAY)){
                    System.out.println("Day :"+c_day+ " - " +day+", Month :"+c_month+ " - " +month+", Hour :"+c_hour+" - "+c1.get(Calendar.HOUR_OF_DAY));
                    tmp_attendance_class=tmp;
                    Log.i("Lecture","Current lecture is at :"+tmp.getLocation());

                    return tmp.getLocation();
                }
            }
        }
        return "noLocation";

    }

    @Override
    public void onCheckAttendanceListener(boolean attendace) {

        if(!attendace){
        Log.i("Lecture", "No attendance");
            lecture.setVisibility(View.VISIBLE);
            txtHeader = (TextView)findViewById(R.id.tv_title);
            txtDate = (TextView)findViewById(R.id.tv_date);
            txtTime = (TextView)findViewById(R.id.tv_time);
            txtLocation = (TextView)findViewById(R.id.tv_location);
            type = (ImageView)findViewById(R.id.lecture_icon);
            btn_attendance=(Button)findViewById(R.id.btn_attendance);

            btn_attendance.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Attendanceprogressdialog.show();
                    String user_id=sharedpreferences.getString("id", "User");
                    String module_id=tmp_attendance_class.getModule();
                    String lecture_type=tmp_attendance_class.getType();
                    String location=tmp_attendance_class.getLocation();
                    Calendar c_start = Calendar.getInstance();
                    c_start.setTime(tmp_attendance_class.getStart());
                    String startDate=c_start.get(Calendar.DAY_OF_MONTH)+"/"+String.valueOf(c_start.get(Calendar.MONTH)+1)+"/"+c_start.get(Calendar.YEAR)+"T"+c_start.get(Calendar.HOUR_OF_DAY)+":"+c_start.get(Calendar.MINUTE);

                    Calendar c_end = Calendar.getInstance();
                    c_end.setTime(tmp_attendance_class.getEnd());
                    String endDate=c_end.get(Calendar.DAY_OF_MONTH)+"/"+String.valueOf(c_end.get(Calendar.MONTH)+1)+"/"+c_end.get(Calendar.YEAR)+"T"+c_end.get(Calendar.HOUR_OF_DAY)+":"+c_end.get(Calendar.MINUTE);

                    String uri = "http://greek-tour-guides.eu/ioannina/dissertation/insert_attendance.php?student_id="+user_id+"&module_id="+module_id+"&lectureType="+lecture_type+"&location="+location+"&startDate="+startDate+"&endDate="+endDate+"&valid=false";
                    Log.i("URI",uri.toString());
                    model.signAttendance(uri);
                }
            });

            txtHeader.setText(tmp_attendance_class.getType() + " " + tmp_attendance_class.getTitle()+" "+ tmp_attendance_class.getModule());
            txtLocation.setText(tmp_attendance_class.getLocation());
            //date
            String dayOfTheWeek = (String) android.text.format.DateFormat.format("EEEE", tmp_attendance_class.getStart());//Thursday
            String stringMonth = (String) android.text.format.DateFormat.format("MMM", tmp_attendance_class.getStart()); //Jun
            String intMonth = (String) android.text.format.DateFormat.format("MM", tmp_attendance_class.getStart()); //06
            String year = (String) android.text.format.DateFormat.format("yyyy", tmp_attendance_class.getStart()); //2013
            String day = (String) android.text.format.DateFormat.format("dd", tmp_attendance_class.getStart()); //20
            txtDate.setText(day + "/" + intMonth + "/" + year);
            //start time
            Calendar c1 = Calendar.getInstance();
            c1.setTime(tmp_attendance_class.getStart());
            String start_hour= Integer.toString(c1.get(Calendar.HOUR_OF_DAY));
            // String start_hour = (String) android.text.format.DateFormat.format("HH", Lectureitems.getStart());
            String start_minutes = (String) android.text.format.DateFormat.format("mm", tmp_attendance_class.getStart());
            //end time
            Calendar c2 = Calendar.getInstance();
            c2.setTime(tmp_attendance_class.getEnd());
            String end_hour= Integer.toString(c2.get(Calendar.HOUR_OF_DAY));
            //String end_hour = (String) android.text.format.DateFormat.format("HH", Lectureitems.getEnd());
            String end_minutes = (String) android.text.format.DateFormat.format("mm", tmp_attendance_class.getEnd());
            txtTime.setText(start_hour + ":" + start_minutes + " - " + end_hour + ":" + end_minutes);

            if (tmp_attendance_class.getType().equals("WORKSHOP"))
                type.setImageResource(R.drawable.computer_lab);
            else
                type.setImageResource(R.drawable.lecture_icon);

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        model.setCheckAttendanceListener(this);
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {

                beaconManager.startRanging(region);


            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("Manual","Stopped");
        model.setCheckAttendanceListener(null);

    }

    @Override
    public void onSignAttendanceListener(boolean signed) {
        Log.i("Manual","Attendance");
        Attendanceprogressdialog.dismiss();
        if(signed==true){
            btn_attendance.setEnabled(false);
            btn_attendance.setBackgroundColor(Color.GRAY);
            final Dialog okDialog = new Dialog(this);
            okDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            LayoutInflater inflater = this.getLayoutInflater();
            View view = inflater.inflate(R.layout.image_layout, null);
            okDialog.setContentView(view);
            okDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            okDialog.show();
            setScaleAnimation(view);
            final Handler handler  = new Handler();
            final Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    if (okDialog.isShowing()) {
                        okDialog.dismiss();
                    }
                }
            };

            okDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    handler.removeCallbacks(runnable);
                }
            });

            handler.postDelayed(runnable, 1000);
        }
    }
    private void setScaleAnimation(View view) {
        ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(FADE_DURATION);
        view.startAnimation(anim);
    }
}
