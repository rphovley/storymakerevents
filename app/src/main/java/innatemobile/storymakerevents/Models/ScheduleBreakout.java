package innatemobile.storymakerevents.Models;

import android.util.Log;

import java.sql.Date;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by rphovley on 3/7/2016.
 */
public class ScheduleBreakout {

    public Schedules schedule;
    public Breakouts breakout;

    public ScheduleBreakout(Schedules schedule, Breakouts breakout){
        this.schedule = schedule;
        this.breakout = breakout;
    }

}
