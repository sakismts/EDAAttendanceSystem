package com.mts.athanasiosmoutsioulis.edaattendancesystem;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class DownloadBeaconIdsService extends Service {
    HandlerThread mythread;
    AttendanceModel  model;
    private BeaconManager beaconManager;
    private Region region;
    int counter = 0;
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;
    private Lecture tmp_attendance_class;

    public DownloadBeaconIdsService() {

    }

    @Override
    public void onCreate() {
        Log.i("Service", "Created");
        super.onCreate();
        mythread=new HandlerThread("Mythread");
        sharedpreferences = getApplicationContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        mythread.start();
        beaconManager = new BeaconManager(MyApplication.getInstance());
        model= AttendanceModel.getOurInstance();
        if (model==null)
       model = new AttendanceModel(this);
        model.load_today_lectures();

        region = new Region("ranged region",
                UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"), null, null);
        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> list) {
                if (!list.isEmpty()) {
                    System.out.println(list);
                    // Deploy the Beacons that belongs to todays lectures
                    ArrayList<com.mts.athanasiosmoutsioulis.edaattendancesystem.Beacon> todayBeaconList= new ArrayList<com.mts.athanasiosmoutsioulis.edaattendancesystem.Beacon>();
                    String BeaconKJsonListString=sharedpreferences.getString("BeaconList", "empty");
                    if (!BeaconKJsonListString.equals("empty")){
                        try {
                            JSONArray jsonArrayBeacon = new JSONArray(BeaconKJsonListString);
                           for (int i=0;i<jsonArrayBeacon.length();i++){
                               JSONObject object = jsonArrayBeacon.getJSONObject(i);
                               todayBeaconList.add(new com.mts.athanasiosmoutsioulis.edaattendancesystem.Beacon(object.getString("UUID"),object.getString("major"),object.getString("minor"),object.getString("location")));
                           }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        String location= checkLecturesTime().toString().toLowerCase();
                        Log.i("Service",location);

                       if(!todayBeaconList.isEmpty()){
                           for (com.mts.athanasiosmoutsioulis.edaattendancesystem.Beacon tmp_todayBeacon:todayBeaconList){
                               if (location.equals(tmp_todayBeacon.getLocation().toLowerCase())){
                                   for (Beacon tmp:list){
                                        String beaconUUID= tmp.getProximityUUID().toString().toLowerCase();
                                        String major = Integer.toString(tmp.getMajor());
                                        String minor = Integer.toString(tmp.getMinor());
                                       if (beaconUUID.equals(tmp_todayBeacon.getUUID().toString().toLowerCase()) && major.equals(tmp_todayBeacon.getMajor()) && minor.equals(tmp_todayBeacon.getMinor()) ){
                                           Log.i("Beacon","Lecture detected at location :"+tmp_todayBeacon.getLocation());
                                       }

                                   }

                               }

                           }
//                            for (Beacon tmp:list){
//                                String beaconUUID= tmp.getProximityUUID().toString().toLowerCase();
//                                String major = Integer.toString(tmp.getMajor());
//                                String minor = Integer.toString(tmp.getMinor());
//                               for (com.mts.athanasiosmoutsioulis.edaattendancesystem.Beacon tmp_todayBeacon:todayBeaconList){
//                                   if (beaconUUID.equals(tmp_todayBeacon.getUUID().toString().toLowerCase()) && major.equals(tmp_todayBeacon.getMajor()) && minor.equals(tmp_todayBeacon.getMinor()) && location.toString().toLowerCase().equals(tmp_todayBeacon.getLocation().toLowerCase())){
//                                       Log.i("Beacon","Lecture detected at location :"+tmp_todayBeacon.getLocation());
//
//                                   }
//
//                               }
//
//                            }

                        }

                    }



//                    for (Beacon tmp : list){
//                    String beaconUUID= tmp.getProximityUUID().toString().toLowerCase();
//            if (beaconUUID.equals(tmp.getProximityUUID().toString()) && major.equals(Integer.toString(tmp.getMajor())) && minor.equals(Integer.toString(tmp.getMinor()))){
//                System.out.println("Verified");
//                showAlertDialog();
//                //showNotification(
////                        "Attendance!",
////                        "Please sign in for the lesson in the lecture hall "
////                                + location);
//                found_flag=true;
//
//            }
//        }

                   //String current_lect_location= checkLecturesTime();

                    Log.i("Json: ", sharedpreferences.getString("BeaconList", "empty"));
                    //  Beacon nearestBeacon = list.get(0);

                    // List<String> places = placesNearBeacon(nearestBeacon);
                    // TODO: update the UI here
                    //Log.d("Airport", "Nearest places: " + places);
                    // beaconManager.stopRanging(region);
                    // String location = checkLecturesTime();
                    //System.out.println(location);
//                            if (location.equals("noLocation"))
//                                beaconManager.startRanging(region);
//                            else {
//                                setTmpBeaconList(list);
//                                model.checkBeacon(location);
//
//                            }


                }
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("Service","Start");
        Toast.makeText(getApplicationContext(),"started",Toast.LENGTH_SHORT).show();
        Handler myHandler= new Handler(mythread.getLooper());

        myHandler.post(new Runnable() {
            @Override
            public void run() {
//                int delay = 5000; // delay for 5 sec.
//                int period = 1000; // repeat every sec.
//                Timer timer = new Timer();
//                timer.scheduleAtFixedRate(new TimerTask() {
//                    public void run() {
//                        // Your code
//
//                        counter++;
//                        Log.i("Counter: ", Integer.toString(counter));
//
//                       // Log.i("today ", "the size of today lectures is : " + Integer.toString(model.getLectures_list_today().size()));
//
//
//                    }
//                }, delay, period);
                beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
                    @Override
                    public void onServiceReady() {
                        beaconManager.startRanging(region);
                    }
                });

            }
        });

//
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
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
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Intent restartService = new Intent(getApplicationContext(),
                this.getClass());
        restartService.setPackage(getPackageName());
        PendingIntent restartServicePI = PendingIntent.getService(
                getApplicationContext(), 1, restartService,
                PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmService = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 1000, restartServicePI);

    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }


}


