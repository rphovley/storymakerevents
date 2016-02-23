package innatemobile.storymakerevents.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import innatemobile.storymakerevents.Adapters.AddScheduleAdapter;
import innatemobile.storymakerevents.Adapters.BreakoutAdapter;
import innatemobile.storymakerevents.Models.Presentations;
import innatemobile.storymakerevents.R;
import innatemobile.storymakerevents.Utils.DatabaseHandler;

public class PresentationActivity extends AppCompatActivity {
    String start;
    String end;
    String day;
    int breakoutID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_presentation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        int breakoutID = getIntent().getExtras().getInt(BreakoutAdapter.BREAKOUT_ID_TAG);
        String start = getIntent().getExtras().getString(BreakoutAdapter.BREAKOUT_START_TAG);
        String end = getIntent().getExtras().getString(BreakoutAdapter.BREAKOUT_END_TAG);
        String day = getIntent().getExtras().getString(BreakoutAdapter.BREAKOUT_DAY_TAG);
        int pres_id = getIntent().getExtras().getInt(AddScheduleAdapter.PRESENTATION_ID);
        DatabaseHandler dh = new DatabaseHandler(getApplicationContext());
        Presentations pres = dh.getPresentation(pres_id);
        getSupportActionBar().setTitle(pres.getTitle());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // do something useful
                Intent i = new Intent(this, AddScheduleActivity.class);
                i.putExtra(BreakoutAdapter.BREAKOUT_ID_TAG, breakoutID);
                i.putExtra(BreakoutAdapter.BREAKOUT_START_TAG, start);
                i.putExtra(BreakoutAdapter.BREAKOUT_END_TAG, end);
                i.putExtra(BreakoutAdapter.BREAKOUT_DAY_TAG, day);
                startActivity(i);
                return(true);
        }

        return(super.onOptionsItemSelected(item));
    }

}
