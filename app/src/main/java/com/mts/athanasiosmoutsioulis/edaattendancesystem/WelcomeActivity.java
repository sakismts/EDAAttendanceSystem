package com.mts.athanasiosmoutsioulis.edaattendancesystem;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Property;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Iterator;

public class WelcomeActivity extends AppCompatActivity {
    AttendanceModel model=AttendanceModel.getOurInstance();

    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private LinearLayout dotsLayout;
    private TextView[] dots;
    private int[] layouts;
    private Button btnPrevious, btnNext;
    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedpreferences;
    CalendarBuilder builder;
    private TextView info;
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;
    private  boolean timetable_load=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {
            }
        };

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
                if(newProfile != null){
                    info.setText("Youu are logged as : "+newProfile.getName());
                    Uri imageUrl= newProfile.getProfilePictureUri(300, 300);
                    Log.i("Facebook", imageUrl.toString());
                    Log.i("FacebbokId", newProfile.getId());
                    String user_id=sharedpreferences.getString("id", "User");
                    model.updateFBaccount("http://greek-tour-guides.eu/ioannina/dissertation/updateFBaccount.php?student_id="+user_id+"&fbID="+newProfile.getId()+"&fbName="+newProfile.getFirstName()+"_"+newProfile.getLastName());
                    new DownloadImage((ImageView)findViewById(R.id.imageView)).execute(imageUrl.toString());
                    model.setFbName(newProfile.getName());
                    btnNext.setText("NEXT");
                }else{
                    ImageView img = (ImageView)findViewById(R.id.imageView);
                    img.setImageResource(R.drawable.photo_profile);
                    model.setFbName("");
                    info.setText("You are not logged in to Facebook");
                }
            }
        };
        accessTokenTracker.startTracking();
        profileTracker.startTracking();
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        // Checking for first time launch - before calling setContentView()


        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        setContentView(R.layout.activity_welcome);
        builder = new CalendarBuilder();

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
        btnPrevious = (Button) findViewById(R.id.btn_skip);
        btnNext = (Button) findViewById(R.id.btn_next);


        // layouts of all welcome sliders
        // add few more layouts if you want
        layouts = new int[]{
                R.layout.welcome_slide1,
                R.layout.welcome_slide2,
                R.layout.welcome_slide3,
                R.layout.welcome_slide4};

        // adding bottom dots
        addBottomDots(0);

        // making notification bar transparent
        changeStatusBarColor();

        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.setOnTouchListener(null);

        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int current = getItem(-1);
                if (current >= 0) {
                    // move to next screen
                    viewPager.setCurrentItem(current);
                }
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // checking for last page
                // if last page home screen will be launched
                int current = getItem(+1);
                if (current < layouts.length) {
                    // move to next screen
                    viewPager.setCurrentItem(current);
                } else {
                    launchHomeScreen();
                }
            }
        });





    }

    @Override
    public void onBackPressed(){
        // do something here and don't write super.onBackPressed()
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }


    private void addBottomDots(int currentPage) {
        dots = new TextView[layouts.length];

        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorsInactive[currentPage]);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(colorsActive[currentPage]);
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    private void launchHomeScreen() {
        SharedPreferences.Editor editor=sharedpreferences.edit();
        editor.putBoolean("IsFirstTimeLaunch", false);
        editor.commit();
        scheduleAlarm();
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }






    //  viewpager change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {



        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);

            // changing the next button text 'NEXT' / 'GOT IT'
            if (position==0){
                btnPrevious.setVisibility(View.GONE);
                btnNext.setTextColor(Color.WHITE);
            }else if (position==1){
                btnNext.setText("NEXT");
                btnPrevious.setVisibility(View.VISIBLE);
            }else if (position==2){
                btnNext.setEnabled(true);
                btnNext.setText("SKIP");
                btnPrevious.setVisibility(View.VISIBLE);
                btnNext.setTextColor(Color.WHITE);
            }else if (position==3){
                if(!timetable_load){
                    btnNext.setEnabled(false);
                    btnNext.setText(getString(R.string.start));
                    btnPrevious.setVisibility(View.VISIBLE);
                    btnNext.setTextColor(Color.GRAY);
                }else{
                    btnNext.setText(getString(R.string.start));
                    btnNext.setEnabled(true);
                    viewPager.beginFakeDrag();
                    btnPrevious.setVisibility(View.GONE);
                    btnNext.setTextColor(Color.WHITE);
                }
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }

    };

    /**
     * Making notification bar transparent
     */
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    /**
     * View pager adapter
     */
    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {
        }


        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            final View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);
            if(position==0){
                if(viewPager.getCurrentItem()==0)
                btnPrevious.setVisibility(View.GONE);
            }
            else if(position==1){
                Button import_timetable = (Button) view.findViewById(R.id.btn_import_timetable);
               final EditText feed_url=(EditText)view.findViewById(R.id.edt_calendar_url);
                import_timetable.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String calendar_url= feed_url.getText().toString();
                        calendar_url=calendar_url.replace("webcal","http");
                        Log.i("calendar", calendar_url);
                        model.deleteAllItems();
                        new DownloadFileFromURL().execute(calendar_url);
                    }
                });
            }else if (position==2){

                setFinishOnTouchOutside(false);
                callbackManager = CallbackManager.Factory.create();
                sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                loginButton = (LoginButton)view.findViewById(R.id.login_button);
                info = (TextView)view.findViewById(R.id.info);
                // Button cancel = (Button)findViewById(R.id.btn_cancel);
                //Load the profile photo from shared preferences
                Profile profile = Profile.getCurrentProfile();
                if (profile != null) {
                    String previouslyEncodedImage = sharedpreferences.getString("photo_profile", "");
                    if( !previouslyEncodedImage.equalsIgnoreCase("") ){
                        byte[] b = Base64.decode(previouslyEncodedImage, Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
                        ImageView img = (ImageView)view.findViewById(R.id.imageView);
                        img.setImageBitmap(bitmap);
                    }
                    info.setText("Youu are logged as : "+profile.getName().toString());
                }
                loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        AccessToken accessToken = loginResult.getAccessToken();
                        Profile profile = Profile.getCurrentProfile();
                        if (profile != null) {
                            System.out.println(profile.getName());
                            info.setText("You are logged as : "+ profile.getName());
                            model.setFbName(profile.getName());
                            Uri imageUrl = profile.getProfilePictureUri(300, 300);
                            // new DownloadImage((ImageView) findViewById(R.id.imageView)).execute(imageUrl.toString());
                        }

                    }

                    @Override
                    public void onCancel() {
                        info.setText("Login attempt canceled.");

                    }

                    @Override
                    public void onError(FacebookException e) {

                        info.setText("Login attempt failed.");
                    }
                });

                loginButton.setReadPermissions("user_friends");




            }else if (position==3){
                TextView header=(TextView)view.findViewById(R.id.tv_welc_header);
                TextView desc=(TextView)view.findViewById(R.id.tv_welc_desc);
                if(!timetable_load){
                header.setText("Not Finished!");
                desc.setText("Please go back to import your timetable");
                }else{
                    header.setText("Finished!");
                    desc.setText("You have finished your configuration!");
                }
            }

            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
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
            dialog = new ProgressDialog(WelcomeActivity.this);
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dialog.setMessage("Importing your timetable...");
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
                timetable_load=true;

            } catch (Exception e) {
                timetable_load=false;
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
            editor.putBoolean("Calendar", true);
            editor.commit();
            model.load_today_lectures();
            if(timetable_load)
            Toast.makeText(WelcomeActivity.this,"You have successfully imported your timetable!",Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(WelcomeActivity.this,"Failed!Check your internet connection.",Toast.LENGTH_SHORT).show();
           

        }

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

    }

    public class DownloadImage extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        private ProgressDialog dialog;

        public DownloadImage(ImageView bmImage) {
            this.bmImage = bmImage;
            dialog = new ProgressDialog(WelcomeActivity.this);
//
        }



        @Override
        protected void onPreExecute() {

            dialog.setMessage("Loading photo profile...");
            dialog.show();
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            //saving image to shared preferences
            Log.i("Facebook","Finished image downloading ");
            Bitmap tmp=result;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            tmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] b = baos.toByteArray();
            String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
            SharedPreferences.Editor edit=sharedpreferences.edit();
            edit.putString("photo_profile", encodedImage);
            edit.commit();
            ///
            bmImage.setImageBitmap(result);
            dialog.dismiss();



        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }



}