package innatemobile.storymakerevents.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import innatemobile.storymakerevents.R;
import innatemobile.storymakerevents.Utils.AppController;
import innatemobile.storymakerevents.Utils.RequestSpreadsheets;

/**
 * Created by rphovley on 1/29/2016.
 * The splash activity is the initial screen that appears
 * when the user opens the app.  It requests the scheduling information.
 */
public class SplashActivity extends AppCompatActivity implements RequestSpreadsheets.iRequestSheet{

    private static final int SPLASH_TIME_OUT = 5000;
    private boolean hasRequestFinished = false;
    private RequestSpreadsheets requester;
    View splash_logo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_main);
        splash_logo = findViewById(R.id.splash_logo);
        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT){  //svg problem for splash image on devices pre kitkat
            splash_logo.setVisibility(View.GONE);
        }
        disableConnectionReuseIfNecessary();
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
                requester.getSpreadsheetKeys();
            }else{
                Snackbar.make(findViewById(R.id.splash_main), "No Connection, please try again later.", Snackbar.LENGTH_LONG).show();
                Intent i = new Intent(this, MainActivity.class);
                startActivity(i);
            }

        } else {
            if(isConnected) {
                requester = new RequestSpreadsheets(this, false, false, false);
                requester.getSpreadsheetKeys();
            }else{
                Snackbar.make(findViewById(R.id.splash_main), "No Internet Connection.", Snackbar.LENGTH_LONG).show();
                Intent i = new Intent(this, MainActivity.class);
                startActivity(i);
            }
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!hasRequestFinished) {
                    requester.has_request_expired = true;
                    AppController.switchToMain(SplashActivity.this, 0, 0);
                }
            }
        }, SPLASH_TIME_OUT);
    }

    @Override
    public void communicateNotificationResult() {
        hasRequestFinished = true;
    }
    /**
     * Helps to fix a volley bug with phones that are older.  Still doesn't fix the
     * problem with devices that are Samsung version 4.2, SDK 17.  Like the SG3
     * */
    private void disableConnectionReuseIfNecessary() {
        // HTTP connection reuse which was buggy pre-froyo
        if (Integer.parseInt(Build.VERSION.SDK) < Build.VERSION_CODES.FROYO) {
            System.setProperty("http.keepAlive", "false");
        }
    }
}