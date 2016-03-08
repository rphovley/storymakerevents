package innatemobile.storymakerevents.Activities;


import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ImageView;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import innatemobile.storymakerevents.Adapters.AddScheduleAdapter;
import innatemobile.storymakerevents.Fragments.BreakoutFragment;
import innatemobile.storymakerevents.Fragments.FeedbackFragment;
import innatemobile.storymakerevents.Fragments.HomeFragment;
import innatemobile.storymakerevents.Fragments.MyScheduleFragment;
import innatemobile.storymakerevents.Models.Breakouts;
import innatemobile.storymakerevents.R;
import innatemobile.storymakerevents.Utils.AppController;
import innatemobile.storymakerevents.Utils.DatabaseHandler;
import innatemobile.storymakerevents.Utils.RequestSpreadsheets;

/*Main Activity is controlling the fragments coming in and out of the view
*
* The initial screen is to display the current schedule.  If there is nothing in the
* schedule, then the screen will display a small add icon, and the plus floating action
* button.  When either are pressed, it will take them to the full schedule page, which will
* have the filters for each breakout.  They will then be able to select schedules until
* they want to go back.  The main home page will then display their current schedule.
* */
public class MainActivity extends AppCompatActivity implements RequestSpreadsheets.iRequestSheet,
        HomeFragment.iHomeFragment, MyScheduleFragment.iMySchedule{
    /************Class Scope Variables**********/
    public final static String TOGGLE_NOTIFICATIONS = "toggle_notifications";
    TabLayout tabLayout;
    ViewPager viewPager;
    Drawable home, myschedule, schedule, notification;
    int colorPrimaryDark, colorIconWhite;
    int selectedPos = 0;
    private List<Drawable> tabIcons;
    ViewPagerAdapter adapter;
    /************Class Scope Variables**********/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppController.startTime = SystemClock.currentThreadTimeMillis();
        setContentView(R.layout.activity_main);

        /***************TAB ICONS, VIEWPAGER INITIALIZATION AND LOGIC*************/
        /*TAB ICONS*/
        home           = ContextCompat.getDrawable(this, R.drawable.ic_home_black_24px);
        myschedule     = ContextCompat.getDrawable(this, R.drawable.calendar);
        schedule       = ContextCompat.getDrawable(this, R.drawable.calendar_add);
        notification   = ContextCompat.getDrawable(this, R.drawable.ic_chat_24dp);
        colorPrimaryDark = ContextCompat.getColor(this, R.color.colorPrimaryDark);
        colorIconWhite   = ContextCompat.getColor(this, R.color.color_icons);
        tabIcons = new ArrayList<>(Arrays.asList(home, myschedule, schedule, notification));

        /*VIEWPAGER*/
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(1);
        setupViewPager(viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();

        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                //add highlight to selected icon
                highlightSelectedIcon(position, selectedPos);
            }
        });

        /***************TAB ICONS INITIALIZATION AND LOGIC*************/
        if(getIntent().getExtras()!=null) {//if we came from addschedule, set that as our selected fragment
            highlightSelectedIcon(2, 0);
            viewPager.setCurrentItem(2);
        }
        AppController.logTimes("MAIN ACTIVITY");
    }

    /*
    * Sets up the tab icons to have correct colors, especially on older devices
    * */
    private void setupTabIcons() {
        if(tabLayout!=null) {
            for(int i = 0; i < tabIcons.size(); i++) {
                ImageView tab = (ImageView) LayoutInflater.from(this).inflate(R.layout.tab_icon, null);
                tab.setImageDrawable(tabIcons.get(i));
                tab.setColorFilter(ContextCompat.getColor(this, R.color.white));
                tabLayout.getTabAt(i).setCustomView(tab);
                highlightSelectedIcon(selectedPos, 1);
            }
        }
    }
    /*
    * Change the item at "position" to be highlighted, and remove highlighting from "unselected" tab
    * */
    private void highlightSelectedIcon(int position, int unselected){
        tabIcons.get(position).setColorFilter(new
                PorterDuffColorFilter(ContextCompat.getColor(this, R.color.colorPrimaryDark), PorterDuff.Mode.MULTIPLY));
        tabIcons.get(unselected).setColorFilter(new
                PorterDuffColorFilter(ContextCompat.getColor(this, R.color.color_icons), PorterDuff.Mode.MULTIPLY));
        selectedPos = position;
    }
    /*
    * Add fragments to the viewpager
    * */
    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new HomeFragment(), "HOME");
        adapter.addFrag(new MyScheduleFragment(), "MY SCHEDULE");
        adapter.addFrag(new BreakoutFragment(), "ADD");
        adapter.addFrag(new FeedbackFragment(), "FEEDBACK");
        viewPager.setAdapter(adapter);
    }

    /*
    * Method to notify the notification view that we have updated information.
    * Ensures that if new information is in the system, the home fragment updates with it.
    * *Implemented from RequestSpreadsheets.iRequestSheet
    * */
    @Override
    public void communicateNotificationResult() {
        if(adapter.getItem(0) instanceof HomeFragment) {
            HomeFragment home = (HomeFragment) adapter.getItem(0);
            home.notificationResult();
        }
    }

    /*
    * Sets the current item in the viewpager to "MySchedule" Tab
    * *Implemented from HomeFragment.iHomeFragment
    * */
    @Override
    public void addToClassFirst() {
        highlightSelectedIcon(2, 0);
        viewPager.setCurrentItem(2);
    }
    /*
    * switch to Presentation activity
    * *Implemented from HomeFragment.iHomeFragment
    * */
    @Override
    public void viewPresentation(Breakouts breakout, int pres_id) {
        Intent i = new Intent(this, PresentationActivity.class);
        int id = breakout.getId();
        String start = breakout.getStartReadable();
        String end = breakout.getEndReadable();
        String day = breakout.getDayOfWeek();
        i.putExtra(AddScheduleAdapter.BREAKOUT_ID_TAG, id);
        i.putExtra(AddScheduleAdapter.BREAKOUT_START_TAG, start);
        i.putExtra(AddScheduleAdapter.BREAKOUT_END_TAG, end);
        i.putExtra(AddScheduleAdapter.BREAKOUT_DAY_TAG, day);
        i.putExtra(AddScheduleAdapter.PRESENTATION_ID, pres_id);
        startActivity(i);
    }

    /*
    *  update the upcoming schedule adapter when the MySchedule has changed
    * Implemented from MyScheduleAdapter.iMySchedule
    * */
    @Override
    public void scheduleChanged() {
        if(adapter.getItem(0) instanceof HomeFragment) {
            /*HomeFragment home = (HomeFragment) adapter.getItem(0);
            home.scheduleChanged();*/
            adapter.update();
        }
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public void update(){
            notifyDataSetChanged();
        }
        @Override
        public int getItemPosition(Object object) {
            if (object instanceof HomeFragment) {
                return POSITION_NONE;
            }
            //don't return POSITION_NONE, avoid fragment recreation.
            return super.getItemPosition(object);
        }
        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "";        }
    }

    /*
    * AlarmReceiver posts notifications to phone from alarms set
    * */
    public static class AlarmReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            SharedPreferences prefs = PreferenceManager
                    .getDefaultSharedPreferences(context);
            if(prefs.getBoolean(MainActivity.TOGGLE_NOTIFICATIONS, false)) {
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(context)
                                .setSmallIcon(R.drawable.ic_chat_24dp)
                                .setContentTitle("Test Feedback Notification")
                                .setContentText("Testing... testing... 1, 2, 3");
                Intent resultIntent = new Intent(context, MainActivity.class);
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                stackBuilder.addParentStack(MainActivity.class);
                stackBuilder.addNextIntent(resultIntent);
                PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                mBuilder.setContentIntent(resultPendingIntent);
                NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.notify(1, mBuilder.build());
            }
        }
    }
    /*
    * Test notification
    * */
    private void handleNotification() {
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 5000, pendingIntent);
        DatabaseHandler dh = new DatabaseHandler(this);
        Breakouts breakout = dh.getBreakout(1);
        alarmManager.set(AlarmManager.RTC_WAKEUP, breakout.getDate().getTime(), pendingIntent);

    }


}
