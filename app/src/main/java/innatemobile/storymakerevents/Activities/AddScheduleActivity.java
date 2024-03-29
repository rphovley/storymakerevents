package innatemobile.storymakerevents.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import innatemobile.storymakerevents.Adapters.AddScheduleAdapter;
import innatemobile.storymakerevents.Models.ScheduleJoined;
import innatemobile.storymakerevents.R;
import innatemobile.storymakerevents.Utils.AppController;
import innatemobile.storymakerevents.Utils.DatabaseHandler;
/**
 * Activity to display a particular breakouts presentations
 * Provides the ability to add and remove a presentation from the schedule
 * Also links to the presentation activity to preview presentation specific info
 * */
public class AddScheduleActivity extends AppCompatActivity implements AddScheduleAdapter.AdapterChanged {
    public static String CAME_FROM_SCHEDULE = "came_from_schedule";

    RecyclerView scheduleView;
    LinearLayoutManager llm;
    AddScheduleAdapter adapter;
    List<ScheduleJoined> schedulesList;
    boolean cameFromBreakout;
    int breakoutID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*SET UP -- GET VALUES*/
        setContentView(R.layout.activity_add_schedule);
        initToolbar();
        getBreakoutInfo();
        setRecyclerView();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // do something useful
                Intent i = new Intent(this, MainActivity.class);
                if(cameFromBreakout) {
                    i.putExtra(CAME_FROM_SCHEDULE, true);
                }
                startActivity(i);
                return(true);
        }

        return(super.onOptionsItemSelected(item));
    }

    /**
     * Creates the toolbar at the top of the presentation page
     * */
    public void initToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_launcher_white);
        getSupportActionBar().setTitle("");
    }
    /**
     * Get the breakout information from the intent extras and the database
     * */
    public void getBreakoutInfo(){
        breakoutID   = getIntent().getExtras().getInt(AppController.BREAKOUT_ID_TAG);
        String start = getIntent().getExtras().getString(AppController.BREAKOUT_START_TAG);
        String end   = getIntent().getExtras().getString(AppController.BREAKOUT_END_TAG);
        String day   = getIntent().getExtras().getString(AppController.BREAKOUT_DAY_TAG);
        cameFromBreakout = getIntent().getExtras().getBoolean(AppController.BREAKOUT_CAME_FROM_BREAKOUT);
        DatabaseHandler dh = new DatabaseHandler(this);
        schedulesList = dh.getScheduleJoinByBreakout(breakoutID);
        if(getSupportActionBar()!=null) {
            getSupportActionBar().setTitle(day + " " + start + "-" + end);
        }
        dh.close();
    }
    /**
     * Setup the recyclerview
     * */
    public void setRecyclerView(){
        scheduleView = (RecyclerView) findViewById(R.id.recyclerview);
        scheduleView.setHasFixedSize(true);
        llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        scheduleView.setLayoutManager(llm);
        adapter = new AddScheduleAdapter(schedulesList, this);
        scheduleView.setAdapter(adapter);
    }
    /***********************INTERFACE METHODS****************************/

    @Override
    public void addedPresentation(int position) {
        adapter.notifyItemChanged(position);
    }

    /***********************INTERFACE METHODS****************************/
}
