package innatemobile.storymakerevents.Activities;

import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import innatemobile.storymakerevents.Fragments.AllSpeakersFragment;
import innatemobile.storymakerevents.Fragments.BreakoutFragment;
import innatemobile.storymakerevents.Fragments.HomeFragment;
import innatemobile.storymakerevents.Fragments.MyScheduleFragment;
import innatemobile.storymakerevents.R;
import innatemobile.storymakerevents.Utils.RequestSpreadsheets;

/*Main Activity is controlling the fragments coming in and out of the view
*
* The initial screen is to display the current schedule.  If there is nothing in their
* schedule, then the screen will display a small add icon, and the plus floating action
* button.  When either are pressed, it will take them to the full schedule page, which will
* have the filters for each breakout.  They will then be able to select schedules until
* they want to go back.  The main home page will then display their current schedule.
* */
public class MainActivity extends AppCompatActivity implements RequestSpreadsheets.iRequestSheet{
    FragmentManager fragManager = getSupportFragmentManager();
    FragmentTransaction ft;
    TabLayout tabLayout;
    ViewPager viewPager;
    Drawable home, myschedule, schedule, notification;
    int tab_home, tab_my_schedule, tab_schedule, tab_notification;
    int colorPrimaryDark, colorIconWhite;
    int selectedPos = 0;
    private List<Drawable> tabIcons;
    ViewPagerAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        home           = ContextCompat.getDrawable(this, R.drawable.ic_home_black_24px);
        myschedule     = ContextCompat.getDrawable(this, R.drawable.calendar);
        schedule       = ContextCompat.getDrawable(this, R.drawable.calendar_add);
        notification   = ContextCompat.getDrawable(this, R.drawable.ic_chat_24dp);
        tab_home       = R.layout.tab_icon;

        colorPrimaryDark = ContextCompat.getColor(this, R.color.colorPrimaryDark);
        colorIconWhite   = ContextCompat.getColor(this, R.color.color_icons);
        tabIcons = new ArrayList<>(Arrays.asList(home, myschedule, schedule, notification));
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(3);
        setupViewPager(viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();
        if(getIntent().getExtras()!=null) {//if we came from addschedule, set that as our selected fragment
            highlightSelectedIcon(2, 0);
            viewPager.setCurrentItem(2);
        }
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                //add highlight to selected icon
                highlightSelectedIcon(position, selectedPos);
            }
        });
    }

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
    private void highlightSelectedIcon(int position, int unselected){
        tabIcons.get(position).setColorFilter(new
                PorterDuffColorFilter(ContextCompat.getColor(this, R.color.colorPrimaryDark), PorterDuff.Mode.MULTIPLY));
        tabIcons.get(unselected).setColorFilter(new
                PorterDuffColorFilter(ContextCompat.getColor(this, R.color.color_icons), PorterDuff.Mode.MULTIPLY));
        selectedPos = position;
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new HomeFragment(), "HOME");
        adapter.addFrag(new MyScheduleFragment(), "SCHEDULE");
        adapter.addFrag(new BreakoutFragment(), "ADD");
        adapter.addFrag(new AllSpeakersFragment(), "FEEDBACK");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void communicateNotificationResult() {
        if(adapter.getItem(0) instanceof HomeFragment) {
            HomeFragment home = (HomeFragment) adapter.getItem(0);
            home.notificationResult();
        }
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
