package innatemobile.storymakerevents.Utils;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.List;

import innatemobile.storymakerevents.Activities.HelpActivity;
import innatemobile.storymakerevents.Activities.MainActivity;
import innatemobile.storymakerevents.Activities.PresentationActivity;
import innatemobile.storymakerevents.Adapters.AddScheduleAdapter;
import innatemobile.storymakerevents.Adapters.BreakoutAdapter;
import innatemobile.storymakerevents.Models.Breakouts;
import innatemobile.storymakerevents.Models.ScheduleJoined;

/**
 * Created by rphovley on 1/25/2016.
 * AndroidHive tutorial http://www.androidhive.info/2014/05/android-working-with-volley-library-1/ for volley related items
 */
public class AppController extends Application {
    public static final String TAG = AppController.class
            .getSimpleName();
    public static long startTime = 0;
    private static long lastTime = 0;

    /**********ACTIVITY TRANSITION VARIABLES****************/
    public static final int HOME_POS = 0;
    public static final int SCHEDULE_POS = 1;
    public static final int ADD_POS = 2;
    public static final int FEEDBACK_POS = 3;

    public static final String HIGHLIGHTED_POSITION_TAG = "highlighted_pos";
    public static final String SCHEDULE_ID_TAG = "schedule_id";
    public static final String HELP_FROM_TAG = "help_from";
    public static boolean firstTimeFlash = false;
    /**********ACTIVITY TRANSITION VARIABLES****************/

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private static AppController mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    /****************LOGGER****************/
    public static void logTimes(String TAG){
        //Log.d(TAG,"Time since Main Activity Load: " + timeSinceLoad() + " ms");
        Log.d(TAG,"Time since last Item Logged: " + timeSinceLast() + " ms");
        lastTime = SystemClock.currentThreadTimeMillis();
    }
    private static String timeSinceLast(){
        return String.valueOf(SystemClock.currentThreadTimeMillis() - lastTime);
    }
    private static String timeSinceLoad(){
        return String.valueOf(SystemClock.currentThreadTimeMillis() - startTime);
    }
    /****************LOGGER****************/
    /**********ACTIVITY TRANSITIONS****************/
    public static void switchToMain(Context c, int selectedTab, int schedule_id){
        Intent i = new Intent(c, MainActivity.class);
        i.putExtra(HIGHLIGHTED_POSITION_TAG, selectedTab);
        i.putExtra(SCHEDULE_ID_TAG, schedule_id);
        c.startActivity(i);
    }
    public static void switchToPresentation(Context c, List<ScheduleJoined> schedulesList, int position){
        DatabaseHandler dh = new DatabaseHandler(c);
        Intent i = new Intent(c, PresentationActivity.class);
        int id = schedulesList.get(position).schedule.getBreakout_id();
        Breakouts breakout = schedulesList.get(position).breakout;
        String start = breakout.getStartReadable();
        String end = breakout.getEndReadable();
        String day = breakout.getDayOfWeek();
        i.putExtra(BreakoutAdapter.BREAKOUT_ID_TAG, id);
        i.putExtra(BreakoutAdapter.BREAKOUT_START_TAG, start);
        i.putExtra(BreakoutAdapter.BREAKOUT_END_TAG, end);
        i.putExtra(BreakoutAdapter.BREAKOUT_DAY_TAG, day);
        i.putExtra(BreakoutAdapter.BREAKOUT_CAME_FROM_BREAKOUT, false);
        i.putExtra(AddScheduleAdapter.PRESENTATION_ID, schedulesList.get(position).schedule.getPresentation_id());
        dh.close();
        c.startActivity(i);
    }
    public static void switchToHelp(Context c, int fromPage){
        Intent i = new Intent(c, HelpActivity.class);
        i.putExtra(HELP_FROM_TAG, fromPage);
        c.startActivity(i);
    }
    /**********ACTIVITY TRANSITIONS****************/
    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.mRequestQueue,
                    new LruBitmapCache());
        }
        return this.mImageLoader;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public static boolean checkDatabaseForContent(Context c){
        DatabaseHandler dh = new DatabaseHandler(c);
        if(dh.getAllSchedule() == null || dh.getAllSchedule().size() == 0){
            return false;
        }
        if(dh.getAllPresentations() == null || dh.getAllPresentations().size() == 0){
            return false;
        }
        if(dh.getAllBreakouts() == null || dh.getAllBreakouts().size() == 0){
            return false;
        }
        if(dh.getAllSpeakers() == null || dh.getAllSpeakers().size() == 0){
            return false;
        }

        return true;
    }

}
