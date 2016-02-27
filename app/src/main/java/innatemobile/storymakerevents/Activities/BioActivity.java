package innatemobile.storymakerevents.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.cloudinary.Cloudinary;

import java.util.HashMap;
import java.util.Map;

import innatemobile.storymakerevents.Adapters.AddScheduleAdapter;
import innatemobile.storymakerevents.Adapters.BreakoutAdapter;
import innatemobile.storymakerevents.Models.Breakouts;
import innatemobile.storymakerevents.Models.Schedules;
import innatemobile.storymakerevents.Models.Speakers;
import innatemobile.storymakerevents.Models.Spreadsheets;
import innatemobile.storymakerevents.R;
import innatemobile.storymakerevents.Utils.AppController;
import innatemobile.storymakerevents.Utils.DatabaseHandler;
import innatemobile.storymakerevents.Utils.ParseJSON;

public class BioActivity extends AppCompatActivity {

    TextView txtBio, txtSpeakerName;
    ImageView imgSpeaker;

    int speaker_id, schedule_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bio);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
       /* getSupportActionBar().setIcon(R.drawable.ic_home_black_24px);*/
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        speaker_id = getIntent().getExtras().getInt(PresentationActivity.SPEAKER_ID);
        schedule_id = getIntent().getExtras().getInt(PresentationActivity.SCHEDULE_ID);
        DatabaseHandler dh = new DatabaseHandler(this);
        Speakers speaker = dh.getSpeaker(speaker_id);
        Map config = new HashMap();
        config.put("cloud_name","innatemobile");
        config.put("api_key", "25355876463913");
        config.put("api_secret", "v9sw4llJArudRhzXc5oVhy-7FPE");
        Cloudinary cloudinary = new Cloudinary(config);

        txtBio = (TextView) findViewById(R.id.txtBio);
        txtSpeakerName = (TextView) findViewById(R.id.txtSpeakerName);
        imgSpeaker = (ImageView) findViewById(R.id.imgSpeaker);
        String url1 = "http://res.cloudinary.com/innatemobile/image/upload/sample.jpg";
        String url2 = cloudinary.url().imageTag("sample.jpg");
        /*ImageLoader imgLoader = AppController.getInstance().getImageLoader();
        imgLoader.get(url1, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                if (response.getBitmap() != null) {
                    imgSpeaker.setAdjustViewBounds(true);
                    imgSpeaker.setImageBitmap(response.getBitmap());

                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });*/
        if(speaker!=null) {
            txtBio.setText(speaker.getBio());
            txtSpeakerName.setText(speaker.getName());
        }







    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // do something useful
                DatabaseHandler dh = new DatabaseHandler(this);
                Schedules sched = dh.getSchedule(schedule_id);
                Breakouts breakout = dh.getBreakout(sched.getBreakout_id());
                Intent i = new Intent(this, PresentationActivity.class);
                i.putExtra(BreakoutAdapter.BREAKOUT_ID_TAG, breakout.getId());
                i.putExtra(BreakoutAdapter.BREAKOUT_START_TAG, breakout.getStartReadable());
                i.putExtra(BreakoutAdapter.BREAKOUT_END_TAG, breakout.getEndReadable());
                i.putExtra(BreakoutAdapter.BREAKOUT_DAY_TAG, breakout.getDayOfWeek());
                i.putExtra(AddScheduleAdapter.PRESENTATION_ID, sched.getPresentation_id());
                startActivity(i);
                return (true);

        }
        return false;
    }


}
