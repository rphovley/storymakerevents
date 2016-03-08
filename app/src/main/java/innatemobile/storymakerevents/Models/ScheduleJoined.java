package innatemobile.storymakerevents.Models;

import android.util.Log;

import java.sql.Date;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by rphovley on 3/7/2016.
 */
public class ScheduleJoined {

    public Schedules schedule;
    public Breakouts breakout;
    public Presentations presentation;
    public Speakers speakers;

    public ScheduleJoined(Schedules schedule, Breakouts breakout, Presentations presentation, Speakers speakers){
        this.schedule = schedule;
        this.breakout = breakout;
        this.presentation = presentation;
        this.speakers = speakers;
    }

}
