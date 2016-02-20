package innatemobile.storymakerevents.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.SharedPreferencesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import innatemobile.storymakerevents.Models.Spreadsheets;
import innatemobile.storymakerevents.R;
import innatemobile.storymakerevents.Utils.AppController;
import innatemobile.storymakerevents.Utils.DatabaseHandler;
import innatemobile.storymakerevents.Utils.ParseJSON;
import innatemobile.storymakerevents.Utils.RequestSpreadsheets;

/**
 * Created by rphovley on 1/29/2016.
 */
public class SplashActivity extends AppCompatActivity implements RequestSpreadsheets.iRequestSheet{

    private static final String TAG_REQUEST_STRING = "request";
    private static final String TAG_GET_SCHEDULE = "schedule";
    private static final String TAG_GET_PRESENTATION = "presentation";
    private static final String TAG_GET_BREAKOUT = "breakout";
    private static final String TAG_GET_SPEAKER = "speaker";
    private boolean hasSchedule, hasSpeaker, hasBreakout, hasPresentation, hasNotification = false;
    private RequestSpreadsheets requester;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_main);
        System.setProperty("http.keepAlive", "false");
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(this);
        if (!prefs.getBoolean("firstTime", false)) {

            // run your one time code
            requester = new RequestSpreadsheets(this, false, true, false);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("firstTime", true);
            editor.commit();
        } else {
            requester = new RequestSpreadsheets(this, false, false, false);
        }
        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/
        /*getApplicationContext().deleteDatabase("storymakersSchedule");*/

        requester.getSpreadsheetKeys();
    }

    @Override
    public void communicateNotificationResult() {

    }
}