package com.mts.athanasiosmoutsioulis.edaattendancesystem;

import android.app.Service;
import android.content.Intent;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.os.Handler;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;


public class MyServiceTest extends Service {
    HandlerThread mythread;
    AttendanceModel model= AttendanceModel.getOurInstance();
    private BeaconManager beaconManager;
    private Region region;

    int counter=0;
    public MyServiceTest() {
    }

    @Override
    public void onCreate() {
        Log.i("Service","Created");
        super.onCreate();
        mythread=new HandlerThread("Mythread");
        mythread.start();
        beaconManager = new BeaconManager(MyApplication.getInstance());

        region = new Region("ranged region",
                UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"), null, null);
        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> list) {
                if (!list.isEmpty()) {
                    System.out.println(list);
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
        Handler myHandler= new Handler(mythread.getLooper());
        final ArrayList<Lecture> tmp= (ArrayList<Lecture>) intent.getSerializableExtra("list");

        System.out.print(intent.getStringExtra("test"));
        System.out.println(tmp);
        myHandler.post(new Runnable() {
            @Override
            public void run() {
                int delay = 5000; // delay for 5 sec.
                int period = 1000; // repeat every sec.
//                Timer timer = new Timer();
//                timer.scheduleAtFixedRate(new TimerTask() {
//                    public void run() {
//                        // Your code
//
//                        counter++;
//                        Log.i("Counter: ", Integer.toString(counter) + " " + tmp.get(0).getLocation());
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
        return Service.START_REDELIVER_INTENT;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
