package com.mts.athanasiosmoutsioulis.edaattendancesystem;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.WindowManager;

import com.estimote.sdk.SystemRequirementsChecker;

public class LoginActivity extends AppCompatActivity implements LoginFragment.OnLoginListener, SignupFragment.OnFragmentInteractionListener{
    AttendanceModel model = AttendanceModel.getOurInstance();
    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        if (model==null)
            model = new AttendanceModel(this);


        if (findViewById(R.id.login_fragment) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            Fragment fragment = new LoginFragment();

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments

            // Add the fragment to the 'fragment_container' FrameLayout
            android.app.FragmentManager fragmentManager = getFragmentManager();
            android.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            fragmentTransaction.add(R.id.login_fragment,fragment).commit();
        }


    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onLoginClicked() {
//        Intent returnIntent = new Intent();
//        returnIntent.putExtra("result", true);
//        setResult(Activity.RESULT_OK, returnIntent);
//        finish();
        Intent mainActivity= new Intent(this,MainActivity.class);
        mainActivity.putExtra("logged",true);
        startActivity(mainActivity);
        finish();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
    @Override
    public void onResume() {
        super.onResume();
        SystemRequirementsChecker.checkWithDefaultDialogs(this);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        System.out.println(sharedpreferences.getBoolean("logged", false));
        System.out.println(sharedpreferences.getBoolean("IsFirstTimeLaunch",true));
        if (sharedpreferences.getBoolean("logged", false)== true && sharedpreferences.getBoolean("IsFirstTimeLaunch",true)==false){
            Intent mainActivity= new Intent(this,MainActivity.class);
            mainActivity.putExtra("logged",true);
            startActivity(mainActivity);
            finish();

        }


    }
}
