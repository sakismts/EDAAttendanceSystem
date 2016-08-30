package com.mts.athanasiosmoutsioulis.edaattendancesystem;

import android.app.ActionBar;
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
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.MenuItemCompat;
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
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, MainFragment.OnFragmentInteractionListener, MainFragment.OnLoadCalendarFeeds, MainFragmentTeacher.OnTeacherFragmentInteractionListener {
    AttendanceModel model;

    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedpreferences;
    static final int FBLOGIN_REQUEST = 2;  // The request code
    CalendarBuilder builder;
    Spinner spinner;
    final static String MY_ACTION = "MY_ACTION";


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

        Log.i("Create", "Activity created");


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        System.out.println(sharedpreferences.getString("MyModulesString","empty"));
        String tmp_modules=sharedpreferences.getString("MyModulesString","empty");
        if(!tmp_modules.equals("empty")){
            try {
                JSONObject obj=new JSONObject(tmp_modules);
                JSONArray tmp_jsonArray=obj.getJSONArray("modules");
                model.getMyModules().clear();
                for (int i=0; i<tmp_jsonArray.length();i++){
                    model.getMyModules().add(tmp_jsonArray.getJSONObject(i).getString("moduleID"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        System.out.println(model.getMyModules());


        //Load Main activity fragment
        if (sharedpreferences.getString("role","nothing").equals("Teacher")){
            navigationView.getMenu().clear(); //clear old inflated items.
            navigationView.inflateMenu(R.menu.activity_main_drawer_teacher); //inflate new items.

            //System.out.println("the size in main Activity of my modules is "+myModules.size());
            if (findViewById(R.id.main_fragment_teacher) != null) {

                if(sharedpreferences.getBoolean("IsFirstTimeLaunch",true)==true){
                    Intent welcome=new Intent(this,TeacherWelcomeActivity.class);
                    startActivityForResult(welcome,1);

                }
                // However, if we're being restored from a previous state,
                // then we don't need to do anything and should return or else
                // we could end up with overlapping fragments.
                if (savedInstanceState != null) {
                    return;
                }

                // Create a new Fragment to be placed in the activity layout
                Fragment fragment = new MainFragmentTeacher();

                // In case this activity was started with special instructions from an
                // Intent, pass the Intent's extras to the fragment as arguments

                // Add the fragment to the 'fragment_container' FrameLayout
                android.app.FragmentManager fragmentManager = getFragmentManager();
                android.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                fragmentTransaction.add(R.id.main_fragment_teacher,fragment).commit();
            }

        }else {

            if(sharedpreferences.getBoolean("IsFirstTimeLaunch",true)==true){
                Intent welcome=new Intent(this,WelcomeActivity.class);
                //startActivity(welcome);
                startActivityForResult(welcome,1);

            }
            navigationView.getMenu().clear(); //clear old inflated items.
            navigationView.inflateMenu(R.menu.activity_main_drawer); //inflate new items.
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

                fragmentTransaction.add(R.id.main_fragment, fragment).commit();
            }



        }






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
        if (sharedpreferences.getString("role","nothing").equals("Teacher")){
            getMenuInflater().inflate(R.menu.main, menu);
        } else{
        getMenuInflater().inflate(R.menu.main, menu);
        }

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
           // showDialog_CalendarImport();
            if (sharedpreferences.getString("role","nothing").equals("Teacher")){
                Intent setting=new Intent(this,TeacherPreferences.class);
                startActivity(setting);
            }else{

                Intent setting=new Intent(this,PreferencesActivity.class);
                startActivity(setting);
            }

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
                            editor.putBoolean("IsFirstTimeLaunch", true);
                            editor.putString("MyModulesString", "");
                            editor.putString("fullName", "");
                            editor.putString("FBname", "");
                            editor.putString("pass", "");
                            editor.putString("role", "");
                            editor.putBoolean("logged", false);
                            editor.putString("photo_profile", "");
                            editor.commit();
                            LoginManager.getInstance().logOut();
                            Intent mserviceIntent = new Intent(MainActivity.this, DownloadBeaconIdsService.class);
                            stopService(mserviceIntent);
//                            FragmentManager fragmentManager = getFragmentManager();
//                            MainFragment fragment = (MainFragment) fragmentManager.findFragmentById(R.id.main_fragment);
//                            fragment.update_logout();
                            //nav header details
                           // update_header_details();

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

        } else if (id == R.id.cources) {
            Intent mycources=new Intent(this,MyModules.class);
            startActivity(mycources);



        } else if (id == R.id.map) {
            Intent mapIntent = new Intent(this, MapsActivity.class);
            startActivity(mapIntent);
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        } else if (id == R.id.about) {
            Intent aboutIntent =  new Intent(this, AboutActivity.class);
            startActivity(aboutIntent);
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        } else if (id == R.id.telephones) {
            Intent telephonesIntent =  new Intent(this, TelephonesActivity.class);
            startActivity(telephonesIntent);
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        SystemRequirementsChecker.checkWithDefaultDialogs(this);
        String tmp_modules=sharedpreferences.getString("MyModulesString","empty");
        if(!tmp_modules.equals("empty")){
            try {
                JSONObject obj=new JSONObject(tmp_modules);
                JSONArray tmp_jsonArray=obj.getJSONArray("modules");
                model.getMyModules().clear();
                for (int i=0; i<tmp_jsonArray.length();i++){
                    model.getMyModules().add(tmp_jsonArray.getJSONObject(i).getString("moduleID"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else {
            model.getMyModules().clear();
        }
        System.out.println(model.getMyModules());
        invalidateOptionsMenu();
        update_header_details();

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

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

        }else if (requestCode == 1) {

            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
                finish();
            }
        }
    }
    public void update_header_details(){
        System.out.println("the Name is "+sharedpreferences.getString("fullName", " "));
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View hView =  navigationView.getHeaderView(0);
       // navigationView.setItemIconTintList(null);

        ImageView nav_img= (ImageView)hView.findViewById(R.id.nav_img);
        TextView student_id=(TextView)hView.findViewById(R.id.header_id);
        TextView fbname=(TextView)hView.findViewById(R.id.header_fbName);
        student_id.setText(sharedpreferences.getString("id", "User"));
        if (sharedpreferences.getString("role","nothing").equals("Teacher")){
            fbname.setText(sharedpreferences.getString("fullName", " "));
            fbname.setVisibility(View.VISIBLE);
            LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,90);
            nav_img.setLayoutParams(lp);
            nav_img.getLayoutParams().height = (int) getResources().getDimension(R.dimen.profile_image);
            nav_img.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
            nav_img.setScaleType(ImageView.ScaleType.CENTER_CROP);

        }else {
            Profile profile = Profile.getCurrentProfile();
            if (profile != null) {
                String previouslyEncodedImage = sharedpreferences.getString("photo_profile", "");
                if (!previouslyEncodedImage.equalsIgnoreCase("")) {
                    byte[] b = Base64.decode(previouslyEncodedImage, Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
                    RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
                    roundedBitmapDrawable.setCornerRadius(25);
                    nav_img.setImageDrawable(roundedBitmapDrawable);
                }

                fbname.setText(profile.getName().toString());
                fbname.setVisibility(View.VISIBLE);
            } else {
                Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.photo_profile);
                RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), imageBitmap);
                roundedBitmapDrawable.setCornerRadius(25);
                nav_img.setImageDrawable(roundedBitmapDrawable);
                fbname.setText(sharedpreferences.getString("fullName", " "));
                fbname.setVisibility(View.VISIBLE);
            }
        }

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onLoadCalendarFeeds() {
        //showDialog_CalendarImport();

    }

    @Override
    public void onTeacherFragmentInteraction(Uri uri) {

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






}
