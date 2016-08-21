package com.mts.athanasiosmoutsioulis.edaattendancesystem;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Property;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Iterator;

public class PreferencesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
        setupActionBar();
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public static class MyPreferenceFragment extends PreferenceFragment
    {
        AttendanceModel model=AttendanceModel.getOurInstance();
        CalendarBuilder builder;
        public static final String MyPREFERENCES = "MyPrefs" ;
        SharedPreferences sharedpreferences;
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
            builder = new CalendarBuilder();

            sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            Preference myPref = (Preference) findPreference("timetable");
            myPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    //open browser or intent here
                    Log.i("timetable", "Load timetable");
                    showDialog_CalendarImport();
                    //Toast.makeText(getActivity(),"Search history is cleared",Toast.LENGTH_SHORT);
                    return true;
                }
            });

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.

        }


        public void showDialog_CalendarImport(){
            AlertDialog.Builder builder_calendar = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();
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
                dialog = new ProgressDialog(getActivity());
            }
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                dialog.setMessage("Loading your calendar info...");
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
            AlarmManager alarmManager = (AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(getActivity(), AlarmReciever.class);
            PendingIntent alarmIntent = PendingIntent.getBroadcast(getActivity(), 1, intent, 0);
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
            Toast.makeText(getActivity(), "You have successfully imported your timetable!", Toast.LENGTH_SHORT).show();

        }

    }

    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();


                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);

            return true;
        }
    };



}