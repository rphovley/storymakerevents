package innatemobile.storymakerevents.Models;

/**
 * Created by rphovley on 1/28/2016.
 */
public class Spreadsheets {

    public static final String SPEAKERS_SHEET      = "Speakers";
    public static final String SCHEDULES_SHEET     = "Schedules";
    public static final String BREAKOUTS_SHEET     = "Breakouts" ;
    public static final String PRESENTATIONS_SHEET = "Presentations";
    public static final String NOTIFICATIONS_SHEET = "Notifications";
    public static final String CONFERENCE_SHEET    = "Conference Feedback";
    public static final String COURSE_SHEET        = "Course Feedback";
    public static final String MAP_LINK            = "Conference Map";

    private int id;
    private String name;
    private String link;

    private String spreadsheet_key;

    public Spreadsheets() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
    public String getSpreadsheet_key() {
        return spreadsheet_key;
    }

    public void setSpreadsheet_key(String spreadsheet_key) {
        this.spreadsheet_key = spreadsheet_key;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
