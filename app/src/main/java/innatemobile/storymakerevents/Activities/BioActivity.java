package innatemobile.storymakerevents.Activities;

import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.cloudinary.Cloudinary;
import java.util.HashMap;
import java.util.Map;
import innatemobile.storymakerevents.Models.Speakers;
import innatemobile.storymakerevents.R;
import innatemobile.storymakerevents.Utils.AppController;
import innatemobile.storymakerevents.Utils.DatabaseHandler;
/**
 * Activity to display a particular speakers information (bio and image)
 * */
public class BioActivity extends AppCompatActivity {
    /************Class Scope Variables**********/
    final String IMAGE_URL = "http://res.cloudinary.com/innatemobile/image/upload/";
    Speakers speaker;
    /*VIEWS*/
    TextView txtBio, txtSpeakerName;
    ImageView imgSpeaker;
    /************Class Scope Variables**********/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bio);
        initToolbar();
        getSpeakerInfo();
        bindViews();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }
    /**
     * Creates the toolbar at the top of the presentation page
     * */
    public void initToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null) {
            getSupportActionBar().setTitle("");
        }
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_launcher_white);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDarkest));
        }
    }

    /**
     * get information about the presentation from intent extras and from database
     * */
    public void getSpeakerInfo(){
        int speaker_id = getIntent().getExtras().getInt(AppController.SPEAKER_ID);
        DatabaseHandler dh = new DatabaseHandler(this);
        speaker = dh.getSpeaker(speaker_id);
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name","innatemobile");
        config.put("api_key", "25355876463913");
        config.put("api_secret", "v9sw4llJArudRhzXc5oVhy-7FPE");
        Cloudinary cloudinary = new Cloudinary(config);

    }
    /**
     * bind data to the views in the layout
     * */
    public void bindViews(){
        txtBio = (TextView) findViewById(R.id.txtBio);
        txtSpeakerName = (TextView) findViewById(R.id.txtSpeakerName);
        imgSpeaker = (ImageView) findViewById(R.id.imgSpeaker);
        String image_name = speaker.getImage().toUpperCase().replace(" ", "_");
        String url = IMAGE_URL + image_name;
        ImageLoader imgLoader = AppController.getInstance().getImageLoader();
        imgLoader.get(url, new ImageLoader.ImageListener() {
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
        });
        if(speaker!=null) {
            txtBio.setText(speaker.getBio());
            txtSpeakerName.setText(speaker.getName());
        }
    }



}
