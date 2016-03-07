package innatemobile.storymakerevents.Utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;

import java.util.List;

import innatemobile.storymakerevents.Activities.MainActivity;
import innatemobile.storymakerevents.Models.Breakouts;
import innatemobile.storymakerevents.Models.Schedules;
import innatemobile.storymakerevents.Models.Spreadsheets;
import innatemobile.storymakerevents.R;

/**
 * Created by rphovley on 2/14/2016.
 */
public class RequestSpreadsheets {
    private static final String TAG_REQUEST_STRING   = "request" ;
    private static final String TAG_GET_SCHEDULE     = "schedule";
    private static final String TAG_GET_PRESENTATION = "presentation";
    private static final String TAG_GET_BREAKOUT     = "breakout";
    private static final String TAG_GET_SPEAKER      = "speaker";
    private boolean hasSchedule, hasSpeaker, hasBreakout, hasPresentation, isSynch, keepScreen = false;
    private boolean isFirst = true;
    private Activity activity;
    private ProgressDialog progress;
    private iRequestSheet iRequest;

    public RequestSpreadsheets(Activity activity, boolean isSynch, boolean isFirst, boolean keepScreen) {
        this.activity = activity;
        this.isSynch = isSynch;
        this.isFirst = isFirst;
        this.keepScreen = keepScreen;
        this.setCommunicator((iRequestSheet) activity);
    }

    public void getSpreadsheetKeys() {
        String url = activity.getString(R.string.spreadsheet_url) + activity.getString(R.string.spreadsheet_key);
        StringRequest strReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                /*Log.d(TAG, response.toString());*/
                DatabaseHandler dh = new DatabaseHandler(activity);
                if(isSynch){
                    progress = new ProgressDialog(activity);
                    progress.setMessage("Getting most recent Storymaker's schedule and Notifications.");
                    progress.show();
                    dh.clearForSynch();
                    //activity.deleteDatabase("storymakersSchedule");
                }
                ParseJSON parsed = new ParseJSON(activity);
                parsed.uploadSheet(response, ParseJSON.SPREADSHEET_CALL);



                if(isFirst || isSynch) {
                    getScheduleSpreadsheet(activity.getString(R.string.spreadsheet_url) + dh.getSpreadsheetKey(Spreadsheets.SCHEDULES_SHEET));
                    getBreakoutSpreadsheet(activity.getString(R.string.spreadsheet_url) + dh.getSpreadsheetKey(Spreadsheets.BREAKOUTS_SHEET));
                    getPresentationSpreadsheet(activity.getString(R.string.spreadsheet_url) + dh.getSpreadsheetKey(Spreadsheets.PRESENTATIONS_SHEET));
                    getSpeakerSpreadsheet(activity.getString(R.string.spreadsheet_url) + dh.getSpreadsheetKey(Spreadsheets.SPEAKERS_SHEET));
                    getNotificationSpreadsheet(activity.getString(R.string.spreadsheet_url) + dh.getSpreadsheetKey(Spreadsheets.NOTIFICATIONS_SHEET));
                }else{ //if it isn't the first time, and we aren't synching the data, send them to the main activity
                    Intent i = new Intent(activity, MainActivity.class);
                    activity.startActivity(i);
                    activity.finish();
                    dh.close();
                }
                dh.close();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Request Spreadsheet:", "Error: " + error.getMessage());
            }
        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, TAG_REQUEST_STRING);
    }

    //get the speaker information into the database
    public void getSpeakerSpreadsheet(String url) {

        final StringRequest strReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Parse and store speaker data
                hasSpeaker = true;
                ParseJSON parsed = new ParseJSON(activity);
                parsed.uploadSheet(response, ParseJSON.SPEAKER_CALL);
                allComplete();

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("TAG", "ERROR getting speaker", error);
                Snackbar.make(null, "Error on Speaker Request", Snackbar.LENGTH_SHORT);
            }
        });
        AppController.getInstance().addToRequestQueue(strReq, TAG_REQUEST_STRING);
    }

    //get the schedule information into the database
    public void getScheduleSpreadsheet(String url) {

        final StringRequest strReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hasSchedule=true;
                //Parse and store speaker data
                ParseJSON parsed = new ParseJSON(activity);
                parsed.uploadSheet(response, ParseJSON.SCHEDULE_CALL);
                allComplete();

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("TAG", "ERROR getting schedule", error);
                Snackbar.make(null, "Error on Schedule Request", Snackbar.LENGTH_SHORT);
            }
        });
        AppController.getInstance().addToRequestQueue(strReq, TAG_GET_SCHEDULE);
    }

    //get the schedule information into the database
    public void getBreakoutSpreadsheet(String url) {

        final StringRequest strReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hasBreakout=true;
                //Parse and store speaker data
                ParseJSON parsed = new ParseJSON(activity);
                parsed.uploadSheet(response, ParseJSON.BREAKOUTS_CALL);
                allComplete();

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("TAG", "ERROR getting breakout", error);
                Snackbar.make(null, "Error on Breakout Request", Snackbar.LENGTH_SHORT);
            }
        });
        AppController.getInstance().addToRequestQueue(strReq, TAG_GET_BREAKOUT);
    }

    //get the schedule information into the database
    public void getPresentationSpreadsheet(String url) {

        final StringRequest strReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hasPresentation=true;
                //Parse and store speaker data
                ParseJSON parsed = new ParseJSON(activity);
                parsed.uploadSheet(response, ParseJSON.PRESENTATIONS_CALL);
                allComplete();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("TAG", "ERROR getting presentations", error);
                Snackbar.make(null, "Error on Presentation Request", Snackbar.LENGTH_SHORT);
                allComplete();
            }
        });
        AppController.getInstance().addToRequestQueue(strReq, TAG_GET_PRESENTATION);
    }

    //get the schedule information into the database
    public void getNotificationSpreadsheet(String url) {

        final StringRequest strReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Parse and store speaker data
                ParseJSON parsed = new ParseJSON(activity);
                parsed.uploadSheet(response, ParseJSON.NOTIFICATIONS_CALL);
                if(isFirst || isSynch){
                    allComplete();
                }else {
                    notificationComplete();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("TAG", "ERROR getting notifications", error);
                Snackbar.make(null, "Error on Notification Request", Snackbar.LENGTH_SHORT);
            }
        });
        AppController.getInstance().addToRequestQueue(strReq, TAG_REQUEST_STRING);
    }

    private void allComplete(){

        if(hasSpeaker && hasSchedule && hasBreakout && hasPresentation) {
            Intent i = new Intent(activity, MainActivity.class);
            if (progress.isShowing()){
                progress.dismiss();
            }
            activity.startActivity(i);
            activity.finish();
        }
    }

    private void notificationComplete(){
        iRequest.communicateNotificationResult();
        if(!keepScreen) {
            Intent i = new Intent(activity, MainActivity.class);
            activity.startActivity(i);
            activity.finish();
        }
    }

    private void setCommunicator(iRequestSheet iRequest){
        this.iRequest = iRequest;
    }
    public interface iRequestSheet{
        void communicateNotificationResult();
    }
}
