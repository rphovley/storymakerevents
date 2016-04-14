package innatemobile.storymakerevents.Activities;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.cloudinary.Cloudinary;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import innatemobile.storymakerevents.Models.Schedules;
import innatemobile.storymakerevents.R;
import innatemobile.storymakerevents.Utils.AppController;
import innatemobile.storymakerevents.Utils.DatabaseHandler;

public class HelpActivity extends AppCompatActivity {
    TextView txtHelp1, txtHelp2, txtHelp3, txtHelp4, txtHelp5;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        initToolbar();
        bindViews();

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
        getSupportActionBar().setTitle("Help");
    }
    /**
     * bind data to the views in the layout
     * */
    public void bindViews(){
        int helpFrom = getIntent().getExtras().getInt(AppController.HELP_FROM_TAG);
        txtHelp1 = (TextView) findViewById(R.id.txtHelp1);
        txtHelp2 = (TextView) findViewById(R.id.txtHelp2);
        txtHelp3 = (TextView) findViewById(R.id.txtHelp3);
        txtHelp4 = (TextView) findViewById(R.id.txtHelp4);
        txtHelp5 = (TextView) findViewById(R.id.txtHelp5);

        switch (helpFrom){
            case AppController.HOME_POS:
                txtHelp1.setText(setMultipleSpan(
                        setImageSpan("To add a presentation to your schedule, press the ", R.drawable.calendar_help_add,
                                " button in your top tab.  Then select the time period you want and then press the "),
                        R.drawable.ic_add_circle_green_help, " button to add the class to your schedule"));
                txtHelp2.setText(R.string.sNextClassHelp);
                txtHelp3.setText(R.string.sUpcomingHelp);
                txtHelp4.setText(R.string.sRemoveTip);

                txtHelp5.setText(setImageSpan("To update your schedule to match any changes from Storymakers, select the ", R.drawable.ic_help_sync, " icon on the home page"));
                break;
            case AppController.SCHEDULE_POS:
                txtHelp1.setText(setImageSpan("To add a presentation to an empty period in your schedule, press the ", R.drawable.ic_add_circle_grey_help, " button."));
                txtHelp2.setText(R.string.sRemoveTip);
                txtHelp3.setText(R.string.sDetailHelp);
                break;
            case AppController.ADD_POS:
                txtHelp1.setText(setImageSpan("To add a presentation to your schedule, press the time period you want, and then press the ", R.drawable.ic_add_circle_green_help, " button to add the class to your schedule"));
                break;
        }
    }
    /**
     * Sets an image inside of a string to wrap the string around the image
     * */
    public SpannableStringBuilder setImageSpan(String start, int drawable, String end){
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(start).append(":");
        builder.setSpan(new ImageSpan(this, drawable),
                builder.length() - 1, builder.length(), 0);
        builder.append(end);
        return builder;
    }

    /**
     * Sets an image around a string that already has been wrapped around an image
     * */
    public SpannableStringBuilder setMultipleSpan(SpannableStringBuilder sBuilder, int drawable, String end){
        SpannableStringBuilder builder = sBuilder.append(":");
        builder.setSpan(new ImageSpan(this, drawable),
                builder.length() - 1, builder.length(), 0);
        builder.append(end);
        return builder;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                AppController.switchToMain(this, AppController.HOME_POS, -1);
                return(true);
        }

        return(super.onOptionsItemSelected(item));
    }
}
