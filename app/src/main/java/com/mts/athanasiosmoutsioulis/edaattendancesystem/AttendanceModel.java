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
import org.xml.sax.ext.LexicalHandler;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
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

    public Lecture getCurrentLecture() {
        return CurrentLecture;
    }

    public void setCurrentLecture(Lecture currentLecture) {
        CurrentLecture = currentLecture;
    }

    public Lecture CurrentLecture;

    public ArrayList<Lecture> getLectures_list() {
        return lectures_list;
    }

    public void setLectures_list(ArrayList<Lecture> lectures_list) {
        this.lectures_list = lectures_list;
    }

    //Lecture array
    ArrayList<Lecture> lectures_list= new ArrayList<Lecture>();

    public ArrayList<Lecture> getTeacherAttendances() {
        return TeacherAttendances;
    }

    public void setTeacherAttendances(ArrayList<Lecture> teacherAttendances) {
        TeacherAttendances = teacherAttendances;
    }

    //teacher Attendances
    ArrayList<Lecture> TeacherAttendances= new ArrayList<Lecture>();

    ///teacher feedback

    public ArrayList<Attendance> getTeacherFeedback() {
        return TeacherFeedback;
    }

    public void setTeacherFeedback(ArrayList<Attendance> teacherFeedback) {
        TeacherFeedback = teacherFeedback;
    }

    ArrayList<Attendance> TeacherFeedback=new ArrayList<Attendance>();

    public ArrayList<Lecture> getAttendances_list() {
        return attendances_list;
    }

    public void setAttendances_list(ArrayList<Lecture> attendances_list) {
        this.attendances_list = attendances_list;
    }

    ArrayList<Lecture> attendances_list= new ArrayList<Lecture>();

    public ArrayList<String> getModules_list() {
        return modules_list;
    }

    public void setModules_list(ArrayList<String> modules_list) {
        this.modules_list = modules_list;
    }

    ArrayList<String> modules_list= new ArrayList<String>();

    public Set<String> getMyModules() {
        return myModules;
    }

    public void setMyModules(Set<String> myModules) {
        this.myModules = myModules;
    }

    Set<String> myModules= new HashSet<String>();

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

    public ArrayList<Attendance> getStudents_Attendance_list() {
        return Students_Attendance_list;
    }

    public void setStudents_Attendance_list(ArrayList<Attendance> students_Attendance_list) {
        Students_Attendance_list = students_Attendance_list;
    }

    ArrayList<Attendance> Students_Attendance_list= new ArrayList<Attendance>();

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
    private OnSignAttendanceListener signAttendanceListener; //define var of the interface
    private OnCheckBeaconListener checkBeaconUpdateListener; //define var of the interface
    private OnGetFacebookDetailsListener GetFacebookDetailsListener;
    private OnGetUsersAttendListener GetUsersAttendListener;
    private OnSendFeedBack feedBackListener;
    private OnCourseListUpdateListener courseListUpdateListener; //define var of the interface
    private OnGetModulesListener getModulesListener;
    private OnGetTeacherAttendances getTeacherAttendancesListener;



    private OnGetTeacherSingleAttendance getTeacherSingleAttendanceListener;



    public OnCheckAttendanceListener getCheckAttendanceListener() {
        return checkAttendanceListener;
    }

    private OnCheckAttendanceListener checkAttendanceListener;

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

    ////////////Get Modules/////////////////////////////
    public interface  OnGetModulesListener{
        void getModules(boolean modules);

    }

    public void setGetModulesListener(OnGetModulesListener getModulesListener) {
        this.getModulesListener = getModulesListener;
    }

    public void getmodules(String uri){

        Log.i("GetModules","Sending request");
        //  String uri = "http://greek-tour-guides.eu/ioannina/dissertation/insert_user.php?id=2&role=student&pass=1&course=a";
        JsonObjectRequest request = new JsonObjectRequest(uri, modulesListener,modulesListenerErrorListener);
        MyApplication.getInstance().getRequestQueue().add(request);

    }

    Response.Listener<JSONObject> modulesListener = new Response.Listener<JSONObject>(){


        @Override
        public void onResponse(JSONObject response) {
            System.out.println(response);
            int result=-1;
            try {
                result= response.getInt("success");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (result==1){
                getModules_list().clear();
                try {
                    JSONArray tmp_modules = response.getJSONArray("modules");
                    String id="";
                    for (int i=0; i<tmp_modules.length();i++){
                        JSONObject objjson = tmp_modules.getJSONObject(i);
                        id = objjson.getString("moduleID");
                        getModules_list().add(id);

                    }
                    notifyListenerGetModules(true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }else{

                notifyListenerGetModules(false);
            }
        }
    };

    private void notifyListenerGetModules(boolean modules){


        if (getModulesListener != null)

            getModulesListener.getModules(modules);

    }

    Response.ErrorListener modulesListenerErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {

        }
    };


    //////////////////////////////////////////////////////

    ///////////Attendances Sign Section//////////////////

    public void signAttendance(String uri){

        Log.i("SignAttendance","Sending request");
        //  String uri = "http://greek-tour-guides.eu/ioannina/dissertation/insert_user.php?id=2&role=student&pass=1&course=a";
        JsonObjectRequest request = new JsonObjectRequest(uri, signAttListener,signAttendanceErrorListener);

        MyApplication.getInstance().getRequestQueue().add(request);

    }

    Response.Listener<JSONObject> signAttListener = new Response.Listener<JSONObject>(){


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
                notifyListenerAttendanceSign(true);
            else
                notifyListenerAttendanceSign(false);
        }
    };

    Response.ErrorListener signAttendanceErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {

        }
    };

    //INTERFACE for signup
    public interface  OnSignAttendanceListener{
        void onSignAttendanceListener(boolean signed);

    }

    public void setSignAttendanceListener(OnSignAttendanceListener signAttendanceListener) {
        this.signAttendanceListener = signAttendanceListener;
    }


    private void notifyListenerAttendanceSign(boolean signed){


        if (signAttendanceListener != null)

            signAttendanceListener.onSignAttendanceListener(signed);

    }


    /////////////////////////////////////////////////////

    ////////////////////Teacher Attendaces/////////////////
    //INTERFACE for signup

    public void getTeacherAttendaces(String uri){

        Log.i("TeacherAttendaces","Sending request");
        //  String uri = "http://greek-tour-guides.eu/ioannina/dissertation/insert_user.php?id=2&role=student&pass=1&course=a";
        JsonObjectRequest request = new JsonObjectRequest(uri, teacherAttendancesListener,teacherAttendancesErrorListener);

        MyApplication.getInstance().getRequestQueue().add(request);

    }

    Response.Listener<JSONObject> teacherAttendancesListener = new Response.Listener<JSONObject>(){


        @Override
        public void onResponse(JSONObject response) {
            System.out.println(response);
            int result=-1;
            try {
                result= response.getInt("success");
            } catch (JSONException e) {
                e.printStackTrace();
            }

                if (result==1){

                    try {
                        JSONArray tmp_attendances = response.getJSONArray("attendances");

                        int count=0;
                        int feedback_count=0;
                        for (int i=0; i<tmp_attendances.length();i++){

                            JSONObject objjson = tmp_attendances.getJSONObject(i);
                            String StudentId = objjson.getString("StudentId");
                            String moduleID = objjson.getString("moduleID");
                            String LectureType = objjson.getString("LectureType");
                            String location = objjson.getString("location");
                            String startDate = objjson.getString("startDate");
                            String endDate = objjson.getString("endDate");
                            String valid = objjson.getString("valid");
                            String feedback = objjson.getString("feedback");
                            String shareID = objjson.getString("shareID");
                            SimpleDateFormat format = new SimpleDateFormat("dd'/'MM'/'yyyy'T'HH':'mm");
                            System.out.println(startDate);


                            if (i+1<=tmp_attendances.length()-1)
                            {
                                JSONObject tmpt_objjson = tmp_attendances.getJSONObject(i+1);
                                if (startDate.equals(tmpt_objjson.getString("startDate"))){
                                    if(!feedback.isEmpty())
                                        feedback_count++;
                                 count++;
                                }else{
                                    count++;
                                    if(!feedback.isEmpty())
                                        feedback_count++;
                                    Date tmpt_start=null;
                                    Date tmpt_end=null;
                                    try {
                                        tmpt_start = format.parse(startDate);
                                        tmpt_end = format.parse(endDate);
                                    } catch (ParseException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }
                                    Lecture tmp= new Lecture(moduleID,LectureType,tmpt_start,tmpt_end,location,count,feedback_count);
                                    getTeacherAttendances().add(tmp);
                                    System.out.println(count);
                                    count=0;
                                    feedback_count=0;
                                }
                            }else{
                                count++;
                                if(!feedback.isEmpty())
                                    feedback_count++;
                                Date tmpt_start=null;
                                Date tmpt_end=null;
                                try {
                                    tmpt_start = format.parse(startDate);
                                    tmpt_end = format.parse(endDate);
                                } catch (ParseException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }

                                Lecture tmp= new Lecture(moduleID,LectureType,tmpt_start,tmpt_end,location,count,feedback_count);
                                getTeacherAttendances().add(tmp);

                            }
                            //date=startDate;

//                            System.out.println("start date :"+startDate);
//                            System.out.println("end date :"+endDate);
//                            System.out.println(count);


                            //

                        }

                        notifyListenerTeacherAttendance(true);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            else
                notifyListenerTeacherAttendance(false);
        }
    };

    Response.ErrorListener teacherAttendancesErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {

        }
    };



    public interface  OnGetTeacherAttendances{
        void onGetTeacherAttendances(boolean signed);

    }


    public void setGetTeacherAttendancesListener(OnGetTeacherAttendances getTeacherAttendancesListener) {
        this.getTeacherAttendancesListener = getTeacherAttendancesListener;
    }

    private void notifyListenerTeacherAttendance(boolean signed){


        if (getTeacherAttendancesListener != null)

            getTeacherAttendancesListener.onGetTeacherAttendances(signed);

    }


    //////////////////////////////////////////////////////

///////Teacher Single Attendance/////////////////////////
    public interface  OnGetTeacherSingleAttendance{
        void onGetTeacherSingleAttendance(boolean signed);

    }

    public void setGetTeacherSingleAttendanceListener(OnGetTeacherSingleAttendance getTeacherSingleAttendanceListener) {
        this.getTeacherSingleAttendanceListener = getTeacherSingleAttendanceListener;
    }

    public void getTeacherSingleAttendace(String uri){

        Log.i("TeacherSingleAttendace","Sending request");
        //  String uri = "http://greek-tour-guides.eu/ioannina/dissertation/insert_user.php?id=2&role=student&pass=1&course=a";
        JsonObjectRequest request = new JsonObjectRequest(uri, teacherSignleAttendancesListener,teacherAttendancesSingleErrorListener);

        MyApplication.getInstance().getRequestQueue().add(request);

    }

    Response.Listener<JSONObject> teacherSignleAttendancesListener = new Response.Listener<JSONObject>(){


        @Override
        public void onResponse(JSONObject response) {
            System.out.println(response);
            int result=-1;
            try {
                result= response.getInt("success");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (result==1){

                try {
                    JSONArray tmp_attendances = response.getJSONArray("attendances");
                    String date="";
                    int count=0;
                    for (int i=0; i<tmp_attendances.length();i++){

                        JSONObject objjson = tmp_attendances.getJSONObject(i);
                        String StudentId = objjson.getString("StudentId");
                        String valid = objjson.getString("valid");
                        String feedback = objjson.getString("feedback");
                        String fullName = objjson.getString("fullName");
                        String shareID = objjson.getString("shareID");
                        System.out.println(StudentId);
                        getStudents_Attendance_list().add(new Attendance(StudentId,valid,shareID,feedback,fullName));
                        if(!feedback.isEmpty())
                            getTeacherFeedback().add(new Attendance(StudentId,valid,shareID,feedback,fullName));

                    }

                    notifyListenerSingleTeacherAttendance(true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
            else
                notifyListenerSingleTeacherAttendance(false);
        }
    };

    Response.ErrorListener teacherAttendancesSingleErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {

        }
    };

    private void notifyListenerSingleTeacherAttendance(boolean signed){


        if (getTeacherSingleAttendanceListener != null)

            getTeacherSingleAttendanceListener.onGetTeacherSingleAttendance(signed);

    }

    ///////////////////////////////////////////////////






///get facebook Details /////////
    //INTERFACE for signup
    public interface  OnGetFacebookDetailsListener{
        void onGetFacebookDetailsListener(String fbid,String fbName);

    }

    public void setGetFacebookDetailsListener(OnGetFacebookDetailsListener getFacebookDetailsListener) {
        GetFacebookDetailsListener = getFacebookDetailsListener;
    }

    public void getfbDetails(String uri){

        Log.i("Faebook","Sending request");
        //  String uri = "http://greek-tour-guides.eu/ioannina/dissertation/insert_user.php?id=2&role=student&pass=1&course=a";
        JsonObjectRequest request = new JsonObjectRequest(uri, getfbinfolistener,getfbinfoErrorListener);

        MyApplication.getInstance().getRequestQueue().add(request);

    }

    Response.Listener<JSONObject> getfbinfolistener = new Response.Listener<JSONObject>(){


        @Override
        public void onResponse(JSONObject response) {
            System.out.println(response);
            int result=-1;
            try {
                result= response.getInt("success");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (result==1){
                try {
                    JSONArray tmp_student = response.getJSONArray("fbDetails");
                    String id="";
                    String name="";
                    for (int i=0; i<tmp_student.length();i++){
                        JSONObject objjson = tmp_student.getJSONObject(i);
                         id = objjson.getString("fbID");
                         name = objjson.getString("fbName");
                    }
                    notifyListenergetfbinfo(id,name);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
    };

    Response.ErrorListener getfbinfoErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {

        }
    };

    private void notifyListenergetfbinfo(String id, String name){


        if (GetFacebookDetailsListener != null)

            GetFacebookDetailsListener.onGetFacebookDetailsListener(id,name);

    }

    ////////



   ////////check attendances Section///////////////

    public void CheckAttendance(String uri){

        Log.i("Attendance","Check request");
        //  String uri = "http://greek-tour-guides.eu/ioannina/dissertation/insert_user.php?id=2&role=student&pass=1&course=a";
        JsonObjectRequest request = new JsonObjectRequest(uri, chAttendanceListener,CheckAttendanceErrorListener);

        MyApplication.getInstance().getRequestQueue().add(request);

    }

    Response.Listener<JSONObject> chAttendanceListener = new Response.Listener<JSONObject>(){


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
                notifyListenerCheckAttendance(true);
            else
                notifyListenerCheckAttendance(false);
        }
    };

    Response.ErrorListener CheckAttendanceErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {

        }
    };

    //INTERFACE for check Attendance
    public interface  OnCheckAttendanceListener{
        void onCheckAttendanceListener(boolean signed);

    }

    public void setCheckAttendanceListener(OnCheckAttendanceListener checkAttendanceListener) {
        this.checkAttendanceListener = checkAttendanceListener;
    }




    private void notifyListenerCheckAttendance(boolean signed){

        if (checkAttendanceListener != null)

            checkAttendanceListener.onCheckAttendanceListener(signed);

    }

    ///////////////////////////////////////////////////////////////
    /////////////sign in /////////////////////////////////

////////get users atternd///////////////

    public void GetUsersAttends(String uri){

        Log.i("Attendance","Check request");
        //  String uri = "http://greek-tour-guides.eu/ioannina/dissertation/insert_user.php?id=2&role=student&pass=1&course=a";
        JsonObjectRequest request = new JsonObjectRequest(uri, getUsersListener,getUsersErrorListener);

        MyApplication.getInstance().getRequestQueue().add(request);

    }

    Response.Listener<JSONObject> getUsersListener = new Response.Listener<JSONObject>(){


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
                notifyListenerGetUsers(response);
            else
                notifyListenerGetUsers(response);
        }
    };

    Response.ErrorListener getUsersErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {

        }
    };

    //INTERFACE
    public interface  OnGetUsersAttendListener{
        void OnGetUsersAttendListener(JSONObject signed);

    }


    public void setGetUsersAttendListener(OnGetUsersAttendListener getUsersAttendListener) {
        GetUsersAttendListener = getUsersAttendListener;
    }



    private void notifyListenerGetUsers(JSONObject response){

        if (GetUsersAttendListener != null)

            GetUsersAttendListener.OnGetUsersAttendListener(response);

    }

    ///////////////////////////////////////////////////////////////



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
            if (result==1){
                try {
                    JSONArray tmp_user = response.getJSONArray("user");
                    String name="";
                    for (int i=0; i<tmp_user.length();i++){
                        JSONObject objjson = tmp_user.getJSONObject(i);
                        name = objjson.getString("fullName");
                    }
                    notifyListenerSignIn(true,name);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            else
                notifyListenerSignIn(false,"");
        }
    };

    Response.ErrorListener signinErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {

        }
    };

    //INTERFACE for signin
    public interface  OnSignInUpdateListener{
        void onSignInUpdateListener(boolean signed, String name);

    }

    public void setSigninUpdateListener(OnSignInUpdateListener signinUpdateListener) {
        this.signinUpdateListener = signinUpdateListener;
    }

    private void notifyListenerSignIn(boolean login, String name){


        if (signinUpdateListener != null)

            signinUpdateListener.onSignInUpdateListener(login,name);

    }



    ///////////////////////////////////////////////

    //////send feedback////////////////////////////
    //INTERFACE for send feedback


    public interface  OnSendFeedBack{
        void onSendFeedBack();

    }
    public void setFeedBackListener(OnSendFeedBack feedBackListener) {
        this.feedBackListener = feedBackListener;
    }

    public void sendFeedBack(String uri){

        Log.i("FeedBack","Sending request");
        //  String uri = "http://greek-tour-guides.eu/ioannina/dissertation/insert_user.php?id=2&role=student&pass=1&course=a";
        JsonObjectRequest request = new JsonObjectRequest(uri, fdb_Listener,fdb_ErrorListener);

        MyApplication.getInstance().getRequestQueue().add(request);

    }

    Response.Listener<JSONObject> fdb_Listener = new Response.Listener<JSONObject>(){


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
                notifyListenerFeedBack(true);
            else
                notifyListenerFeedBack(false);
        }
    };

    Response.ErrorListener fdb_ErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {

        }
    };

    private void notifyListenerFeedBack(boolean submited){


        if (feedBackListener != null)

            feedBackListener.onSendFeedBack();

    }

    ////////////////////////////////////////////////




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
            Log.i("BeaconError",error.toString());

        }
    };

    private void notifyListenerBeaconCheck(String UUID, String major, String minor, String location ){


        if (checkBeaconUpdateListener != null)

            checkBeaconUpdateListener.oncheckBeaconListener(UUID,major,minor,location);

    }


    //////////////////////////////////////////////

    ///////update facebook account on server///////

    public void updateFBaccount(String URI){

        JsonObjectRequest request = new JsonObjectRequest(URI, updateFBIDListener,updateFBIDErrorListener);

        MyApplication.getInstance().getRequestQueue().add(request);

    }

    Response.Listener<JSONObject> updateFBIDListener = new Response.Listener<JSONObject>(){


        @Override
        public void onResponse(JSONObject response) {
            System.out.println(response);
            int result=-1;
            try {
                result= response.getInt("success");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (result==1){

               Log.i("Facebook","Id updated on server");
            }
            else
                Log.i("Facebook", "Id not updated on server");
        }
    };

    Response.ErrorListener updateFBIDErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.i("Facebook",error.toString());

        }
    };






    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();

    }
    public void opendb_read() throws SQLException {
        database = dbHelper.getReadableDatabase();

    }


    public void close(){
        database.close();

    }

    public void addLectureDBRecord(String title,String module,String type,String start, String end,String location,String description, String attendance ){

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
        SimpleDateFormat format2 = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
        String newSDate=format2.format(tmpt_start);
        String newEDate=format2.format(tmpt_end);


        ContentValues values = new ContentValues();
        values.put(LecturesDBHelper.COLUMN_TITLE, title);
        values.put(LecturesDBHelper.COLUMN_MODULE, module);
        values.put(LecturesDBHelper.COLUMN_TYPE, type);
        values.put(LecturesDBHelper.COLUMN_LOCATION, location);
        values.put(LecturesDBHelper.COLUMN_START, newSDate);
        values.put(LecturesDBHelper.COLUMN_END, newEDate);
        values.put(LecturesDBHelper.COLUMN_DESCRIPTION, description);
        values.put(LecturesDBHelper.COLUMN_ATTENDANCE, attendance);



            long insertId = database.insert(LecturesDBHelper.TABLE_NAME, null, values);

        Log.i("Database", "insert" + counter++);


    }

    public void deleteAllItems(){
        counter=0;
        try {
            open();
            database.delete(LecturesDBHelper.TABLE_NAME, null, null);
            close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void updateAttendance(String title,String module,String type,String start, String end,String location, String attendance ){

        System.out.println(start);
        System.out.println(end);
        ContentValues values = new ContentValues();
        values.put(LecturesDBHelper.COLUMN_TITLE, title);
        values.put(LecturesDBHelper.COLUMN_MODULE, module);
        values.put(LecturesDBHelper.COLUMN_TYPE, type);
        values.put(LecturesDBHelper.COLUMN_LOCATION, location);
        values.put(LecturesDBHelper.COLUMN_START, start);
        values.put(LecturesDBHelper.COLUMN_END, end);
        values.put(LecturesDBHelper.COLUMN_ATTENDANCE, attendance);
        //String query= LecturesDBHelper.COLUMN_TITLE+" = ? AND "+LecturesDBHelper.COLUMN_MODULE+" = ? AND "+LecturesDBHelper.COLUMN_TYPE+" = ? AND "+LecturesDBHelper.COLUMN_LOCATION+" = ? AND "+LecturesDBHelper.COLUMN_START+" = ? AND "+LecturesDBHelper.COLUMN_END+" = ? AND "+LecturesDBHelper.COLUMN_ATTENDANCE+" = ?";

       // String query= LecturesDBHelper.COLUMN_TITLE+" = ? AND "+LecturesDBHelper.COLUMN_MODULE+" = ? AND "+LecturesDBHelper.COLUMN_TYPE+" = ? AND "+LecturesDBHelper.COLUMN_LOCATION+" = ? AND "+LecturesDBHelper.COLUMN_START+" = ? AND "+LecturesDBHelper.COLUMN_END+" = ? ";
        String query= LecturesDBHelper.COLUMN_LOCATION+" = ? AND "+LecturesDBHelper.COLUMN_START+" = ? AND "+LecturesDBHelper.COLUMN_END+" = ? ";
        String[] args = new String[]{location,start,end};
        long insertId = database.update(LecturesDBHelper.TABLE_NAME, values, query, args);
        System.out.println("the query is "+insertId);
       // long insertId = database.insert(LecturesDBHelper.TABLE_NAME, null, values);
        System.out.println(insertId);
        if(insertId==1){
            read_db_today();

        }

    }

    public void updateFeedback(String title,String module,String type,String start, String end,String location, String feedback ){

        ContentValues values = new ContentValues();
        values.put(LecturesDBHelper.COLUMN_TITLE, title);
        values.put(LecturesDBHelper.COLUMN_MODULE, module);
        values.put(LecturesDBHelper.COLUMN_TYPE, type);
        values.put(LecturesDBHelper.COLUMN_LOCATION, location);
        values.put(LecturesDBHelper.COLUMN_START, start);
        values.put(LecturesDBHelper.COLUMN_END, end);
        values.put(LecturesDBHelper.COLUMN_FEEDBACK, feedback);
        //String query= LecturesDBHelper.COLUMN_TITLE+" = ? AND "+LecturesDBHelper.COLUMN_MODULE+" = ? AND "+LecturesDBHelper.COLUMN_TYPE+" = ? AND "+LecturesDBHelper.COLUMN_LOCATION+" = ? AND "+LecturesDBHelper.COLUMN_START+" = ? AND "+LecturesDBHelper.COLUMN_END+" = ? AND "+LecturesDBHelper.COLUMN_ATTENDANCE+" = ?";

        String query= LecturesDBHelper.COLUMN_TITLE+" = ? AND "+LecturesDBHelper.COLUMN_MODULE+" = ? AND "+LecturesDBHelper.COLUMN_TYPE+" = ? AND "+LecturesDBHelper.COLUMN_LOCATION+" = ? AND "+LecturesDBHelper.COLUMN_START+" = ? AND "+LecturesDBHelper.COLUMN_END+" = ? ";
        String[] args = new String[]{title,module,type,location,start,end};
        long insertId = database.update(LecturesDBHelper.TABLE_NAME, values, query, args);
        // long insertId = database.insert(LecturesDBHelper.TABLE_NAME, null, values);
        System.out.println(insertId);
        if(insertId==1){
            read_db_today();

        }

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
                LecturesDBHelper.COLUMN_DESCRIPTION,
                LecturesDBHelper.COLUMN_ATTENDANCE,
                LecturesDBHelper.COLUMN_FEEDBACK

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
            String attendance = dbCursor.getString(dbCursor.getColumnIndexOrThrow(LecturesDBHelper.COLUMN_ATTENDANCE));
            String feedback = dbCursor.getString(dbCursor.getColumnIndexOrThrow(LecturesDBHelper.COLUMN_FEEDBACK));
            System.out.println("title:"+title);
            System.out.println("location:"+location);
            System.out.println("sDate:"+start);
            System.out.println("eDate:"+end);
            if(feedback==null)
                feedback="false";

            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
//            TimeZone timeZone = TimeZone.getTimeZone("GMT");
//            format.setTimeZone(timeZone);
            Date tmpt_start=null;
            Date tmpt_end=null;
            try {
                tmpt_start = format.parse(start);
                tmpt_end = format.parse(end);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


            tmpList.add(new Lecture(title, module, type, tmpt_start, tmpt_end, location, description,attendance));


        }
        Collections.sort(tmpList);
        getLectures_list().clear();

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
        getLectures_list_month().clear();
        Calendar c2 = Calendar.getInstance();
        for (Lecture tmp: tmpList){
            c2.setTime(tmp.getStart());
            int month = c2.get(Calendar.MONTH)+1;
            //System.out.println("Day :"+c_day+ " - " +day+", Month :"+c_month+ " - " +intMonth);
            if (month==c_month) {
               getLectures_list_month().add(tmp);
            }
        }

        getLectures_list_week().clear();

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
                LecturesDBHelper.COLUMN_DESCRIPTION,
                LecturesDBHelper.COLUMN_ATTENDANCE,
                LecturesDBHelper.COLUMN_FEEDBACK

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
            String attendance = dbCursor.getString(dbCursor.getColumnIndexOrThrow(LecturesDBHelper.COLUMN_ATTENDANCE));
            String feedback = dbCursor.getString(dbCursor.getColumnIndexOrThrow(LecturesDBHelper.COLUMN_FEEDBACK));
            if(feedback==null)
                feedback="false";


            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
//
            Date tmpt_start=null;
            Date tmpt_end=null;
            try {
                tmpt_start = format.parse(start);
                tmpt_end = format.parse(end);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


            tmpList.add(new Lecture(title, module, type, tmpt_start, tmpt_end, location, description,attendance));


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
        getLectures_list_today().clear();

        for (Lecture tmp : tmpList){



            c1.setTime(tmp.getStart());

            int day = c1.get(Calendar.DAY_OF_MONTH);
            int month = c1.get(Calendar.MONTH)+1;
            if (day==c_day && month==c_month) {
                getLectures_list_today().add(tmp);
                Log.i("Location", tmp.getLocation());
            }
        }

    }

    public void readAttendances(){

        ArrayList<Lecture>  tmpList = new ArrayList<Lecture>();
        String[] allColumns = {
                LecturesDBHelper.COLUMN_TITLE,
                LecturesDBHelper.COLUMN_MODULE,
                LecturesDBHelper.COLUMN_TYPE,
                LecturesDBHelper.COLUMN_START,
                LecturesDBHelper.COLUMN_END,
                LecturesDBHelper.COLUMN_LOCATION,
                LecturesDBHelper.COLUMN_DESCRIPTION,
                LecturesDBHelper.COLUMN_ATTENDANCE,
                LecturesDBHelper.COLUMN_FEEDBACK

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
            String attendance = dbCursor.getString(dbCursor.getColumnIndexOrThrow(LecturesDBHelper.COLUMN_ATTENDANCE));
            String feedback = dbCursor.getString(dbCursor.getColumnIndexOrThrow(LecturesDBHelper.COLUMN_FEEDBACK));
            if(feedback==null)
                feedback="false";
            Log.i("Feedback",feedback);

            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
//            TimeZone timeZone = TimeZone.getTimeZone("GMT");
//            format.setTimeZone(timeZone);
            Date tmpt_start=null;
            Date tmpt_end=null;
            try {
                tmpt_start = format.parse(start);
                tmpt_end = format.parse(end);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            if (attendance.equals("true") && feedback.equals("false"))
            tmpList.add(new Lecture(title, module, type, tmpt_start, tmpt_end, location, description,attendance));


        }
        Collections.sort(tmpList, Collections.reverseOrder());
        setAttendances_list(tmpList);



    }

    public void readAttendances(int month){

        ArrayList<Lecture>  tmpList = new ArrayList<Lecture>();
        String[] allColumns = {
                LecturesDBHelper.COLUMN_TITLE,
                LecturesDBHelper.COLUMN_MODULE,
                LecturesDBHelper.COLUMN_TYPE,
                LecturesDBHelper.COLUMN_START,
                LecturesDBHelper.COLUMN_END,
                LecturesDBHelper.COLUMN_LOCATION,
                LecturesDBHelper.COLUMN_DESCRIPTION,
                LecturesDBHelper.COLUMN_ATTENDANCE,
                LecturesDBHelper.COLUMN_FEEDBACK

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
            String attendance = dbCursor.getString(dbCursor.getColumnIndexOrThrow(LecturesDBHelper.COLUMN_ATTENDANCE));
            String feedback = dbCursor.getString(dbCursor.getColumnIndexOrThrow(LecturesDBHelper.COLUMN_FEEDBACK));
            if(feedback==null)
                feedback="false";
            Log.i("Feedback",feedback);

            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
//            TimeZone timeZone = TimeZone.getTimeZone("GMT");
//            format.setTimeZone(timeZone);
            Date tmpt_start=null;
            Date tmpt_end=null;
            try {
                tmpt_start = format.parse(start);
                tmpt_end = format.parse(end);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            if (attendance.equals("true") && feedback.equals("false"))
                tmpList.add(new Lecture(title, module, type, tmpt_start, tmpt_end, location, description,attendance));


        }
        Collections.sort(tmpList, Collections.reverseOrder());

        getAttendances_list().clear();
        Calendar c1 = Calendar.getInstance();

        for (Lecture tmp : tmpList){
            c1.setTime(tmp.getStart());
            int tmp_month = c1.get(Calendar.MONTH)+1;
            if (tmp_month==month) {
                getAttendances_list().add(tmp);
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
     public void load_today_lectures(){
         try {
             opendb_read();
             read_db_today();
         } catch (SQLException e) {
             e.printStackTrace();
         }
         close();

     }

}
