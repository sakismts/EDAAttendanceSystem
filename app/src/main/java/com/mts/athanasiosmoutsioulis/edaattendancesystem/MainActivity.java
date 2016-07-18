package com.mts.athanasiosmoutsioulis.edaattendancesystem;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Fragment;
import android.app.FragmentManager;
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
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.estimote.sdk.SystemRequirementsChecker;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Property;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, MainFragment.OnFragmentInteractionListener, MainFragment.OnLoadCalendarFeeds {
    AttendanceModel model;

    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedpreferences;
    static final int FBLOGIN_REQUEST = 2;  // The request code
    CalendarBuilder builder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        setContentView(R.layout.activity_main);




        model=AttendanceModel.getOurInstance();
        if (model==null)
        model= new AttendanceModel(this);
        builder = new CalendarBuilder();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //Load Main activity fragment
        if (findViewById(R.id.main_fragment) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            Fragment fragment = new MainFragment();

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments

            // Add the fragment to the 'fragment_container' FrameLayout
            android.app.FragmentManager fragmentManager = getFragmentManager();
            android.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            fragmentTransaction.add(R.id.main_fragment,fragment).commit();
        }

        /// update the login details
        Profile profile = Profile.getCurrentProfile();
                    if (profile==null){
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("Facebook Login")
                                .setMessage("Do you also want to login with Facebook")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent facebookLogin = new Intent(MainActivity.this, FacebookActivity.class);
                                        startActivityForResult(facebookLogin, FBLOGIN_REQUEST);
                                    }
                                })
                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // do nothing
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();

                    }
                    update_header_details();





    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            showDialog_CalendarImport();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.menu_logout) {
            // Handle the camera action
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage(R.string.logout_message)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //clear the shared preferences
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putString("id", "");
                            editor.putString("pass", "");
                            editor.putBoolean("logged", false);
                            editor.putString("photo_profile", "");
                            editor.commit();
                            LoginManager.getInstance().logOut();
                            FragmentManager fragmentManager = getFragmentManager();
                            MainFragment fragment = (MainFragment) fragmentManager.findFragmentById(R.id.main_fragment);
                            fragment.update_logout();
                            //nav header details
                            update_header_details();

                            Intent loginActivity = new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(loginActivity);
                            finish();

                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
            // Create the AlertDialog object and return it
            builder.create().show();
        } else if (id == R.id.menu_FBAccount) {
            Intent facebookLogin = new Intent(MainActivity.this, FacebookActivity.class);
            startActivityForResult(facebookLogin, FBLOGIN_REQUEST);

        } /*else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        SystemRequirementsChecker.checkWithDefaultDialogs(this);
        // if the user is not logged than to the the Login screen
//        if (sharedpreferences.getBoolean("logged", false)== false){
//            //the activity login will return a result if the user logged in or no
//            Intent loginActivity = new Intent(MainActivity.this, LoginActivity.class);
//            startActivityForResult(loginActivity, LOGIN_REQUEST);
//        }else{
//            //update the gui
//            FragmentManager fragmentManager = getFragmentManager();
//            MainFragment fragment = (MainFragment) fragmentManager.findFragmentById(R.id.main_fragment);
//            fragment.update_login();
//
//
//        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
//        if (requestCode == LOGIN_REQUEST) {
//            // Make sure the request was successful
//            if (resultCode == RESULT_OK) {
//                Log.i("Request","ok");
//                Boolean result=data.getBooleanExtra("result", false);
//                if (result == true){
//                    //if the user logged in the update the ui with his info
//
//                    Profile profile = Profile.getCurrentProfile();
//                    if (profile==null){
//                        new AlertDialog.Builder(MainActivity.this)
//                                .setTitle("Facebook Login")
//                                .setMessage("Do you also want to login with Facebook")
//                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        Intent facebookLogin = new Intent(MainActivity.this, FacebookActivity.class);
//                                        startActivityForResult(facebookLogin, FBLOGIN_REQUEST);
//                                    }
//                                })
//                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        // do nothing
//                                    }
//                                })
//                                .setIcon(android.R.drawable.ic_dialog_alert)
//                                .show();
//
//                    }
//                    update_header_details();
//                    FragmentManager fragmentManager = getFragmentManager();
//                    MainFragment fragment = (MainFragment) fragmentManager.findFragmentById(R.id.main_fragment);
//                    fragment.update_login();
//
//                }
//
//            }
//            // if the user wasn't logged in the close the application
//            if (resultCode == Activity.RESULT_CANCELED) {
//                Log.i("Request","cancel");
//                finish();
//            }
//        }else
        if (requestCode == FBLOGIN_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                Log.i("FBRequest","ok");
                Boolean result=data.getBooleanExtra("result", false);
                if (result == true){
                    //if the user logged in the update the ui with his info
                    update_header_details();
                    FragmentManager fragmentManager = getFragmentManager();
                    MainFragment fragment = (MainFragment) fragmentManager.findFragmentById(R.id.main_fragment);
                    fragment.update_FBlogin();
                }

            }

        }
    }
    public void update_header_details(){
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View hView =  navigationView.getHeaderView(0);

        ImageView nav_img= (ImageView)hView.findViewById(R.id.nav_img);
        TextView student_id=(TextView)hView.findViewById(R.id.header_id);
        TextView fbname=(TextView)hView.findViewById(R.id.header_fbName);
        student_id.setText(sharedpreferences.getString("id", "User"));
        Profile profile = Profile.getCurrentProfile();
        if (profile!=null){
            String previouslyEncodedImage = sharedpreferences.getString("photo_profile", "");
            if( !previouslyEncodedImage.equalsIgnoreCase("") ){
                byte[] b = Base64.decode(previouslyEncodedImage, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
                RoundedBitmapDrawable roundedBitmapDrawable= RoundedBitmapDrawableFactory.create(getResources(), bitmap);
                roundedBitmapDrawable.setCornerRadius(25);
                nav_img.setImageDrawable(roundedBitmapDrawable);
            }
            fbname.setText(profile.getName().toString());
            fbname.setVisibility(View.VISIBLE);
        }else{
            Bitmap imageBitmap=BitmapFactory.decodeResource(getResources(),  R.drawable.photo_profile);
            RoundedBitmapDrawable roundedBitmapDrawable= RoundedBitmapDrawableFactory.create(getResources(), imageBitmap);
            roundedBitmapDrawable.setCornerRadius(25);
            nav_img.setImageDrawable(roundedBitmapDrawable);
            fbname.setVisibility(View.GONE);
        }


    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onLoadCalendarFeeds() {
        showDialog_CalendarImport();

    }

    /**
     * Background Async Task to download file
     * */
    class DownloadFileFromURL extends AsyncTask<String, String, String> {
        private ProgressDialog dialog;
        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         * */
        public DownloadFileFromURL() {
            dialog = new ProgressDialog(MainActivity.this);
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dialog.setMessage("Loading your calendar indo...");
            dialog.show();
        }

        /**
         * Downloading file in background thread
         * */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection conection = url.openConnection();
                String redirect = conection.getHeaderField("Location");
                if (redirect != null){
                    conection = new URL(redirect).openConnection();
                }

                //  conection.connect();
                InputStream input = conection.getInputStream();
                Calendar calendar = null;
                try {
                    calendar = builder.build(input);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ParserException e) {
                    e.printStackTrace();
                }
                System.out.println(calendar);
                SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
                try {
                    model.open();
                    if(!model.isEmpty()){
                        model.deleteAllFromDB();
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                }

                for (Iterator i = calendar.getComponents().iterator(); i.hasNext();) {
                    Component component = (Component) i.next();
                    //System.out.println("Component [" + component.getName() + "]");

                    String title="";
                    String module="";
                    String type="";
                    String start="";
                    String  end="";
                    String location="";
                    String description="";


                    for (Iterator j = component.getProperties().iterator(); j.hasNext();) {
                        Property property = (Property) j.next();
                        switch (property.getName()){
                            case "LOCATION":
                                location = property.getValue();
                                break;
                            case "DTSTART":
                                    start= property.getValue();
                                break;
                            case "DTEND":
                                    end= property.getValue();
                                break;
                            case "SUMMARY":
                                String[] parts = property.getValue().split(" ");
                                type= parts[0];
                                module=parts[1];
                                for (int k=2; k<parts.length;k++)
                                {title=title+" "+parts[k];}
                                break;
                            case "DESCRIPTION":
                                description=property.getValue();
                                break;

                        }

                    }

                    //model.getLectures_list().add(new Lecture(title, module, type, start, end, location, description));
                    model.addLectureDBRecord(title, module, type, start, end, location, description, "false");


                }
                model.close();

                input.close();


            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }

        /**
         * Updating progress bar
         * */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage

        }

        /**
         * After completing background task
         * Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after the file was downloaded

            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putBoolean("Calendar",true);
            editor.commit();
            model.load_today_lectures();
            scheduleAlarm();

        }

    }

    public String readStringFromFile(File file){
        String response="";
        try
        {
            FileInputStream fileInputStream= new FileInputStream(file+"/calendar.txt");
            StringBuilder builder = new StringBuilder();
            int ch;
            while((ch = fileInputStream.read()) != -1){
                builder.append((char)ch);
            }
            response = builder.toString();

        }
        catch (FileNotFoundException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return response;
    }

    public void showDialog_CalendarImport(){
        AlertDialog.Builder builder_calendar = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialog_view=inflater.inflate(R.layout.import_calendar_dialog, null);
        builder_calendar.setView(dialog_view);
        final EditText feed_url= (EditText)dialog_view.findViewById(R.id.edt_calendar_url);

        builder_calendar.setMessage("Paste the url for the caledanr feed")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                        String calendar_url= feed_url.getText().toString();
                        calendar_url=calendar_url.replace("webcal","http");
                        Log.i("calendar",calendar_url);
                        model.deleteAllItems();
                        new DownloadFileFromURL().execute(calendar_url);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });

        builder_calendar.create().show();

    }

    public void scheduleAlarm()
    {

        // Set the alarm to start at approximately 8:00 p.m.
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 8);
        calendar.set(java.util.Calendar.MINUTE, 01);

        // Intent intentAlarm = new Intent(getActivity(), AlarmReciever.class);

        // create the object
        //  AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        AlarmManager alarmManager = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReciever.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 1, intent, 0);
        // constants--in this case, AlarmManager.INTERVAL_DAY.

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, alarmIntent);

        SharedPreferences.Editor prefsEditor = sharedpreferences.edit();
        prefsEditor.putString("BeaconList","empty");
        prefsEditor.commit();

        //set the alarm for particular time
//        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
//                AlarmManager.INTERVAL_DAY, PendingIntent.getBroadcast(getActivity(),1,  intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));
//
        Toast.makeText(this, "Alarm Scheduled for 08:01", Toast.LENGTH_SHORT).show();

    }
}
