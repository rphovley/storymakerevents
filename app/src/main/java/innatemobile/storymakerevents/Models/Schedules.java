package innatemobile.storymakerevents.Models;

import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;

/**
 * Created by rphovley on 1/30/2016.
 */
public class Schedules {

    /****************SCHEDULE VARS***************/
    private int id;
    private int presentation_id;
    private int breakout_id;
    private int section_id;
    private boolean isPresentation;
    private String location;
    private boolean isEmptyBreakout;
    /***************SCHEDULE VARS***************/
    //isEmptyBreakout is a post database processing variable


    public boolean isEmptyBreakout() {
        return isEmptyBreakout;
    }

    public void setIsEmptyBreakout(boolean isEmptyBreakout) {
        this.isEmptyBreakout = isEmptyBreakout;
    }

    public Schedules() {

    }

    public Schedules(int id, int presentation_id, int breakout_id, int section_id, String location, boolean isPresentation) {
        this.id = id;
        this.presentation_id = presentation_id;
        this.breakout_id = breakout_id;
        this.section_id = section_id;
        this.location = location;
        this.isPresentation = isPresentation;
    }



    public boolean isPresentation() {
        return isPresentation;
    }

    public int isPresentationDB(){
        if(isPresentation){
            return 1;
        }else{
            return 0;
        }
    }

    public void setIsPresentationDB(int isPresentation){
        if(isPresentation==1){
            this.isPresentation = true;
        }else{
            this.isPresentation = false;
        }
    }

    public void setIsPresentation(boolean isPresentation) {
        this.isPresentation = isPresentation;
    }

    public void setStringIsPresentation(String isPresentation) {
        if(isPresentation.toLowerCase().trim().equals("yes")){
            this.isPresentation = true;
        }else{
            this.isPresentation = false;
        }
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getSection_id() {
        return section_id;
    }

    public void setSection_id(int section_id) {
        this.section_id = section_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPresentation_id() {
        return presentation_id;
    }

    public void setPresentation_id(int presentation_id) {
        this.presentation_id = presentation_id;
    }

    public int getBreakout_id() {
        return breakout_id;
    }

    public void setBreakout_id(int breakout_id) {
        this.breakout_id = breakout_id;
    }

}
