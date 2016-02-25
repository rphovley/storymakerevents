package innatemobile.storymakerevents.Fragments;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import innatemobile.storymakerevents.Activities.MainActivity;
import innatemobile.storymakerevents.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FeedbackFragment extends Fragment {


    public FeedbackFragment() {
        // Required empty public constructor
    }
    SharedPreferences prefs;
    TextView txtSubText;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_feedback, container, false);
        android.support.v7.widget.SwitchCompat toggle = (SwitchCompat) view.findViewById(R.id.toggleFeedback);
        txtSubText = (TextView) view.findViewById(R.id.txtFeedbackSub);

         prefs = PreferenceManager
                .getDefaultSharedPreferences(getActivity());
        if(!prefs.getBoolean(MainActivity.TOGGLE_NOTIFICATIONS, false)) {
            toggle.setChecked(false);
            txtSubText.setText("Notifications for feedback reminders are off");
        }else{
            toggle.setChecked(true);
            txtSubText.setText("Notifications for feedback reminders are on");
        }

        toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
            }
        });
        return view;
    }

}
