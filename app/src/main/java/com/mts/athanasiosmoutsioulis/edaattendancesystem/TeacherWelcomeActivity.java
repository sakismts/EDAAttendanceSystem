package com.mts.athanasiosmoutsioulis.edaattendancesystem;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Set;

public class TeacherWelcomeActivity extends AppCompatActivity {
    AttendanceModel model=AttendanceModel.getOurInstance();

    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private LinearLayout dotsLayout;
    private TextView[] dots;
    private int[] layouts;
    private Button btnPrevious, btnNext;
    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedpreferences;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        // Checking for first time launch - before calling setContentView()


        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        setContentView(R.layout.activity_welcome);


        viewPager = (ViewPager) findViewById(R.id.view_pager);
        dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
        btnPrevious = (Button) findViewById(R.id.btn_skip);
        btnNext = (Button) findViewById(R.id.btn_next);


        // layouts of all welcome sliders
        // add few more layouts if you want
        layouts = new int[]{
                R.layout.welcome_teacher1,
                R.layout.welcome_teacher2,
                R.layout.welcome_teacher3,
                R.layout.welcome_teacher4};

        // adding bottom dots
        addBottomDots(0);

        // making notification bar transparent
        changeStatusBarColor();

        myViewPagerAdapter = new MyViewPagerAdapter(this);
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
        System.out.println(getJSONArray(model.getMyModules()));
        editor.putString("MyModulesString", getJSONArray(model.getMyModules()).toString());
        editor.commit();
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    public JSONObject getJSONArray(Set tmp) {
        JSONArray JsonArray=new JSONArray();
        for(String tmpString:model.getMyModules()){
            JSONObject obj = new JSONObject();
            try {
                obj.put("moduleID", tmpString);
            } catch (JSONException e) {
                Log.i("JSONException: ", e.getMessage());
            }
            JsonArray.put(obj);
        }
        JSONObject modulesObj = new JSONObject();
        try {
            modulesObj.put("modules", JsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return modulesObj;
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
                btnNext.setText("NEXT");
                btnPrevious.setVisibility(View.VISIBLE);
                btnNext.setTextColor(Color.WHITE);
            }else if (position==3){
                if(model.getMyModules().isEmpty()){
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
    public class MyViewPagerAdapter extends PagerAdapter implements AttendanceModel.OnGetModulesListener{

        private LinearLayoutManager mLayoutManager;
        AdapterAvailableModules mAdapter;
        private LayoutInflater layoutInflater;
        private Context context;

        public MyViewPagerAdapter(Context context) {
            this.context=context;
            model.setGetModulesListener(this);
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
                RecyclerView modulesRecycler = (RecyclerView)view. findViewById(R.id.recycler_view);
                modulesRecycler.setHasFixedSize(true);


                // use a linear layout manager
                mLayoutManager = new LinearLayoutManager(context);
                mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                mLayoutManager.scrollToPosition(0);
                modulesRecycler.setLayoutManager(mLayoutManager);
                mAdapter = new AdapterAvailableModules(context,true);
                modulesRecycler.setAdapter(mAdapter);
                String uri = "http://greek-tour-guides.eu/ioannina/dissertation/getModules.php";
                Log.i("URI", uri.toString());
                model.getmodules(uri);

            }else if (position==2){



            }else if (position==3){
                TextView header=(TextView)view.findViewById(R.id.tv_welc_header);
                TextView desc=(TextView)view.findViewById(R.id.tv_welc_desc);
                if(model.getMyModules().isEmpty()){
                    header.setText("Not Finished!");
                    desc.setText("Please go back to add your modules");
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

        @Override
        public void getModules(boolean modules) {
            mAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public void onBackPressed(){
        // do something here and don't write super.onBackPressed()
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }



}