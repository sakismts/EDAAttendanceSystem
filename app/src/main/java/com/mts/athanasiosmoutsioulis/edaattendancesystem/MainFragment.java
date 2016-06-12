package com.mts.athanasiosmoutsioulis.edaattendancesystem;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.SystemRequirementsChecker;
import com.facebook.Profile;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class MainFragment extends Fragment implements AttendanceModel.OnCheckBeaconListener {
    TextView id;
    TextView fbName;
    ImageView photo_profile;
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;
    AttendanceModel model = AttendanceModel.getOurInstance();
    private OnFragmentInteractionListener mListener;
    private BeaconManager beaconManager;
    private Region region;
    private List<Beacon> tmpBeaconList;
    private Lecture tmp_attendance_class;
    public List<Beacon> getTmpBeaconList() {
        return tmpBeaconList;
    }

    public void setTmpBeaconList(List<Beacon> tmpBeaconList) {
        this.tmpBeaconList = tmpBeaconList;
    }



    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_main, container, false);
        sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        photo_profile = (ImageView)view.findViewById(R.id.img_fbPhoto);
        id = (TextView)view.findViewById(R.id.tv_id);
        fbName = (TextView)view.findViewById(R.id.tv_fbName);
        //read lectures for today
        try {
            model.opendb_read();
            model.read_db_today();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        model.close();
        System.out.println("todays count" + model.getLectures_list_today().size());
        model.setCheckBeaconUpdateListener(this);
        LinearLayout lectures = (LinearLayout)view.findViewById(R.id.ll_lectures);
        lectures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    model.opendb_read();
                    model.read_db();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                model.close();
                System.out.println(model.getLectures_list().size());
                Intent schedule = new Intent(getActivity(), ScheduleActivity.class);
                startActivity(schedule);
                /*for (Lecture tmp : model.getLectures_list()){
                    System.out.println("START EVENT");
                    System.out.println(tmp.getTitle());
                    System.out.println(tmp.getModule());
                    System.out.println(tmp.getType());
                    System.out.println(tmp.getLocation());
                    System.out.println(tmp.getStart());
                    System.out.println(tmp.getEnd());
                    System.out.println(tmp.getDescription());

                }*/
            }
        });
        update_FBlogin();
//        beaconManager = new BeaconManager(getActivity());
//
//        region = new Region("ranged region",
//                UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"), null, null);
//        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
//            @Override
//            public void onBeaconsDiscovered(Region region, List<Beacon> list) {
//                if (!list.isEmpty()) {
//                    System.out.println(list);
//                    //  Beacon nearestBeacon = list.get(0);
//
//                    // List<String> places = placesNearBeacon(nearestBeacon);
//                    // TODO: update the UI here
//                    //Log.d("Airport", "Nearest places: " + places);
//                    beaconManager.stopRanging(region);
//                    String location = checkLecturesTime();
//                    System.out.println(location);
//                    if (location.equals("noLocation"))
//                        beaconManager.startRanging(region);
//                    else {
//                        setTmpBeaconList(list);
//                        model.checkBeacon(location);
//
//                    }
//
//
//                }
//            }
//        });


        scheduleAlarm();
        Intent msgIntent = new Intent(getActivity(), MyServiceTest.class);
        msgIntent.putExtra("list", model.getLectures_list_today());
        msgIntent.putExtra("test","test");
        getActivity().startService(msgIntent);
        //
        return view;
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
                    return tmp.getLocation();
                }
            }
        }
        return "noLocation";

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        SystemRequirementsChecker.checkWithDefaultDialogs(getActivity());
//        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
//            @Override
//            public void onServiceReady() {
//                beaconManager.startRanging(region);
//            }
//        });

    }

    @Override
    public void onPause() {
        super.onPause();
       // beaconManager.stopRanging(region);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;

    }

    @Override
    public void oncheckBeaconListener(String UUID, String major, String minor, String location) {
        Boolean found_flag=false;
        System.out.println(UUID.toLowerCase() + " " + major + " " + minor + " " + location);
        System.out.println(tmpBeaconList);

        for (Beacon tmp : tmpBeaconList){
            String beaconUUID= UUID.toLowerCase();
            if (beaconUUID.equals(tmp.getProximityUUID().toString()) && major.equals(Integer.toString(tmp.getMajor())) && minor.equals(Integer.toString(tmp.getMinor()))){
                System.out.println("Verified");
                showAlertDialog();
                //showNotification(
//                        "Attendance!",
//                        "Please sign in for the lesson in the lecture hall "
//                                + location);
                found_flag=true;

            }
        }
        if (!found_flag)
            beaconManager.startRanging(region);
    }

    public void showNotification(String title, String message) {
        Intent notifyIntent = new Intent(getActivity(), MainActivity.class);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivities(getActivity(), 0,
                new Intent[] { notifyIntent }, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(getActivity())
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();
        notification.defaults |= Notification.DEFAULT_SOUND;
        NotificationManager notificationManager =
                (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
    }

    public void showAlertDialog(){

        // 1. Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

// 2. Chain together various setter methods to set the dialog characteristics
        builder.setMessage("Do you want to sign in for the "+tmp_attendance_class.getType()+" "+tmp_attendance_class.getModule()+"which is taking place in "+tmp_attendance_class.getLocation()+"?")
                .setTitle("Attendance for the "+tmp_attendance_class.getModule());
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });

// 3. Get the AlertDialog from create()
        AlertDialog dialog = builder.create();
        dialog.show();


    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    public void update_login(){
        id.setText(sharedpreferences.getString("id", "User"));

    }

    public void update_logout(){
        id.setText("");
        Bitmap imageBitmap= BitmapFactory.decodeResource(getResources(), R.drawable.photo_profile);
        RoundedBitmapDrawable roundedBitmapDrawable= RoundedBitmapDrawableFactory.create(getResources(), imageBitmap);
        roundedBitmapDrawable.setCornerRadius(25);
        photo_profile.setImageDrawable(roundedBitmapDrawable);
        fbName.setVisibility(View.GONE);

    }

    public void update_FBlogin(){
        Profile profile = Profile.getCurrentProfile();
        if (profile!=null){
            //load photo profile from shared preferences
            String previouslyEncodedImage = sharedpreferences.getString("photo_profile", "");
            if( !previouslyEncodedImage.equalsIgnoreCase("") ){
                byte[] b = Base64.decode(previouslyEncodedImage, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
                RoundedBitmapDrawable roundedBitmapDrawable= RoundedBitmapDrawableFactory.create(getResources(), bitmap);
                roundedBitmapDrawable.setCornerRadius(25);
                 photo_profile.setImageDrawable(roundedBitmapDrawable);
            }
             fbName.setVisibility(View.VISIBLE);
             fbName.setText(profile.getName());

        }else{
            //set default images
            Bitmap imageBitmap=BitmapFactory.decodeResource(getResources(),  R.drawable.photo_profile);
            RoundedBitmapDrawable roundedBitmapDrawable= RoundedBitmapDrawableFactory.create(getResources(), imageBitmap);
            roundedBitmapDrawable.setCornerRadius(25);
              photo_profile.setImageDrawable(roundedBitmapDrawable);
            fbName.setVisibility(View.GONE);

        }

    }


    public void scheduleAlarm()
    {

        // Set the alarm to start at approximately 8:00 p.m.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 01);

       // Intent intentAlarm = new Intent(getActivity(), AlarmReciever.class);

        // create the object
      //  AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        AlarmManager alarmManager = (AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getActivity(), AlarmReciever.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(getActivity(), 0, intent, 0);
        // constants--in this case, AlarmManager.INTERVAL_DAY.
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, alarmIntent);

        //set the alarm for particular time
//        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
//                AlarmManager.INTERVAL_DAY, PendingIntent.getBroadcast(getActivity(),1,  intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));
//
        Toast.makeText(getActivity(), "Alarm Scheduled for Tommrrow", Toast.LENGTH_SHORT).show();

    }
}



