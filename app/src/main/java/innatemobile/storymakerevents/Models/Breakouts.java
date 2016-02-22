package innatemobile.storymakerevents.Models;

import android.util.Log;

import java.sql.Date;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by rphovley on 1/31/2016.
 */
public class Breakouts {
    private int id;
    private Time start;
    private Time end;
    private Date date;
    private String breakoutName;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat dateFromFormat = new SimpleDateFormat("MM/dd/yyyy");
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    private SimpleDateFormat readableFormat = new SimpleDateFormat("h:mm");
    private SimpleDateFormat ampmFormat = new SimpleDateFormat("h:mm a");
    private SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");
    private static final String TAG = "Breakouts.java";
    public Breakouts(){

    }
    public Breakouts(int id, String sStart, String sEnd, String sDate, String breakoutName) {
        Time tStart = null;
        Time tEnd = null;
        Date dDate = null;
        try {
            tStart = new Time(timeFormat.parse(sStart).getTime());
            tEnd   = new Time(timeFormat.parse(sEnd).getTime());
            dDate  = new Date(dateFormat.parse(sDate).getTime());
        }catch(ParseException e){
            Log.d(TAG, "ERROR parsing date and time and getBreakout().", e);
        }
        this.start = tStart;
        this.id = id;
        this.start = tStart;
        this.end = tEnd;
        this.date = dDate;
        this.breakoutName = breakoutName;
    }

    public String getBreakoutName() {
        return breakoutName;
    }

    public void setBreakoutName(String breakoutName) {
        this.breakoutName = breakoutName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Time getStart() {
        return start;
    }
    public String getStartString() {
        return timeFormat.format(getStart());
    }

    public void setStart(String sStart) {
        Time tStart = null;
        try {
            tStart = new Time(timeFormat.parse(sStart).getTime());
        }catch(ParseException e){
            Log.d(TAG, "ERROR parsing time to Breakouts Model.", e);
        }
        this.start = tStart;
    }

    public Time getEnd() {
        return end;
    }

    public String getStringEnd(){
        return timeFormat.format(getEnd());
    }

    public void setEnd(String sEnd) {
        Time tEnd = null;
        try {
            tEnd = new Time(timeFormat.parse(sEnd).getTime());
        }catch(ParseException e){
            Log.d(TAG, "ERROR parsing time to Breakouts Model.", e);
        }
        this.end = tEnd;
    }

    public Date getDate() {
        return date;
    }
    public Date getDateAndStartTime(){
        Date startDateAndTime = new Date(0);
        startDateAndTime.setTime(getDate().getTime()+ getStart().getTime());
        return startDateAndTime;
    }
    public String getDayOfWeek(){
        return dayFormat.format(getDate());
    }

    public String getStringDate() {
        return dateFormat.format(getDate());
    }

    public void setDate(String sDate) {
        Date dDate = null;
        try {
            dDate = new Date(dateFormat.parse(sDate).getTime());
        }catch(ParseException e){
            Log.d(TAG, "ERROR parsing time to Breakouts Model.", e);
        }
        this.date = dDate;
    }

    public void setDateFromJSON(String sDate) {
        Date dDate = null;
        try {
            dDate = new Date(dateFromFormat.parse(sDate).getTime());
        }catch(ParseException e){
            Log.d(TAG, "ERROR parsing time to Breakouts Model.", e);
        }
        this.date = dDate;
    }

    public String getStartReadable(){
        return readableFormat.format(getStart());
    }

    public String getEndReadable(){
        return ampmFormat.format(getEnd());
    }
}
