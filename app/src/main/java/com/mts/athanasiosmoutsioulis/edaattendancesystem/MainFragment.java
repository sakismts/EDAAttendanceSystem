package com.mts.athanasiosmoutsioulis.edaattendancesystem;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.SystemRequirementsChecker;
import com.facebook.Profile;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Property;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class MainFragment extends Fragment {
    TextView id;
    TextView fbName;
    ImageView photo_profile;
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;
    public AttendanceModel model = AttendanceModel.getOurInstance();
    private OnFragmentInteractionListener mListener;
    private OnLoadCalendarFeeds mCalendarListener;
    private BeaconManager beaconManager;
    private Region region;


    ArrayList<com.mts.athanasiosmoutsioulis.edaattendancesystem.Beacon> tmpBeaconList = new ArrayList<com.mts.athanasiosmoutsioulis.edaattendancesystem.Beacon>();

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
        System.out.println("the size of today lectures is : " + model.getLectures_list_today().size());
        try {
            model.opendb_read();
            model.read_db_today();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        model.close();
        System.out.println("todays count" + model.getLectures_list_today().size());

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
            }
        });
        update_FBlogin();

        Boolean calendar_feeds=sharedpreferences.getBoolean("Calendar", false);
        if(calendar_feeds==false)
        showDialog_ImportCalendar();

        Intent msgIntent = new Intent(getActivity(), DownloadBeaconIdsService.class);
        getActivity().startService(msgIntent);
        //
        return view;
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
            mCalendarListener = (OnLoadCalendarFeeds) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }

    }

    @Override
    public void onResume() {
        super.onResume();


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
        mCalendarListener=null;

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

    public interface OnLoadCalendarFeeds {
        // TODO: Update argument type and name
        public void onLoadCalendarFeeds();
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




    public void showDialog_ImportCalendar(){
        if (mCalendarListener!=null)
            mCalendarListener.onLoadCalendarFeeds();


    }


}



