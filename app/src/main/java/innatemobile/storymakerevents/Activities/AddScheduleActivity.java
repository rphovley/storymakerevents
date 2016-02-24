package innatemobile.storymakerevents.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.util.List;

import innatemobile.storymakerevents.Adapters.AddScheduleAdapter;
import innatemobile.storymakerevents.Adapters.BreakoutAdapter;
import innatemobile.storymakerevents.Models.Schedules;
import innatemobile.storymakerevents.R;
import innatemobile.storymakerevents.Utils.DatabaseHandler;

public class AddScheduleActivity extends AppCompatActivity implements AddScheduleAdapter.AdapterChanged {
    public static String CAME_FROM_SCHEDULE = "came_from_schedule";

    RecyclerView scheduleView;
    LinearLayoutManager llm;
    AddScheduleAdapter adapter;
    List<Schedules> myScheduleList;
    List<Schedules> schedulesList;
    boolean cameFromBreakout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*SET UP -- GET VALUES*/
        setContentView(R.layout.activity_add_schedule);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        int breakoutID = getIntent().getExtras().getInt(BreakoutAdapter.BREAKOUT_ID_TAG);
        String start = getIntent().getExtras().getString(BreakoutAdapter.BREAKOUT_START_TAG);
        String end = getIntent().getExtras().getString(BreakoutAdapter.BREAKOUT_END_TAG);
        String day = getIntent().getExtras().getString(BreakoutAdapter.BREAKOUT_DAY_TAG);
        cameFromBreakout = getIntent().getExtras().getBoolean(BreakoutAdapter.BREAKOUT_CAME_FROM_BREAKOUT);

        getSupportActionBar().setTitle(day + " " + start + "-" + end);
        DatabaseHandler dh = new DatabaseHandler(this);
        schedulesList = dh.getScheduleByBreakout(breakoutID);
        /*SET UP -- GET VALUES*/

        /*SET UP -- RECYCLERVIEW*/
        scheduleView = (RecyclerView) findViewById(R.id.recyclerview);
        scheduleView.setHasFixedSize(true);
        llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        scheduleView.setLayoutManager(llm);
        adapter = new AddScheduleAdapter(schedulesList, this);
        scheduleView.setAdapter(adapter);
        dh.close();
        /*SET UP -- RECYCLERVIEW*/
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


    @Override
    public void addedPresentation(int position) {
        adapter.notifyItemChanged(position);
    }

    @Override
    public void removedPresentation(int position) {
        adapter.notifyItemChanged(position);
    }
}
