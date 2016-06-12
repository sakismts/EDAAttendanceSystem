package com.mts.athanasiosmoutsioulis.edaattendancesystem;

/**
 * Created by AthanasiosMoutsioulis on 01/06/16.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by AthanasiosMoutsioulis on 20/05/16.
 */
public class AttendanceModel {

    //variables
    private static AttendanceModel ourInstance;
    //SQL
    private SQLiteDatabase database;
    private LecturesDBHelper dbHelper;
    private Cursor dbCursor;
    private int counter=0;

    //user info
    String id;
    String fbName;
    Bitmap photoProfile;

    public ArrayList<Lecture> getLectures_list() {
        return lectures_list;
    }

    public void setLectures_list(ArrayList<Lecture> lectures_list) {
        this.lectures_list = lectures_list;
    }

    //Lecture array
    ArrayList<Lecture> lectures_list= new ArrayList<Lecture>();

    public ArrayList<Lecture> getLectures_list_today() {
        return lectures_list_today;
    }

    public ArrayList<Lecture> getLectures_list_week() {
        return lectures_list_week;
    }

    public ArrayList<Lecture> getLectures_list_month() {
        return lectures_list_month;
    }

    public void setLectures_list_today(ArrayList<Lecture> lectures_list_today) {
        this.lectures_list_today = lectures_list_today;
    }

    public void setLectures_list_week(ArrayList<Lecture> lectures_list_week) {
        this.lectures_list_week = lectures_list_week;
    }

    public void setLectures_list_month(ArrayList<Lecture> lectures_list_month) {
        this.lectures_list_month = lectures_list_month;
    }

    ArrayList<Lecture> lectures_list_today= new ArrayList<Lecture>();
    ArrayList<Lecture> lectures_list_week= new ArrayList<Lecture>();
    ArrayList<Lecture> lectures_list_month= new ArrayList<Lecture>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFbName() {
        return fbName;
    }

    public void setFbName(String fbName) {
        this.fbName = fbName;
    }

    public Bitmap getPhotoProfile() {
        return photoProfile;
    }

    public void setPhotoProfile(Bitmap photoProfile) {
        this.photoProfile = photoProfile;
    }
    ///////


    private OnSignUpUpdateListener signupUpdateListener; //define var of the interface

    private OnSignInUpdateListener signinUpdateListener; //define var of the interface




    private OnCheckBeaconListener checkBeaconUpdateListener; //define var of the interface



    private OnCourseListUpdateListener courseListUpdateListener; //define var of the interface

    //constructor
    public AttendanceModel(Context context) {
        ourInstance=this;
        dbHelper = new LecturesDBHelper(MyApplication.getInstance());
    }



    public static AttendanceModel getOurInstance() {
        return ourInstance;
    }

    public static void setOurInstance(AttendanceModel ourInstance) {
        AttendanceModel.ourInstance = ourInstance;
    }

    /////// Sign Up Section/////////////

    public void signup(String uri){

        Log.i("Signup","Sending request");
        //  String uri = "http://greek-tour-guides.eu/ioannina/dissertation/insert_user.php?id=2&role=student&pass=1&course=a";
        JsonObjectRequest request = new JsonObjectRequest(uri, signupListener,signupErrorListener);

        MyApplication.getInstance().getRequestQueue().add(request);

    }

    Response.Listener<JSONObject> signupListener = new Response.Listener<JSONObject>(){


        @Override
        public void onResponse(JSONObject response) {
            System.out.println(response);
            int result=-1;
            try {
                result= response.getInt("success");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (result==1)
                notifyListenerSignUp(true);
            else
                notifyListenerSignUp(false);
        }
    };

    Response.ErrorListener signupErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {

        }
    };

    //INTERFACE for signup
    public interface  OnSignUpUpdateListener{
        void onSignUpUpdateListener(boolean signed);

    }

    public void setSignupUpdateListener(OnSignUpUpdateListener signupUpdateListener) {
        this.signupUpdateListener = signupUpdateListener;
    }

    private void notifyListenerSignUp(boolean signed){


        if (signupListener != null)

            signupUpdateListener.onSignUpUpdateListener(signed);

    }
    ///////////////////////////////////////////////////////

    /////Sing in Section/////////////

    public void signin(String uri){

        Log.i("Signin","Sending request");
        //  String uri = "http://greek-tour-guides.eu/ioannina/dissertation/insert_user.php?id=2&role=student&pass=1&course=a";
        JsonObjectRequest request = new JsonObjectRequest(uri, signinListener,signinErrorListener);

        MyApplication.getInstance().getRequestQueue().add(request);

    }

    Response.Listener<JSONObject> signinListener = new Response.Listener<JSONObject>(){


        @Override
        public void onResponse(JSONObject response) {
            System.out.println(response);
            int result=-1;
            try {
                result= response.getInt("success");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (result==1)
                notifyListenerSignIn(true);
            else
                notifyListenerSignIn(false);
        }
    };

    Response.ErrorListener signinErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {

        }
    };

    //INTERFACE for signin
    public interface  OnSignInUpdateListener{
        void onSignInUpdateListener(boolean signed);

    }

    public void setSigninUpdateListener(OnSignInUpdateListener signinUpdateListener) {
        this.signinUpdateListener = signinUpdateListener;
    }

    private void notifyListenerSignIn(boolean login){


        if (signinUpdateListener != null)

            signinUpdateListener.onSignInUpdateListener(login);

    }

    ///////////////////////////////////////////////////////////////
    /////////////update course list////////////////////////////////

    public void updateCourseList(){

        Log.i("CourseList","Sending request");
        //  String uri = "http://greek-tour-guides.eu/ioannina/dissertation/insert_user.php?id=2&role=student&pass=1&course=a";
        String uri = "http://greek-tour-guides.eu/ioannina/dissertation/courseList.php?";
        JsonObjectRequest request = new JsonObjectRequest(uri, courseUpdateListener,courseUpdateErrorListener);

        MyApplication.getInstance().getRequestQueue().add(request);

    }

    Response.Listener<JSONObject> courseUpdateListener = new Response.Listener<JSONObject>(){


        @Override
        public void onResponse(JSONObject response) {
            System.out.println(response);
            ArrayList<String> courses = new ArrayList<String>();
            int result=-1;
            try {
                result= response.getInt("success");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (result==1){
                try {
                    JSONArray tmp_course = response.getJSONArray("courses");

                    for (int i=0; i<tmp_course.length();i++){
                        JSONObject objjson = tmp_course.getJSONObject(i);
                        courses.add(objjson.getString("course"));

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            notifyListenerCourseList(courses);

        }
    };

    Response.ErrorListener courseUpdateErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {

        }
    };

    //interface for course list
    public interface  OnCourseListUpdateListener{
        void oncourseListUpdateListener(ArrayList courses);

    }

    public void setCourseListUpdateListener(OnCourseListUpdateListener courseListUpdateListener) {
        this.courseListUpdateListener = courseListUpdateListener;
    }

    private void notifyListenerCourseList(ArrayList courses){


        if (courseListUpdateListener != null)

            courseListUpdateListener.oncourseListUpdateListener(courses);

    }


    //////////////////Beacon check/////////////////////////////
    public interface  OnCheckBeaconListener{
        void oncheckBeaconListener(String UUID,String major,String minor,String location);

    }
    public OnCheckBeaconListener getCheckBeaconUpdateListener() {
        return checkBeaconUpdateListener;
    }

    public void setCheckBeaconUpdateListener(OnCheckBeaconListener checkBeaconUpdateListener) {
        this.checkBeaconUpdateListener = checkBeaconUpdateListener;
    }



    public void checkBeacon(String location){

        Log.i("CheckBeacon","Search beacon for location");
          String uri = "http://greek-tour-guides.eu/ioannina/dissertation/checkBeacon.php?location="+location;
        JsonObjectRequest request = new JsonObjectRequest(uri, checkBeaconListener,checkBeaconErrorListener);

        MyApplication.getInstance().getRequestQueue().add(request);

    }

    Response.Listener<JSONObject> checkBeaconListener = new Response.Listener<JSONObject>(){


        @Override
        public void onResponse(JSONObject response) {
            System.out.println(response);
            int result=-1;
            try {
                result= response.getInt("success");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String BeaconUUID="-1",major="-1",minor="-1",location="-1";
            if (result==1){

                try {
                    JSONArray tmp_course = response.getJSONArray("Beacons");

                    for (int i=0; i<tmp_course.length();i++){
                        JSONObject objjson = tmp_course.getJSONObject(i);
                        BeaconUUID=objjson.getString("UUID");
                        major=objjson.getString("major");
                        minor=objjson.getString("minor");
                        location=objjson.getString("location");


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                notifyListenerBeaconCheck(BeaconUUID,major,minor,location);
            }
            else
                notifyListenerBeaconCheck(BeaconUUID,major,minor,location);
        }
    };

    Response.ErrorListener checkBeaconErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {

        }
    };

    private void notifyListenerBeaconCheck(String UUID, String major, String minor, String location ){


        if (checkBeaconUpdateListener != null)

            checkBeaconUpdateListener.oncheckBeaconListener(UUID,major,minor,location);

    }


    //////////////////////////////////////////////
    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();

    }
    public void opendb_read() throws SQLException {
        database = dbHelper.getReadableDatabase();

    }


    public void close(){
        database.close();

    }

    public void addLectureDBRecord(String title,String module,String type,String start, String end,String location,String description ){

        ContentValues values = new ContentValues();
        values.put(LecturesDBHelper.COLUMN_TITLE, title);
        values.put(LecturesDBHelper.COLUMN_MODULE, module);
        values.put(LecturesDBHelper.COLUMN_TYPE, type);
        values.put(LecturesDBHelper.COLUMN_LOCATION, location);
        values.put(LecturesDBHelper.COLUMN_START, start);
        values.put(LecturesDBHelper.COLUMN_END, end);
        values.put(LecturesDBHelper.COLUMN_DESCRIPTION, description);


            long insertId = database.insert(LecturesDBHelper.TABLE_NAME, null, values);

        Log.i("Database", "insert" + counter++);


    }
    public void read_db(){
        ArrayList<Lecture>  tmpList = new ArrayList<Lecture>();
        String[] allColumns = {
                LecturesDBHelper.COLUMN_TITLE,
                LecturesDBHelper.COLUMN_MODULE,
                LecturesDBHelper.COLUMN_TYPE,
                LecturesDBHelper.COLUMN_START,
                LecturesDBHelper.COLUMN_END,
                LecturesDBHelper.COLUMN_LOCATION,
                LecturesDBHelper.COLUMN_DESCRIPTION

        };

            dbCursor = database.query(LecturesDBHelper.TABLE_NAME,allColumns,null,null,null,null,null,null);


        //dbCursor.moveToFirst();
        while (dbCursor.moveToNext()) {

            String title = dbCursor.getString(dbCursor.getColumnIndexOrThrow(LecturesDBHelper.COLUMN_TITLE));
            String module = dbCursor.getString(dbCursor.getColumnIndexOrThrow(LecturesDBHelper.COLUMN_MODULE));
            String type = dbCursor.getString(dbCursor.getColumnIndexOrThrow(LecturesDBHelper.COLUMN_TYPE));
            String start = dbCursor.getString(dbCursor.getColumnIndexOrThrow(LecturesDBHelper.COLUMN_START));
            String end = dbCursor.getString(dbCursor.getColumnIndexOrThrow(LecturesDBHelper.COLUMN_END));
            String location = dbCursor.getString(dbCursor.getColumnIndexOrThrow(LecturesDBHelper.COLUMN_LOCATION));
            String description = dbCursor.getString(dbCursor.getColumnIndexOrThrow(LecturesDBHelper.COLUMN_DESCRIPTION));

            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
            TimeZone timeZone = TimeZone.getTimeZone("GMT");
            format.setTimeZone(timeZone);
            Date tmpt_start=null;
            Date tmpt_end=null;
            try {
                tmpt_start = format.parse(start);
                tmpt_end = format.parse(end);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


            tmpList.add(new Lecture(title, module, type, tmpt_start, tmpt_end, location, description));


        }
        Collections.sort(tmpList);

       setLectures_list(tmpList);

        // populate the other tables
        //current date and time
        Calendar c = Calendar.getInstance();
        int c_day = c.get(Calendar.DAY_OF_MONTH);
        int c_week = c.get(Calendar.WEEK_OF_YEAR);
        int c_month = c.get(Calendar.MONTH)+1;
        int c_year = c.get(Calendar.YEAR);

        //today
//        Calendar c1 = Calendar.getInstance();
//
//        for (Lecture tmp: tmpList){
//            c1.setTime(tmp.getStart());
//            int day = c1.get(Calendar.DAY_OF_MONTH);
//            int month = c1.get(Calendar.MONTH)+1;
//            //System.out.println("Day :"+c_day+ " - " +day+", Month :"+c_month+ " - " +intMonth);
//            if (day==c_day && month==c_month) {
//                getLectures_list_today().add(tmp);
//            }
//        }



        //month
        Calendar c2 = Calendar.getInstance();
        for (Lecture tmp: tmpList){
            c2.setTime(tmp.getStart());
            int month = c2.get(Calendar.MONTH)+1;
            //System.out.println("Day :"+c_day+ " - " +day+", Month :"+c_month+ " - " +intMonth);
            if (month==c_month) {
               getLectures_list_month().add(tmp);
            }
        }


        //week
        Calendar c3 = Calendar.getInstance();
        for (Lecture tmp: tmpList){
            c3.setTime(tmp.getStart());
            int intWeek = c3.get(Calendar.WEEK_OF_YEAR);
            if (intWeek==c_week) {
                getLectures_list_week().add(tmp);
            }
        }


    }

    public void read_db_today(){
        ArrayList<Lecture>  tmpList = new ArrayList<Lecture>();
        String[] allColumns = {
                LecturesDBHelper.COLUMN_TITLE,
                LecturesDBHelper.COLUMN_MODULE,
                LecturesDBHelper.COLUMN_TYPE,
                LecturesDBHelper.COLUMN_START,
                LecturesDBHelper.COLUMN_END,
                LecturesDBHelper.COLUMN_LOCATION,
                LecturesDBHelper.COLUMN_DESCRIPTION

        };

        dbCursor = database.query(LecturesDBHelper.TABLE_NAME,allColumns,null,null,null,null,null,null);


        //dbCursor.moveToFirst();
        while (dbCursor.moveToNext()) {

            String title = dbCursor.getString(dbCursor.getColumnIndexOrThrow(LecturesDBHelper.COLUMN_TITLE));
            String module = dbCursor.getString(dbCursor.getColumnIndexOrThrow(LecturesDBHelper.COLUMN_MODULE));
            String type = dbCursor.getString(dbCursor.getColumnIndexOrThrow(LecturesDBHelper.COLUMN_TYPE));
            String start = dbCursor.getString(dbCursor.getColumnIndexOrThrow(LecturesDBHelper.COLUMN_START));
            String end = dbCursor.getString(dbCursor.getColumnIndexOrThrow(LecturesDBHelper.COLUMN_END));
            String location = dbCursor.getString(dbCursor.getColumnIndexOrThrow(LecturesDBHelper.COLUMN_LOCATION));
            String description = dbCursor.getString(dbCursor.getColumnIndexOrThrow(LecturesDBHelper.COLUMN_DESCRIPTION));

            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
            TimeZone timeZone = TimeZone.getTimeZone("GMT");
            format.setTimeZone(timeZone);
            Date tmpt_start=null;
            Date tmpt_end=null;
            try {
                tmpt_start = format.parse(start);
                tmpt_end = format.parse(end);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


            tmpList.add(new Lecture(title, module, type, tmpt_start, tmpt_end, location, description));


        }
        Collections.sort(tmpList);

        setLectures_list(tmpList);

        // populate the other tables
        //current date and time
        Calendar c = Calendar.getInstance();
        int c_day = c.get(Calendar.DAY_OF_MONTH);
        int c_week = c.get(Calendar.WEEK_OF_YEAR);
        int c_month = c.get(Calendar.MONTH)+1;
        int c_year = c.get(Calendar.YEAR);

        //today

        Calendar c1 = Calendar.getInstance();

        //Calendar c1 = new GregorianCalendar(TimeZone.getTimeZone("London"));

        for (Lecture tmp : tmpList){
            System.out.println(tmp.getStart());


            c1.setTime(tmp.getStart());
            System.out.println(c1.get(Calendar.HOUR_OF_DAY));
            int day = c1.get(Calendar.DAY_OF_MONTH);
            int month = c1.get(Calendar.MONTH)+1;
            if (day==c_day && month==c_month) {
                getLectures_list_today().add(tmp);
            }
        }

    }

    public Cursor getLectures(){
        String[] allColumns = {
                LecturesDBHelper.COLUMN_ID

        };
        dbCursor = database.query(LecturesDBHelper.TABLE_NAME,allColumns,null,null,null,null,null,null);

        return dbCursor;

    }
    public boolean isEmpty(){
        dbCursor = getLectures();
        long size = dbCursor.getCount();
        dbCursor.close();
        return (size==0);

    }
    public void deleteAllFromDB(){
            database.delete(dbHelper.TABLE_NAME, null, null);
    }

}
