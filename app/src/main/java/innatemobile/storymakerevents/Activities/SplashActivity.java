package innatemobile.storymakerevents.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.content.SharedPreferencesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
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
        // run your one time code
        ConnectivityManager cm =
                (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if (!prefs.getBoolean("firstTimeSplash", false)) {
            if(isConnected) {
                requester = new RequestSpreadsheets(this, false, true, false);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("firstTimeSplash", true);
                editor.apply();
            }else{
                Snackbar.make(findViewById(R.id.splash_main), "No Connection, please try again later.", Snackbar.LENGTH_LONG).show();
                Intent i = new Intent(this, MainActivity.class);
                startActivity(i);
            }

        } else {
            if(isConnected) {
                requester = new RequestSpreadsheets(this, false, false, false);
            }else{
                Snackbar.make(findViewById(R.id.splash_main), "No Internet Connection.", Snackbar.LENGTH_LONG).show();
                Intent i = new Intent(this, MainActivity.class);
                startActivity(i);
            }
        }
        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/
        /*getApplicationContext().deleteDatabase("storymakersSchedule");*/
        if(isConnected) {
            requester.getSpreadsheetKeys();
        }else{
            Snackbar.make(findViewById(R.id.splash_main), "No Internet Connection.", Snackbar.LENGTH_LONG).show();
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
        }
    }

    @Override
    public void communicateNotificationResult() {

    }
}