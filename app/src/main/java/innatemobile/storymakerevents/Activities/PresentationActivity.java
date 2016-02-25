package innatemobile.storymakerevents.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import innatemobile.storymakerevents.Adapters.AddScheduleAdapter;
import innatemobile.storymakerevents.Adapters.BreakoutAdapter;
import innatemobile.storymakerevents.Models.Breakouts;
import innatemobile.storymakerevents.Models.Presentations;
import innatemobile.storymakerevents.Models.Schedules;
import innatemobile.storymakerevents.Models.Speakers;
import innatemobile.storymakerevents.R;
import innatemobile.storymakerevents.Utils.DatabaseHandler;

public class PresentationActivity extends AppCompatActivity {
    String sStart;
    String sEnd;
    String sDay;
    int breakoutID;
    Presentations pres;
    List<Schedules> sched;
    ImageView imgUpNav;
    TextView txtPresentationName, txtPresentationDescription, txtLocation, txtTime, txtSpeaker, txtAddClass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_presentation);
        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");*/
        breakoutID = getIntent().getExtras().getInt(BreakoutAdapter.BREAKOUT_ID_TAG);
        sStart = getIntent().getExtras().getString(BreakoutAdapter.BREAKOUT_START_TAG);
        sEnd = getIntent().getExtras().getString(BreakoutAdapter.BREAKOUT_END_TAG);
        sDay = getIntent().getExtras().getString(BreakoutAdapter.BREAKOUT_DAY_TAG);
        int pres_id = getIntent().getExtras().getInt(AddScheduleAdapter.PRESENTATION_ID);
        DatabaseHandler dh = new DatabaseHandler(getApplicationContext());
        pres = dh.getPresentation(pres_id);
        sched = dh.getScheduleByBreakoutPres(breakoutID, pres_id);
        if(pres.isIntensive()==1){
            sched = dh.getScheduleIntByPresentation(pres_id, sched.get(0).getSection_id());
        }
        Breakouts start = dh.getBreakout(sched.get(0).getBreakout_id());
        Breakouts end = dh.getBreakout(sched.get(sched.size() - 1).getBreakout_id());
        String time = start.getDayOfWeek() + " " + start.getStartReadable() + "-" + end.getEndReadable();
        Speakers speaker = dh.getSpeaker(pres.getSpeaker_id());

        txtPresentationName        = (TextView) findViewById(R.id.txtPresentationTitle);
        txtPresentationDescription = (TextView) findViewById(R.id.txtPresentationDescription);
        txtLocation                = (TextView) findViewById(R.id.txtLocation);
        txtTime                    = (TextView) findViewById(R.id.txtTime);
        txtSpeaker                 = (TextView) findViewById(R.id.txtSpeakerName);
        txtAddClass                = (TextView) findViewById(R.id.txtAddClass);
        imgUpNav                   = (ImageView) findViewById(R.id.imgUpNav);

        txtAddClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseHandler dh = new DatabaseHandler(getApplicationContext());
                if(pres.isIntensive()==1) {
                    List<Schedules> intensive = dh.getScheduleIntByPresentation(pres.getId(), sched.get(0).getSection_id());
                    for(int j = 0; j< intensive.size(); j++){
                        dh.addMySchedule(intensive.get(j));
                    }
                }else{
                    dh.addMySchedule(sched.get(0));
                }
                dh.close();
                String presName = "";
                if(pres.getTitle().length()>15){
                    presName = pres.getTitle().substring(0,15) + "...";
                }else{
                    presName = pres.getTitle();
                }
                dh.close();
                Snackbar.make(txtPresentationName, presName + " Added to Schedule", Snackbar.LENGTH_LONG).show();
            }
        });
        imgUpNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // do something useful
                Intent i = new Intent(getApplication(), AddScheduleActivity.class);
                i.putExtra(BreakoutAdapter.BREAKOUT_ID_TAG, breakoutID);
                i.putExtra(BreakoutAdapter.BREAKOUT_START_TAG, sStart);
                i.putExtra(BreakoutAdapter.BREAKOUT_END_TAG, sEnd);
                i.putExtra(BreakoutAdapter.BREAKOUT_DAY_TAG, sDay);
                startActivity(i);
            }
        });
        txtPresentationName.setText(pres.getTitle());
        txtPresentationDescription.setText(pres.getDescription());
        txtLocation.setText(sched.get(0).getLocation());
        txtTime.setText(time);
        if(speaker!=null) {
            txtSpeaker.setText(speaker.getName());
        }
        dh.close();
    }
    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.presentation_menu, menu);
        return true;
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
            case R.id.action_add_class:
                DatabaseHandler dh = new DatabaseHandler(getApplicationContext());
                if(pres.isIntensive()==1) {
                    List<Schedules> intensive = dh.getScheduleIntByPresentation(pres.getId(), sched.get(0).getSection_id());
                    for(int j = 0; j< intensive.size(); j++){
                        dh.addMySchedule(intensive.get(j));
                    }
                }else{
                    dh.addMySchedule(sched.get(0));
                }
                dh.close();
                String presName = "";
                if(pres.getTitle().length()>15){
                    presName = pres.getTitle().substring(0,15) + "...";
                }else{
                    presName = pres.getTitle();
                }
                Snackbar.make(txtPresentationName, presName + " Added to Schedule", Snackbar.LENGTH_LONG).show();
                return(true);
        }

        return(super.onOptionsItemSelected(item));
    }*/

}
