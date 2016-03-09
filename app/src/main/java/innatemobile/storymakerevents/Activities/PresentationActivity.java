package innatemobile.storymakerevents.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import innatemobile.storymakerevents.Adapters.AddScheduleAdapter;
import innatemobile.storymakerevents.Adapters.BreakoutAdapter;
import innatemobile.storymakerevents.Models.Breakouts;
import innatemobile.storymakerevents.Models.Presentations;
import innatemobile.storymakerevents.Models.Schedules;
import innatemobile.storymakerevents.Models.Speakers;
import innatemobile.storymakerevents.Models.Spreadsheets;
import innatemobile.storymakerevents.R;
import innatemobile.storymakerevents.Utils.AppController;
import innatemobile.storymakerevents.Utils.DatabaseHandler;

public class PresentationActivity extends AppCompatActivity implements View.OnClickListener{
    public static final String SPEAKER_ID = "speaker_id";
    public  static final String SCHEDULE_ID = "schedule_id" ;
    String sStart;
    String sEnd;
    String sDay;
    int breakoutID;
    Presentations pres;
    List<Schedules> sched;
    ImageView imgUpNav;
    Speakers speaker;

    TextView txtPresentationName, txtPresentationDescription, txtLocation, txtTime, txtSpeaker, txtViewBio, txtFeedback;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_presentation);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_launcher_white);
        getSupportActionBar().setTitle("");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimaryDarkest));
        }
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
        speaker = dh.getSpeaker(pres.getSpeaker_id());

        txtPresentationName        = (TextView) findViewById(R.id.txtPresentationTitle);
        txtPresentationDescription = (TextView) findViewById(R.id.txtPresentationDescription);
        txtLocation                = (TextView) findViewById(R.id.txtLocation);
        txtTime                    = (TextView) findViewById(R.id.txtTime);
        txtSpeaker                 = (TextView) findViewById(R.id.txtSpeakerName);
        txtViewBio                 = (TextView) findViewById(R.id.txtViewBio);
        txtFeedback                = (TextView) findViewById(R.id.txtFeedback);
        txtFeedback.setOnClickListener(this);

        if(speaker!=null && speaker.getName()!=null) {
            txtViewBio.setOnClickListener(this);
        }else{
            txtViewBio.setVisibility(View.GONE);
        }

        txtPresentationName.setText(pres.getTitle());
        txtPresentationDescription.setText(pres.getDescription());
        txtLocation.setText(sched.get(0).getLocation());
        txtTime.setText(time);
        if(speaker!=null) {
            txtSpeaker.setText(speaker.getName());
        }
        dh.close();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.presentation_menu, menu);
        if(sched.get(0)!=null && !sched.get(0).isPresentation()){
            menu.findItem(R.id.action_add_class).setVisible(false);
        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                AppController.switchToMain(this, AppController.HOME_POS, -1);
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
                AppController.switchToMain(this, AppController.SCHEDULE_POS, sched.get(0).getId());
                return(true);
        }

        return(super.onOptionsItemSelected(item));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.txtFeedback:
                switchToFeedback();
                break;
            case R.id.txtViewBio:
                switchToBio();
                break;
        }
    }

    public void switchToBio(){
        Intent i = new Intent(getApplication(), BioActivity.class);
        i.putExtra(SPEAKER_ID, speaker.getId());
        i.putExtra(SCHEDULE_ID, sched.get(0).getId());
        startActivity(i);
    }

    public void switchToFeedback(){
        DatabaseHandler dh = new DatabaseHandler(this);
        String url = dh.getSpreadsheetLink(Spreadsheets.COURSE_SHEET);
        url += dh.getSpreadsheetKey(Spreadsheets.COURSE_SHEET);
        try {
            url += URLEncoder.encode(pres.getTitle(), "UTF-8");
        }catch(UnsupportedEncodingException e){
            Log.d("FeedbackFragment", "UnsupportedEncoding", e);
        }
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }
}
