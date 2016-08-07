package com.mts.athanasiosmoutsioulis.edaattendancesystem;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
public class MainFragment extends Fragment implements AttendanceModel.OnSignAttendanceListener{
    TextView id;
    TextView status;
    TextView fbName;
    ImageView photo_profile;
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;
    public AttendanceModel model = AttendanceModel.getOurInstance();
    private OnFragmentInteractionListener mListener;
    private OnLoadCalendarFeeds mCalendarListener;
    MyReceiver myReceiver;
    private ProgressDialog Attendanceprogressdialog;
    private final static int FADE_DURATION = 800; // in milliseconds

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
        Attendanceprogressdialog=new ProgressDialog(getActivity());
        Attendanceprogressdialog.setMessage("Sign Attendance, please wait.");
        model.setSignAttendanceListener(this);
        status=(TextView)view.findViewById(R.id.tv_status);
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent feedback = new Intent(getActivity(), FeedBackList.class);
                startActivity(feedback);
            }
        });


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

        LinearLayout fbList = (LinearLayout)view.findViewById(R.id.ll_facebookList);
        fbList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent fbIntent=new Intent(getActivity(),FacebookList.class);
                startActivity(fbIntent);
            }
        });
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
                Intent schedule = new Intent(getActivity(), ScheduleActivity.class);
                startActivity(schedule);
            }
        });
        LinearLayout manualAttendance = (LinearLayout)view.findViewById(R.id.ll_search_beacon);
        manualAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent manualAtt=new Intent(getActivity(),ManualAttendance.class);
                startActivity(manualAtt);
            }
        });
        update_login();
        update_FBlogin();

        Boolean calendar_feeds=sharedpreferences.getBoolean("Calendar", false);
        if(calendar_feeds==false)
        showDialog_ImportCalendar();

        //Register BroadcastReceiver
        //to receive event from our service
        myReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DownloadBeaconIdsService.MY_ACTION);
        getActivity().registerReceiver(myReceiver, intentFilter);

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
        updateStatus();

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
        getActivity().unregisterReceiver(myReceiver);


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

    public void update_login() {
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

    private class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            // TODO Auto-generated method stub

            if(arg1.getBooleanExtra("Detected_Lecture",false)==true){
               updateStatus();
            }else{
            String datapassed = arg1.getStringExtra("Lecture");
            displayAttendanceDialog(datapassed);
            }

        }

    }

    private void displayAttendanceDialog(String data)
    {
        System.out.println("the current lecture is : "+model);

        System.out.println("the current lecture is : "+model.getCurrentLecture());

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Attendance");
        builder.setMessage(data).setCancelable(
                false).setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Attendanceprogressdialog.show();
                        String user_id=sharedpreferences.getString("id", "User");
                        String module_id=model.getCurrentLecture().getModule();
                        String lecture_type=model.getCurrentLecture().getType();
                        String location=model.getCurrentLecture().getLocation();

                        Calendar c_start = Calendar.getInstance();
                        c_start.setTime(model.getCurrentLecture().getStart());
                        System.out.println(model.getCurrentLecture().getStart());
                        String startDate=c_start.get(Calendar.DAY_OF_MONTH)+"/"+String.valueOf(c_start.get(Calendar.MONTH)+1)+"/"+c_start.get(Calendar.YEAR)+"T"+c_start.get(Calendar.HOUR_OF_DAY)+":"+c_start.get(Calendar.MINUTE);

                        Calendar c_end = Calendar.getInstance();
                        c_end.setTime(model.getCurrentLecture().getEnd());
                        String endDate=c_end.get(Calendar.DAY_OF_MONTH)+"/"+String.valueOf(c_end.get(Calendar.MONTH)+1)+"/"+c_end.get(Calendar.YEAR)+"T"+c_end.get(Calendar.HOUR_OF_DAY)+":"+c_end.get(Calendar.MINUTE);

                        String uri = "http://greek-tour-guides.eu/ioannina/dissertation/insert_attendance.php?student_id="+user_id+"&module_id="+module_id+"&lectureType="+lecture_type+"&location="+location+"&startDate="+startDate+"&endDate="+endDate+"&valid=true";
                        Log.i("URI",uri.toString());
                        model.signAttendance(uri);
                    }
                }).setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onSignAttendanceListener(boolean signed) {
        Attendanceprogressdialog.dismiss();
        if(signed==true){
            Lecture tmp_attendance_class=model.getCurrentLecture();
            String dateStart = DateFormat.format("yyyyMMdd'T'HHmmss'Z'", tmp_attendance_class.getStart()).toString();
            String dateEnd = DateFormat.format("yyyyMMdd'T'HHmmss'Z'", tmp_attendance_class.getEnd()).toString();

            try {
                model.open();
                model.updateAttendance(tmp_attendance_class.getTitle(),tmp_attendance_class.getModule(),tmp_attendance_class.getType(),dateStart,dateEnd,tmp_attendance_class.getLocation(),"true");
                model.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }


        final Dialog okDialog = new Dialog(getActivity());
        okDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = getActivity().getLayoutInflater();
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
            Toast.makeText(getActivity(),"You have signed successfully!",Toast.LENGTH_SHORT).show();
        }

    }

    private  void updateStatus(){
        Lecture tmp=checkLectures();
        if(tmp!=null){
            if (tmp.getAttendance().equals("false")){
        System.out.println();
        status.setText("You have "+tmp.getType()+" "+tmp.getTitle()+tmp.getLocation()+"\n Status:Not Signed");
        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(100); //You can manage the blinking time with this parameter
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        status.startAnimation(anim);}
        else{
                status.setText("You have "+tmp.getType()+" "+tmp.getTitle()+tmp.getLocation()+"\n Status:Signed");
                status.clearAnimation();
            }
        }else{
            status.setText("");
            status.clearAnimation();
        }
    }
    private void setScaleAnimation(View view) {
        ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(FADE_DURATION);
        view.startAnimation(anim);
    }


    public Lecture checkLectures(){
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
                    Log.i("Lecture","Current lecture is at :"+tmp.getLocation());

                    return tmp;
                }
            }
        }
        return null;

    }

}



