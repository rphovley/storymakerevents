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

    private int id;
    private String name;
    private String spreadsheet_key;

    public Spreadsheets() {
    }

    public Spreadsheets(int id, String name, String spreadsheet_key) {
        this.id = id;
        this.name = name;
        this.spreadsheet_key = spreadsheet_key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
