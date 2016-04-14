package innatemobile.storymakerevents.Fragments;


import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import innatemobile.storymakerevents.Models.Presentations;
import innatemobile.storymakerevents.Models.Spreadsheets;
import innatemobile.storymakerevents.R;
import innatemobile.storymakerevents.Utils.DatabaseHandler;

/**
 *Feedback fragment displays buttons to link to the google feedback forms
 */
public class FeedbackFragment extends Fragment implements View.OnClickListener {


    public FeedbackFragment() {
        // Required empty public constructor
    }
    SharedPreferences prefs;
    TextView txtCourse, txtConference;
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_feedback, container, false);
        bindViews();
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
           /* case R.id.toggleFeedback:
                if(!prefs.getBoolean(MainActivity.TOGGLE_NOTIFICATIONS, false)) {
                    // run your one time code
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean(MainActivity.TOGGLE_NOTIFICATIONS, true);
                    editor.apply();
                    Snackbar.make(v, "Notifications for feedback turned on", Snackbar.LENGTH_LONG).show();
                    txtSubText.setText("Notifications for feedback reminders are on");
                }else{
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean(MainActivity.TOGGLE_NOTIFICATIONS, false);
                    Snackbar.make(v, "Notifications for feedback turned off", Snackbar.LENGTH_LONG).show();
                    txtSubText.setText("Notifications for feedback reminders are off");
                    editor.apply();
                }
                break;*/
            case R.id.txtCourseFeedback:
                googleFeedback(Spreadsheets.COURSE_SHEET, 1);
                break;
            case R.id.txtConferenceFeedback:
                googleFeedback(Spreadsheets.CONFERENCE_SHEET, -1);
                break;
        }
    }

    /**
     * Binds the data to the views
     * currently the toggle to turn notifications on and off is commented out until the functionality is needed
     * */
    public void bindViews(){
        //android.support.v7.widget.SwitchCompat toggle = (SwitchCompat) view.findViewById(R.id.toggleFeedback);
        //txtSubText    = (TextView) view.findViewById(R.id.txtFeedbackSub);
        txtCourse     = (TextView) view.findViewById(R.id.txtCourseFeedback);
        txtConference = (TextView) view.findViewById(R.id.txtConferenceFeedback);
        prefs = PreferenceManager
                .getDefaultSharedPreferences(getActivity());
        /*if(!prefs.getBoolean(MainActivity.TOGGLE_NOTIFICATIONS, false)) {
            toggle.setChecked(false);
            txtSubText.setText("Notifications for feedback reminders are off");
        }else{
            toggle.setChecked(true);
            txtSubText.setText("Notifications for feedback reminders are on");
        }*/
        //toggle.setOnClickListener(this);
        txtConference.setOnClickListener(this);
        txtCourse.setOnClickListener(this);
    }

    /**
     * Opens google feedback forms
     * */
    public void googleFeedback(String spreadsheetName, int pres_id){
        DatabaseHandler dh = new DatabaseHandler(getContext());
        String url = dh.getSpreadsheetLink(spreadsheetName);
        if(pres_id != -1){
            Presentations pres = dh.getPresentation(pres_id);
            if(pres!=null) {
                url += dh.getSpreadsheetKey(spreadsheetName);
                try {
                    url += URLEncoder.encode(pres.getTitle(), "UTF-8");
                }catch(UnsupportedEncodingException e){
                    Log.d("FeedbackFragment", "UnsupportedEncoding", e);
                }
            }
        }

        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }
}
