package innatemobile.storymakerevents.Utils;

import android.app.Presentation;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.Date;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 2;

    // Database Name
    private static final String DATABASE_NAME = "storymakersSchedule";

    // Table Names
    private static final String TABLE_SPREADSHEETS  = "spreadsheets";
    private static final String TABLE_SPEAKERS      = "speakers";
    private static final String TABLE_SCHEDULE      = "schedule";
    private static final String TABLE_BREAKOUTS     = "breakouts";
    private static final String TABLE_PRESENTATIONS = "presentations";
    private static final String TABLE_NOTIFICATIONS = "notifications";
    private static final String TABLE_MY_SCHEDULE   = "my_schedule";

    // Spreadsheet Column Names
    private static final String SPREADSHEET_ID = "id";
    private static final String SPREADSHEET_NAME = "name";
    private static final String SPREADSHEET_KEY = "spreadsheet_key";

    //Speaker Column Names
    private static final String SPEAKER_ID = "id";
    private static final String SPEAKER_NAME = "name";
    private static final String SPEAKER_BIO  = "bio";
    private static final String SPEAKER_IMAGE = "image";

    //Schedule & My Schedule Column Names
    private static final String SCHEDULE_ID = "id";
    private static final String SCHEDULE_PRESENTATION_ID = "presentation_id";
    private static final String SCHEDULE_BREAKOUT_ID = "breakout_id";
    private static final String SCHEDULE_SECTION = "section";
    private static final String SCHEDULE_LOCATION = "location";
    private static final String SCHEDULE_IS_PRESENTATION = "is_presentation";

    //Breakout Column Names
    private static final String BREAKOUT_ID    = "id";
    private static final String BREAKOUT_START = "start";
    private static final String BREAKOUT_END   = "end";
    private static final String BREAKOUT_DATE  = "date";
    private static final String BREAKOUT_NAME  = "name";

    //Presentation Column Names
    private static final String PRESENTATION_ID           = "id";
    private static final String PRESENTATION_TITLE        = "title";
    private static final String PRESENTATION_DESCRIPTION  = "description";
    private static final String PRESENTATION_SPEAKER_ID   = "speaker_id";
    private static final String PRESENTATION_IS_INTENSIVE = "is_intensive";
    private static final String PRESENTATION_IS_KEYNOTE   = "is_keynote";

    //Notification Column Names
    private static final String NOTIFICATION_ID = "id";
    private static final String NOTIFICATION    = "notification";


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //CREATE OUR TABLES
        /******************SPREADSHEETS*******************/
        String CREATE_SPREADSHEETS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_SPREADSHEETS + "("
                + SPREADSHEET_ID + " INTEGER PRIMARY KEY," + SPREADSHEET_NAME + " TEXT,"
                + SPREADSHEET_KEY + " TEXT" + ")";
        db.execSQL(CREATE_SPREADSHEETS_TABLE);
        /******************SPREADSHEETS*******************/

        /******************SPEAKERS***********************/
        String CREATE_SPEAKERS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_SPEAKERS + "("
                + SPEAKER_ID + " INTEGER PRIMARY KEY," + SPEAKER_NAME + " TEXT," +
                SPEAKER_BIO + " TEXT," +SPEAKER_IMAGE + " TEXT" + ")";
        db.execSQL(CREATE_SPEAKERS_TABLE);
        /******************SPEAKERS***********************/

        /******************SCHEDULE***********************/
        String CREATE_SCHEDULE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_SCHEDULE + "("
                + SCHEDULE_ID + " INTEGER PRIMARY KEY," + SCHEDULE_PRESENTATION_ID + " INTEGER,"
                + SCHEDULE_BREAKOUT_ID + " INTEGER," + SCHEDULE_SECTION + " INTEGER,"
                + SCHEDULE_LOCATION + " TEXT," + SCHEDULE_IS_PRESENTATION + " INTEGER"+ ")";
        db.execSQL(CREATE_SCHEDULE_TABLE);
        /******************SCHEDULE***********************/

        /******************MY SCHEDULE***********************/
        String CREATE_MY_SCHEDULE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_MY_SCHEDULE + "("
                + SCHEDULE_ID + " INTEGER PRIMARY KEY," + SCHEDULE_PRESENTATION_ID + " INTEGER,"
                + SCHEDULE_BREAKOUT_ID + " INTEGER," + SCHEDULE_SECTION + " INTEGER,"
                + SCHEDULE_LOCATION + " TEXT," + SCHEDULE_IS_PRESENTATION + " INTEGER"+ ")";
        db.execSQL(CREATE_MY_SCHEDULE_TABLE);
        /******************MY SCHEDULE***********************/

        /******************BREAKOUTS***********************/
        String CREATE_BREAKOUT_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_BREAKOUTS + "("
                + BREAKOUT_ID + " INTEGER PRIMARY KEY," + BREAKOUT_START + " TEXT,"
                + BREAKOUT_END + " TEXT, " + BREAKOUT_DATE + " TEXT,"+ BREAKOUT_NAME + " TEXT"+")";
        db.execSQL(CREATE_BREAKOUT_TABLE);
        /******************BREAKOUTS***********************/

        /******************PRESENTATIONS***********************/
        String CREATE_PRESENTATION_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_PRESENTATIONS + "("
                + PRESENTATION_ID + " INTEGER PRIMARY KEY," + PRESENTATION_DESCRIPTION+ " TEXT,"
                + PRESENTATION_TITLE + " TEXT, " + PRESENTATION_SPEAKER_ID + " INTEGER, "
                + PRESENTATION_IS_INTENSIVE + " INTEGER, " + PRESENTATION_IS_KEYNOTE + " INTEGER" + ")";
        db.execSQL(CREATE_PRESENTATION_TABLE);
        /******************PRESENTATION***********************/

        /******************NOTIFICATIONS***********************/
        String CREATE_NOTIFICATION_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NOTIFICATIONS + "("
                + NOTIFICATION_ID + " INTEGER PRIMARY KEY," + NOTIFICATION+ " TEXT" + ")";
        db.execSQL(CREATE_NOTIFICATION_TABLE);
        /******************NOTIFICATIONS***********************/
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SPREADSHEETS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SPEAKERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCHEDULE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MY_SCHEDULE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRESENTATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATIONS);
        // Create tables again
        onCreate(db);
    }

    /********************SPREADSHEET METHODS****************/
    public void addSpreadsheet(Spreadsheets sheet){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put(SPREADSHEET_ID, sheet.getId());
        content.put(SPREADSHEET_NAME, sheet.getName());
        content.put(SPREADSHEET_KEY, sheet.getSpreadsheet_key());
        db.insert(TABLE_SPREADSHEETS, null, content);
        db.close();
    }
    public String getSpreadsheetKey(String name){
        SQLiteDatabase db = this.getReadableDatabase();
        //query the database to return the spreadsheet key for the spreadsheet name
        Cursor cursor = db.query(TABLE_SPREADSHEETS, new String[]{SPREADSHEET_KEY},
                SPREADSHEET_NAME + "=?", new String[]{name}, null, null, null, null);
        if(cursor!=null && cursor.getCount() > 0){
            cursor.moveToFirst();
            db.close();
            return cursor.getString(0);
        }
        db.close();
        return null;
    }

    /********************SPREADSHEET METHODS****************/
    /********************SPEAKER METHODS*******************/
    public void addSpeaker(Speakers speaker){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put(SPEAKER_ID, speaker.getId());
        content.put(SPEAKER_NAME, speaker.getName());
        content.put(SPEAKER_BIO, speaker.getBio());
        content.put(SPEAKER_IMAGE, speaker.getImage());
        db.insert(TABLE_SPEAKERS, null, content);
        db.close();
    }
    public Speakers getSpeaker(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        //query the database to return the spreadsheet key for the spreadsheet name
        Cursor cursor = db.query(TABLE_SPEAKERS, new String[]{SPEAKER_ID, SPEAKER_NAME, SPEAKER_BIO, SPEAKER_IMAGE},
                SPEAKER_ID + "=?", new String[]{String.valueOf(id)}, null, null, null, null);
        int count = cursor.getCount();
        if(cursor!=null && cursor.getCount() > 0){
            cursor.moveToFirst();
            Speakers speaker = new Speakers(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3));
            db.close();
            return speaker;
        }
        db.close();
        return null;
    }
    public List<Speakers> getAllSpeakers(){
        List<Speakers> speakerList = new ArrayList<Speakers>();
        SQLiteDatabase db = this.getReadableDatabase();
        //query the database to return the Animal key for the Animal name
        String selectQuery = "SELECT  * FROM " + TABLE_SPEAKERS;
        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()){
            do {
                Speakers speaker= new Speakers();
                speaker.setId(Integer.parseInt(cursor.getString(0)));
                speaker.setName(cursor.getString(1));
                speaker.setBio(cursor.getString(2));
                speaker.setImage(cursor.getString(3));
                speakerList.add(speaker);
            }while(cursor.moveToNext());
            db.close();
            return speakerList;
        }
        db.close();
        return null;
    }
    /********************SPEAKER METHODS*******************/

    /********************SCHEDULE METHODS*******************/
    public void addSchedule(Schedules schedule){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put(SCHEDULE_ID, schedule.getId());
        content.put(SCHEDULE_PRESENTATION_ID, schedule.getPresentation_id());
        content.put(SCHEDULE_BREAKOUT_ID, schedule.getBreakout_id());
        content.put(SCHEDULE_SECTION, schedule.getSection_id());
        content.put(SCHEDULE_LOCATION, schedule.getLocation());
        content.put(SCHEDULE_IS_PRESENTATION, schedule.isPresentationDB());
        db.insert(TABLE_SCHEDULE, null, content);
        db.close();
    }
    public List<Schedules> getAllSchedule(){
        List<Schedules> scheduleList = new ArrayList<Schedules>();
        SQLiteDatabase db = this.getReadableDatabase();
        //query the database to return the Animal key for the Animal name
        String selectQuery = "SELECT  * FROM " + TABLE_SCHEDULE;
        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.moveToFirst() && cursor.getCount() > 0){
            do {
                Schedules schedule = new Schedules();
                schedule.setId(Integer.parseInt(cursor.getString(0)));
                schedule.setPresentation_id(Integer.parseInt(cursor.getString(1)));
                schedule.setBreakout_id(Integer.parseInt(cursor.getString(2)));
                schedule.setSection_id(Integer.parseInt(cursor.getString(3)));
                schedule.setLocation(cursor.getString(4));
                schedule.setIsPresentationDB(Integer.parseInt(cursor.getString(5)));
                scheduleList.add(schedule);
            }while(cursor.moveToNext());
        }
        db.close();
        return scheduleList;
    }
    public List<Schedules> getScheduleByBreakout(int id){
        List<Schedules> scheduleList = new ArrayList<Schedules>();
        SQLiteDatabase db = this.getReadableDatabase();
        //query the database to return the spreadsheet key for the spreadsheet name
        Cursor cursor = db.query(TABLE_SCHEDULE, new String[]{SCHEDULE_ID, SCHEDULE_PRESENTATION_ID, SCHEDULE_BREAKOUT_ID,
                        SCHEDULE_SECTION, SCHEDULE_LOCATION, SCHEDULE_IS_PRESENTATION},
                SCHEDULE_BREAKOUT_ID + "=?", new String[]{String.valueOf(id)}, null, null, null, null);
        if(cursor.moveToFirst() && cursor.getCount() > 0){
            do {
                Schedules schedule = new Schedules();
                schedule.setId(Integer.parseInt(cursor.getString(0)));
                schedule.setPresentation_id(Integer.parseInt(cursor.getString(1)));
                schedule.setBreakout_id(Integer.parseInt(cursor.getString(2)));
                schedule.setSection_id(Integer.parseInt(cursor.getString(3)));
                schedule.setLocation(cursor.getString(4));
                schedule.setIsPresentationDB(Integer.parseInt(cursor.getString(5)));
                scheduleList.add(schedule);
            }while(cursor.moveToNext());
            db.close();
            return scheduleList;
        }
        db.close();
        return null;
    }
    public List<Schedules> getScheduleByPresentation(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        List<Schedules> scheduleList = new ArrayList<Schedules>();
        //query the database to return the spreadsheet key for the spreadsheet name
        Cursor cursor = db.query(TABLE_SCHEDULE, new String[]{SCHEDULE_ID, SCHEDULE_PRESENTATION_ID, SCHEDULE_BREAKOUT_ID,
                        SCHEDULE_SECTION, SCHEDULE_LOCATION, SCHEDULE_IS_PRESENTATION},
                SCHEDULE_PRESENTATION_ID + "=?", new String[]{String.valueOf(id)}, null, null, null, null);
        if(cursor.moveToFirst() && cursor.getCount() > 0){
            do {
                Schedules schedule = new Schedules();
                schedule.setId(Integer.parseInt(cursor.getString(0)));
                schedule.setPresentation_id(Integer.parseInt(cursor.getString(1)));
                schedule.setBreakout_id(Integer.parseInt(cursor.getString(2)));
                schedule.setSection_id(Integer.parseInt(cursor.getString(3)));
                schedule.setLocation(cursor.getString(4));
                schedule.setIsPresentationDB(Integer.parseInt(cursor.getString(5)));
                scheduleList.add(schedule);
            }while(cursor.moveToNext());
            db.close();
            return scheduleList;
        }
        db.close();
        return null;
    }
    /********************SCHEDULE METHODS*******************/

    /********************MY SCHEDULE METHODS*******************/
    public void addMySchedule(Schedules schedule){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put(SCHEDULE_ID, schedule.getId());
        content.put(SCHEDULE_PRESENTATION_ID, schedule.getPresentation_id());
        content.put(SCHEDULE_BREAKOUT_ID, schedule.getBreakout_id());
        content.put(SCHEDULE_SECTION, schedule.getSection_id());
        content.put(SCHEDULE_LOCATION, schedule.getLocation());
        content.put(SCHEDULE_IS_PRESENTATION, schedule.isPresentationDB());
        db.insert(TABLE_MY_SCHEDULE, null, content);
        db.close();
    }
    public List<Schedules> getAllMySchedule(){
        List<Schedules> scheduleList = new ArrayList<Schedules>();
        SQLiteDatabase db = this.getReadableDatabase();
        //query the database to return the Animal key for the Animal name
        String selectQuery = "SELECT  * FROM " + TABLE_MY_SCHEDULE +
                " my JOIN " + TABLE_BREAKOUTS + " br ON my." + SCHEDULE_BREAKOUT_ID + "=br." + BREAKOUT_ID +
                " ORDER BY " + "br. " + BREAKOUT_DATE + ", br." +BREAKOUT_START;
        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.moveToFirst() && cursor.getCount() > 0){
            do {
                Schedules schedule = new Schedules();
                schedule.setId(Integer.parseInt(cursor.getString(0)));
                schedule.setPresentation_id(Integer.parseInt(cursor.getString(1)));
                schedule.setBreakout_id(Integer.parseInt(cursor.getString(2)));
                schedule.setSection_id(Integer.parseInt(cursor.getString(3)));
                schedule.setLocation(cursor.getString(4));
                schedule.setIsPresentationDB(Integer.parseInt(cursor.getString(5)));
                scheduleList.add(schedule);
            }while(cursor.moveToNext());
        }
        db.close();
        return scheduleList;
    }
    public List<Schedules> getMyScheduleByBreakout(int id){
        List<Schedules> scheduleList = new ArrayList<Schedules>();
        SQLiteDatabase db = this.getReadableDatabase();
        //query the database to return the spreadsheet key for the spreadsheet name
        Cursor cursor = db.query(TABLE_MY_SCHEDULE, new String[]{SCHEDULE_ID, SCHEDULE_PRESENTATION_ID, SCHEDULE_BREAKOUT_ID,
                        SCHEDULE_SECTION, SCHEDULE_LOCATION, SCHEDULE_IS_PRESENTATION},
                SCHEDULE_BREAKOUT_ID + "=?", new String[]{String.valueOf(id)}, null, null, null, null);
        if(cursor.moveToFirst() && cursor.getCount() > 0){
            do {
                Schedules schedule = new Schedules();
                schedule.setId(Integer.parseInt(cursor.getString(0)));
                schedule.setPresentation_id(Integer.parseInt(cursor.getString(1)));
                schedule.setBreakout_id(Integer.parseInt(cursor.getString(2)));
                schedule.setSection_id(Integer.parseInt(cursor.getString(3)));
                schedule.setLocation(cursor.getString(4));
                schedule.setIsPresentationDB(Integer.parseInt(cursor.getString(5)));
                scheduleList.add(schedule);
            }while(cursor.moveToNext());
            db.close();
            return scheduleList;
        }
        db.close();
        return null;
    }
    public List<Schedules> getMyScheduleByPresentation(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        List<Schedules> scheduleList = new ArrayList<Schedules>();
        //query the database to return the spreadsheet key for the spreadsheet name
        Cursor cursor = db.query(TABLE_MY_SCHEDULE, new String[]{SCHEDULE_ID, SCHEDULE_PRESENTATION_ID, SCHEDULE_BREAKOUT_ID,
                        SCHEDULE_SECTION, SCHEDULE_LOCATION, SCHEDULE_IS_PRESENTATION},
                SCHEDULE_PRESENTATION_ID + "=?", new String[]{String.valueOf(id)}, null, null, null, null);
        if(cursor.moveToFirst() && cursor.getCount() > 0){
            do {
                Schedules schedule = new Schedules();
                schedule.setId(Integer.parseInt(cursor.getString(0)));
                schedule.setPresentation_id(Integer.parseInt(cursor.getString(1)));
                schedule.setBreakout_id(Integer.parseInt(cursor.getString(2)));
                schedule.setSection_id(Integer.parseInt(cursor.getString(3)));
                schedule.setLocation(cursor.getString(4));
                schedule.setIsPresentationDB(Integer.parseInt(cursor.getString(5)));
                scheduleList.add(schedule);
            }while(cursor.moveToNext());
            db.close();
            return scheduleList;
        }
        db.close();
        return null;
    }
    /********************MY SCHEDULE METHODS*******************/

    /********************BREAKOUT METHODS*******************/
    public void addBreakout(Breakouts breakout){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put(BREAKOUT_ID, breakout.getId());
        content.put(BREAKOUT_START, breakout.getStartString());
        content.put(BREAKOUT_END, breakout.getStringEnd());
        content.put(BREAKOUT_DATE, breakout.getStringDate());
        content.put(BREAKOUT_NAME, breakout.getBreakoutName());
        db.insert(TABLE_BREAKOUTS, null, content);
        db.close();
    }
    public Breakouts getBreakout(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        //query the database to return the spreadsheet key for the spreadsheet name
        Cursor cursor = db.query(TABLE_BREAKOUTS, new String[]{BREAKOUT_ID, BREAKOUT_START, BREAKOUT_END, BREAKOUT_DATE, BREAKOUT_NAME},
                BREAKOUT_ID + "=?", new String[]{String.valueOf(id)}, null, null, null, null);
        if(cursor!=null && cursor.getCount() > 0){
            cursor.moveToFirst();
            Breakouts breakout = new Breakouts(Integer.parseInt(cursor.getString(0)), cursor.getString(1),
                    cursor.getString(2), cursor.getString(3), cursor.getString(4));
            db.close();
            return breakout;
        }
        db.close();
        return null;
    }
    public List<Breakouts> getAllBreakouts(){
        List<Breakouts> breakoutList = new ArrayList<Breakouts>();
        SQLiteDatabase db = this.getReadableDatabase();
        //query the database to return the spreadsheet key for the spreadsheet name
        String selectQuery = "SELECT  * FROM " + TABLE_BREAKOUTS;
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor.moveToFirst() && cursor!=null && cursor.getCount() > 0){
            do {
                Breakouts breakout = new Breakouts(Integer.parseInt(cursor.getString(0)), cursor.getString(1),
                        cursor.getString(2), cursor.getString(3), cursor.getString(4));
                breakoutList.add(breakout);
            }while(cursor.moveToNext());
            db.close();
            return breakoutList;
        }
        db.close();
        return null;
    }
    public List<Breakouts> getAllBreakoutsRefined(){
        List<Breakouts> breakoutList = new ArrayList<Breakouts>();
        SQLiteDatabase db = this.getReadableDatabase();
        //query the database to return the spreadsheet key for the spreadsheet name
        String selectQuery = "SELECT  * FROM " + TABLE_BREAKOUTS +
                " WHERE " + BREAKOUT_ID + "=" + BREAKOUT_NAME;
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor.moveToFirst() && cursor!=null && cursor.getCount() > 0){
            do {
                Breakouts breakout = new Breakouts(Integer.parseInt(cursor.getString(0)), cursor.getString(1),
                        cursor.getString(2), cursor.getString(3), cursor.getString(4));
                breakoutList.add(breakout);
            }while(cursor.moveToNext());
            db.close();
            return breakoutList;
        }
        db.close();
        return null;
    }
    /********************BREAKOUT METHODS*******************/

    /********************PRESENTATION METHODS*******************/
    public void addPresentation(Presentations presentation){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put(PRESENTATION_ID,presentation.getId() );
        content.put(PRESENTATION_TITLE, presentation.getTitle());
        content.put(PRESENTATION_DESCRIPTION, presentation.getDescription());
        content.put(PRESENTATION_SPEAKER_ID, presentation.getSpeaker_id());
        content.put(PRESENTATION_IS_INTENSIVE, presentation.isIntensive());
        content.put(PRESENTATION_IS_KEYNOTE, presentation.isKeynote());
        db.insert(TABLE_PRESENTATIONS, null, content);
        db.close();
    }
    public Presentations getPresentation(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        //query the database to return the spreadsheet key for the spreadsheet name
        Cursor cursor = db.query(TABLE_PRESENTATIONS, new String[]{PRESENTATION_ID, PRESENTATION_TITLE, PRESENTATION_DESCRIPTION,
                        PRESENTATION_SPEAKER_ID, PRESENTATION_IS_INTENSIVE,PRESENTATION_IS_KEYNOTE},
                        PRESENTATION_ID + "=?", new String[]{String.valueOf(id)}, null, null, null, null);
        if(cursor!=null && cursor.getCount() > 0){
            cursor.moveToFirst();
            int presentation_id = cursor.getInt(0);
            String title        = cursor.getString(1);
            String description  = cursor.getString(2);
            int speaker_id      = cursor.getInt(3);
            int is_intensive    = cursor.getInt(4);
            int is_keynote      = cursor.getInt(5);

            Presentations presentation = new Presentations(presentation_id, title, description, speaker_id, is_intensive, is_keynote);
            db.close();
            return presentation;
        }
        db.close();
        return null;
    }
    public List<Presentations> getAllPresentations(){
        List<Presentations> presentationsList = new ArrayList<Presentations>();
        SQLiteDatabase db = this.getReadableDatabase();
        //query the database to return the spreadsheet key for the spreadsheet name
        String selectQuery = "SELECT  * FROM " + TABLE_PRESENTATIONS;
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor.moveToFirst() && cursor!=null && cursor.getCount() > 0){
            do {
                int presentation_id = cursor.getInt(0);
                String title = cursor.getString(2);
                String description = cursor.getString(1);
                int speaker_id = cursor.getInt(3);
                int is_intensive = cursor.getInt(4);
                int is_keynote = cursor.getInt(5);

                Presentations presentation = new Presentations(presentation_id, title, description, speaker_id, is_intensive, is_keynote);
                presentationsList.add(presentation);
            }while(cursor.moveToNext());
            db.close();
            return presentationsList;
        }
        db.close();
        return null;
    }
    /********************PRESENTATION METHODS*******************/

    /********************NOTIFICATION METHODS*******************/
    public void addNotification(Notifications notification){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put(NOTIFICATION,notification.getId() );
        content.put(NOTIFICATION, notification.getNotification());
        db.insert(TABLE_NOTIFICATIONS, null, content);
        db.close();
    }
    public String getLastNotification(){
        SQLiteDatabase db = this.getReadableDatabase();
        //query the database to return the spreadsheet key for the spreadsheet name
        String selectQuery = "SELECT " + NOTIFICATION + " FROM " +
                TABLE_NOTIFICATIONS + " WHERE " + NOTIFICATION_ID +
                " = (SELECT MAX( " + NOTIFICATION_ID + " ) FROM " + TABLE_NOTIFICATIONS + ")";
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor!=null && cursor.getCount() > 0){
            cursor.moveToFirst();
            db.close();
            return cursor.getString(0);
        }
        db.close();
        return null;
    }
    /********************NOTIFICATION METHODS*******************/
}
