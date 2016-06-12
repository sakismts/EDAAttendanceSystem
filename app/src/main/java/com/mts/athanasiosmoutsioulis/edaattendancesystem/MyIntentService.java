package com.mts.athanasiosmoutsioulis.edaattendancesystem;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.estimote.sdk.repackaged.retrofit_v1_9_0.retrofit.RestAdapter;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class MyIntentService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    public static final String ACTION_FOO = "com.mts.athanasiosmoutsioulis.edaattendancesystem.action.FOO";
    public static final String ACTION_BAZ = "com.mts.athanasiosmoutsioulis.edaattendancesystem.action.BAZ";

    // TODO: Rename parameters
    public static final String EXTRA_PARAM1 = "com.mts.athanasiosmoutsioulis.edaattendancesystem.extra.PARAM1";
    public static final String EXTRA_PARAM2 = "com.mts.athanasiosmoutsioulis.edaattendancesystem.extra.PARAM2";
    int counter = 0;

    public MyIntentService() {
        super("MyIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i("Intent","running");

        if (intent != null) {

            int delay = 5000; // delay for 5 sec.
            int period = 1000; // repeat every sec.
            Timer timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask()
            {
                public void run()
                {
                    // Your code

                    counter++;
                    Log.i("Counter: ",Integer.toString(counter));


                }
            }, delay, period);

//            final String action = intent.getAction();
//            if (ACTION_FOO.equals(action)) {
//                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
//                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
//                handleActionFoo(param1, param2);
//            } else if (ACTION_BAZ.equals(action)) {
//                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
//                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
//                handleActionBaz(param1, param2);
//            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }


}
