package innatemobile.storymakerevents.Utils;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import innatemobile.storymakerevents.Models.Breakouts;
import innatemobile.storymakerevents.Models.Notifications;
import innatemobile.storymakerevents.Models.Presentations;
import innatemobile.storymakerevents.Models.Schedules;
import innatemobile.storymakerevents.Models.Speakers;
import innatemobile.storymakerevents.Models.Spreadsheets;

/**
 * Created by rphovley on 1/28/2016.
 */
public class ParseJSON {
    static final String TAG = "ParseJSON";
    public static final int SPREADSHEET_CALL    = 0;
    public static final int SPEAKER_CALL        = 1;
    public static final int SCHEDULE_CALL       = 2;
    public static final int BREAKOUTS_CALL      = 3;
    public static final int PRESENTATIONS_CALL  = 4;
    public static final int NOTIFICATIONS_CALL  = 5;
    private Context context;

    public ParseJSON(Context context) {
        this.context = context;
    }
    /*public static String returnArrayString(String JSON){
        return getSheetRows(JSON).toString();
    }*/

    //strip off the covering to the individual row array
    public JSONArray uploadSheet(String rawJSON, int modelType){
        int last = rawJSON.lastIndexOf("}");
        int first = rawJSON.indexOf("{", rawJSON.indexOf("{") + 1);
        String JSON = rawJSON.substring(first, last);
        JSONArray rows = getSheetRows(JSON);
        JSONObject row = null;
        JSONArray cRow = null;
        for (int i = 0; i < rows.length(); i++) {
            try {
                row = rows.getJSONObject(i);
            } catch (JSONException e) {
                Log.d(TAG, "Failed to parse row of json array in ParseJson.uploadSheet() at index " + i, e);
            }
            try {
                cRow = row.getJSONArray("c");
            } catch (JSONException e) {
                Log.d(TAG, "Failed to parse row of json array 'c' in ParseJson.uploadSheet() ", e);
            }
            switch(modelType){
                case SPREADSHEET_CALL:
                    uploadSheetKeys(cRow);
                    break;
                case SPEAKER_CALL:
                    uploadSheetSpeakers(cRow);
                    break;
                case SCHEDULE_CALL:
                    uploadSheetSchedules(cRow);
                    break;
                case BREAKOUTS_CALL:
                    uploadSheetBreakouts(cRow);
                    break;
                case PRESENTATIONS_CALL:
                    uploadSheetPresentations(cRow);
                    break;
                case NOTIFICATIONS_CALL:
                    uploadSheetNotifications(cRow);
                    break;
            }
        }
        return cRow;
    }


    //strip down extra information off of json to get to the rows array
    private JSONArray getSheetRows(String JSON){
        JSONObject json = null;
        try {
            json = new JSONObject(JSON);

        }catch (JSONException e){
            Log.d(TAG, "Failed to parse JSON string in ParseJson.getSheetRows()",e);
        }
        try {
            return json.getJSONArray("rows");
        } catch(JSONException e){
            Log.d(TAG, "Failed to get array 'rows' in ParseJson.getSheetRows()",e);
            return null;
        }
    }

    private void uploadSheetKeys(JSONArray row) {
        /*JSONArray row = getSheetRow(rows, SPREADSHEET_CALL);*/
        final int COLUMN_SHEET_ID   = 0;
        final int COLUMN_SHEET_NAME = 1;
        final int COLUMN_SHEET_KEY  = 3;
        DatabaseHandler dh = new DatabaseHandler(context);
        Spreadsheets sheet = new Spreadsheets();
        for(int i = 0; i < row.length(); i++) {

            try {
                String value = row.getJSONObject(i).getString("v");
                switch(i){
                    case COLUMN_SHEET_ID:
                        sheet.setId((int) Float.parseFloat(value));
                    case COLUMN_SHEET_NAME:
                        sheet.setName(value);
                        break;
                    case COLUMN_SHEET_KEY:
                        sheet.setSpreadsheet_key(value);
                        break;
                }
            } catch (JSONException e) {
                Log.d(TAG, "Failed to parse value in object in ParseJson.uploadSheetKeys() ", e);
            }
        }
        dh.addSpreadsheet(sheet);
        dh.close();
    }
    private void uploadSheetSpeakers(JSONArray row) {
        /*JSONArray row = getSheetRow(rows, SPREADSHEET_CALL);*/
        final int COLUMN_ID = 0;
        final int COLUMN_NAME  = 1;
        final int COLUMN_BIO = 2;
        final int COLUMN_IMAGE = 3;

        DatabaseHandler dh = new DatabaseHandler(context);
        Speakers speaker = new Speakers();
        for(int i = 0; i < row.length(); i++) {

            try {
                String value = row.getJSONObject(i).getString("v");
                switch(i){
                    case COLUMN_ID:
                        speaker.setId((int) Float.parseFloat(value));
                        break;
                    case COLUMN_NAME:
                        speaker.setName(value);
                        break;
                    case COLUMN_BIO:
                        speaker.setBio(value);
                        break;
                    case COLUMN_IMAGE:
                        speaker.setImage(value);
                        break;
                }
            } catch (JSONException e) {
                Log.d(TAG, "Failed to parse value in object in ParseJson.uploadSheetKeys() ", e);
            }
        }
        dh.addSpeaker(speaker);
        dh.close();
    }
    private void uploadSheetSchedules(JSONArray row){
        /*JSONArray row = getSheetRow(rows, SPREADSHEET_CALL);*/
        final int COLUMN_ID              = 0;
        final int COLUMN_PRESENTATION_ID = 2;
        final int COLUMN_BREAKOUT_ID     = 7;
        final int COLUMN_SECTION         = 4;
        final int COLUMN_LOCATION        = 5;
        final int COLUMN_IS_PRESENTATION = 6;
        DatabaseHandler dh = new DatabaseHandler(context);
        Schedules schedule = new Schedules();
        for(int i = 0; i < row.length(); i++) {

            try {
                String value = row.getJSONObject(i).getString("v");
                switch(i){
                    case COLUMN_ID:
                        schedule.setId((int) Float.parseFloat(value));
                        break;
                    case COLUMN_PRESENTATION_ID:
                        schedule.setPresentation_id((int) Float.parseFloat(value));
                        break;
                    case COLUMN_BREAKOUT_ID:
                        schedule.setBreakout_id((int) Float.parseFloat(value));
                        break;
                    case COLUMN_SECTION:
                        schedule.setSection_id(((int) Float.parseFloat(value)));
                        break;
                    case COLUMN_LOCATION:
                        schedule.setLocation(value);
                        break;
                    case COLUMN_IS_PRESENTATION:
                        schedule.setStringIsPresentation(value);
                        break;
                }
            } catch (JSONException e) {
                Log.d(TAG, "Failed to parse value in object in ParseJson.uploadSheetSchedule() ", e);
            }
        }
        dh.addSchedule(schedule);
        if(!schedule.isPresentation()){
            dh.addMySchedule(schedule);
        }
        dh.close();
    }
    private void uploadSheetBreakouts(JSONArray row){
        /*JSONArray row = getSheetRow(rows, SPREADSHEET_CALL);*/
        final int COLUMN_BREAKOUT_ID   = 4;
        final int COLUMN_START         = 1;
        final int COLUMN_END           = 2;
        final int COLUMN_DATE          = 3;
        final int COLUMN_BREAKOUT_NAME = 0;

        DatabaseHandler dh = new DatabaseHandler(context);
        Breakouts breakout = new Breakouts();
        for(int i = 0; i < row.length(); i++) {
            String value = null;
            String fValue = null;
            try {
                value = row.getJSONObject(i).getString("v");
                fValue = row.getJSONObject(i).getString("f");
            }catch(JSONException e){
                Log.d("PARSE", "ERROR GETTING VALUE FROM schedule row", e);
            }
            switch (i) {
                case COLUMN_BREAKOUT_NAME:
                    if(value!=null) {
                        breakout.setBreakoutName(value);
                    }
                    break;
                case COLUMN_START:
                    if(fValue!=null) {
                        breakout.setStart(fValue);
                    }
                    break;
                case COLUMN_END:
                    if(fValue!=null) {
                        breakout.setEnd(fValue);
                    }
                    break;
                case COLUMN_DATE:
                    if(fValue!=null) {
                        breakout.setDateFromJSON(fValue);
                    }
                    break;
                case COLUMN_BREAKOUT_ID:
                    if(value!=null) {
                        breakout.setId((int) Float.parseFloat(value));
                    }
                    break;

            }
        }
        dh.addBreakout(breakout);
        dh.close();
    }
    private void uploadSheetPresentations(JSONArray row){
        /*JSONArray row = getSheetRow(rows, SPREADSHEET_CALL);*/
        final int COLUMN_ID          = 0;
        final int COLUMN_TITLE       = 1;
        final int COLUMN_DESCRIPTION = 2;
        final int COLUMN_SPEAKER_ID  = 4;
        final int COLUMN_INTENSIVE   = 5;
        final int COLUMN_KEYNOTE     = 6;

        DatabaseHandler dh = new DatabaseHandler(context);
        Presentations presentation = new Presentations();
        for(int i = 0; i < row.length(); i++) {

            try {
                //fValue is for more compatible version of value in the JSON
                String value  = row.getJSONObject(i).getString("v");
                if(value!=null) {
                    switch (i) {
                        case COLUMN_ID:
                            presentation.setId((int) Float.parseFloat(value));
                            break;
                        case COLUMN_TITLE:
                            presentation.setTitle(value);
                            break;
                        case COLUMN_DESCRIPTION:
                            presentation.setDescription(value);
                            break;
                        case COLUMN_SPEAKER_ID:
                            presentation.setSpeaker_id((int) Float.parseFloat(value));
                            break;
                        case COLUMN_KEYNOTE:
                            presentation.setIsKeynoteJSON(value);
                            break;
                        case COLUMN_INTENSIVE:
                            presentation.setIsIntensiveJSON(value);
                            break;
                    }
                }
            } catch (JSONException e) {
                Log.d(TAG, "Failed to parse value in object in ParseJson.uploadSheetPresentations() ", e);
            }
        }
        dh.addPresentation(presentation);
        dh.close();
    }
    private void uploadSheetNotifications(JSONArray row){
        /*JSONArray row = getSheetRow(rows, SPREADSHEET_CALL);*/
        final int COLUMN_ID           = 0;
        final int COLUMN_NOTIFICATION = 1;

        DatabaseHandler dh = new DatabaseHandler(context);
        Notifications notifications = new Notifications();
        for(int i = 0; i < row.length(); i++) {

            try {
                //fValue is for more compatible version of value in the JSON
                String value  = row.getJSONObject(i).getString("v");
                if(value!=null) {
                    switch (i) {
                        case COLUMN_ID:
                            notifications.setId((int) Float.parseFloat(value));
                            break;
                        case COLUMN_NOTIFICATION:
                            notifications.setNotification(value);
                            break;
                    }
                }
            } catch (JSONException e) {
                Log.d(TAG, "Failed to parse value in object in ParseJson.uploadSheetPresentations() ", e);
            }
        }
        dh.addNotification(notifications);
        dh.close();

    }
}
