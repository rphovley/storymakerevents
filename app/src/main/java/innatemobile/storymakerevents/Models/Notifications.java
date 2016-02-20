package innatemobile.storymakerevents.Models;

/**
 * Created by rphovley on 2/3/2016.
 */
public class Notifications {

    private int id;
    private String notification;

    public Notifications() {
    }

    public Notifications(int id, String notification) {
        this.id = id;
        this.notification = notification;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }
}
