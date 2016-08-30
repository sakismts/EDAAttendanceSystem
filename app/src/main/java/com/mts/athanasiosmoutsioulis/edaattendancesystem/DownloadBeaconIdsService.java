package com.mts.athanasiosmoutsioulis.edaattendancesystem;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
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
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class DownloadBeaconIdsService extends Service implements AttendanceModel.OnCheckAttendanceListener {
    HandlerThread mythread;
    Handler myHandler;
    AttendanceModel  model;
    private BeaconManager beaconManager;
    private Region region;
    int counter = 0;
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;
    private Lecture tmp_attendance_class;
    final static String MY_ACTION = "MY_ACTION";
    private int lecture_hour;
    private boolean notificationsend=false;
    int startid;




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

        //

        model.setCheckAttendanceListener(this);
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
                    String location= checkLecturesTime().toString().toLowerCase();
                    Log.i("Service", location);
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





                       if(!todayBeaconList.isEmpty() ){
                           for (com.mts.athanasiosmoutsioulis.edaattendancesystem.Beacon tmp_todayBeacon:todayBeaconList){
                               if (location.equals(tmp_todayBeacon.getLocation().toLowerCase())){
                                   for (Beacon tmp:list){
                                        String beaconUUID= tmp.getProximityUUID().toString().toLowerCase();
                                        String major = Integer.toString(tmp.getMajor());
                                        String minor = Integer.toString(tmp.getMinor());
                                       if (beaconUUID.equals(tmp_todayBeacon.getUUID().toString().toLowerCase()) && major.equals(tmp_todayBeacon.getMajor()) && minor.equals(tmp_todayBeacon.getMinor()) ){
                                           Log.i("Beacon", "Lecture detected at location :" + tmp_todayBeacon.getLocation());

                                           Calendar c = Calendar.getInstance();
                                           c.setTime(tmp_attendance_class.getEnd());
                                           lecture_hour = c.get(Calendar.HOUR_OF_DAY);

                                            beaconManager.stopRanging(region);
                                           waitToRestartBeaconSearch();

                                            model.setCurrentLecture(tmp_attendance_class);
                                           System.out.println(tmp_attendance_class);
                                           //////check for attendance////
                                           String user_id=sharedpreferences.getString("id", "User");
                                           String module_id=model.getCurrentLecture().getModule();
                                           String lecture_type=model.getCurrentLecture().getType();
                                           String lect_location=model.getCurrentLecture().getLocation();
                                           Calendar c_start = Calendar.getInstance();
                                           c_start.setTime(model.getCurrentLecture().getStart());
                                           String startDate=c_start.get(Calendar.DAY_OF_MONTH)+"/"+String.valueOf(c_start.get(Calendar.MONTH)+1)+"/"+c_start.get(Calendar.YEAR)+"T"+c_start.get(Calendar.HOUR_OF_DAY)+":"+c_start.get(Calendar.MINUTE);

                                           Calendar c_end = Calendar.getInstance();
                                           c_end.setTime(model.getCurrentLecture().getEnd());
                                           String endDate=c_end.get(Calendar.DAY_OF_MONTH)+"/"+String.valueOf(c_end.get(Calendar.MONTH)+1)+"/"+c_end.get(Calendar.YEAR)+"T"+c_end.get(Calendar.HOUR_OF_DAY)+":"+c_end.get(Calendar.MINUTE);

                                           String uri = "http://greek-tour-guides.eu/ioannina/dissertation/checkAttendance.php?student_id="+user_id+"&module_id="+module_id+"&lectureType="+lecture_type+"&location="+lect_location+"&startDate="+startDate+"&endDate="+endDate;
                                           Log.i("URI",uri.toString());
                                            model.CheckAttendance(uri);

                                       }

                                   }

                               }

                           }


                        }

                    }

                   //String current_lect_location= checkLecturesTime();

                    Log.i("Json: ", sharedpreferences.getString("BeaconList", "empty"));



                }
            }
        });
    }

    public void showNotification() {
        Intent notifyIntent = new Intent(getApplicationContext(), MainActivity.class);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivities(getApplicationContext(), 0,
                new Intent[] { notifyIntent }, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(getApplicationContext())
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Attendance")
                .setContentText("Please sign for "+tmp_attendance_class.getModule()+" at "+tmp_attendance_class.getLocation())
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();
        notification.defaults |= Notification.DEFAULT_SOUND;
        NotificationManager notificationManager =
                (NotificationManager)getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.startid = startId;
        Log.i("Service","Start");
         myHandler= new Handler(mythread.getLooper());

        myHandler.post(new Runnable() {
            @Override
            public void run() {

                beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
                    @Override
                    public void onServiceReady() {

                        beaconManager.startRanging(region);


                    }
                });

            }
        });

        return Service.START_REDELIVER_INTENT;
    }

    public void waitToRestartBeaconSearch(){
        int delay = 5000; // delay for 5 sec.
                int period = 1000; // repeat every sec.
       final Timer[] timer = {new Timer()};
                timer[0].scheduleAtFixedRate(new TimerTask() {
                    public void run() {
                        // Your code

                        counter++;
                        Log.i("Counter: ", Integer.toString(counter));

                        Calendar c = Calendar.getInstance();
                        int cur_hour = c.get(Calendar.HOUR_OF_DAY);
                        if (cur_hour == lecture_hour) {
                            timer[0].cancel();
                            timer[0] = null;
                            beaconManager.startRanging(region);
                            notificationsend=false;
                            if (model.getCheckAttendanceListener()==null)
                                setListener();


                        }
                        // Log.i("today ", "the size of today lectures is : " + Integer.toString(model.getLectures_list_today().size()));


                    }
                }, delay, period);


    }

    public void setListener(){
        model.setCheckAttendanceListener(this);
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
                    Log.i("Lecture", "Current lecture is at :" + tmp.getLocation());
                    Intent intent = new Intent();
                    intent.setAction(MY_ACTION);
                    intent.putExtra("Detected_Lecture", true);
                    sendBroadcast(intent);
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
        Log.v("SERVICE", "Service killed");
       beaconManager.stopRanging(region);


    }


    @Override
    public void onCheckAttendanceListener(boolean signed) {

        if(signed==false){
            Intent intent = new Intent();
            intent.setAction(MY_ACTION);
            intent.putExtra("Lecture", "Do you want to sign for the " + tmp_attendance_class.getType() + " of " + tmp_attendance_class.getModule() + " at " + tmp_attendance_class.getLocation());
            sendBroadcast(intent);
            if(!notificationsend){

            showNotification();
            notificationsend=true;
            }

        }else{
            if(tmp_attendance_class.getAttendance().equals("false")){
                String dateStart = DateFormat.format("yyyyMMdd'T'HHmmss'Z'", tmp_attendance_class.getStart()).toString();
                String dateEnd = DateFormat.format("yyyyMMdd'T'HHmmss'Z'", tmp_attendance_class.getEnd()).toString();
                try {
                    model.open();
                    model.updateAttendance(tmp_attendance_class.getTitle(),tmp_attendance_class.getModule(),tmp_attendance_class.getType(),dateStart,dateEnd,tmp_attendance_class.getLocation(),"true");
                    model.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private class MessageFromManualActivity extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            // TODO Auto-generated method stub

            String datapassed = arg1.getStringExtra("Lecture");


        }

    }
}


