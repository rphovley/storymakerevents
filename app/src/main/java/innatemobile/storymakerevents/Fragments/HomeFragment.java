package innatemobile.storymakerevents.Fragments;


import android.animation.Animator;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.HashMap;
import java.util.List;

import innatemobile.storymakerevents.Adapters.UpcomingScheduleAdapter;
import innatemobile.storymakerevents.Models.Breakouts;
import innatemobile.storymakerevents.Models.ScheduleJoined;
import innatemobile.storymakerevents.Models.Spreadsheets;
import innatemobile.storymakerevents.R;
import innatemobile.storymakerevents.Utils.AppController;
import innatemobile.storymakerevents.Utils.DatabaseHandler;
import innatemobile.storymakerevents.Utils.RequestSpreadsheets;

/**
 * The HomeFragment displays a handful of "most" relevant information about the
 * conference to the user at the time.
 */
public class HomeFragment extends Fragment implements UpcomingScheduleAdapter.iUpcomingAdapter {

    public HomeFragment() {
        // Required empty public constructor
    }
    /************Class Scope Variables**********/
    ImageView addToClass;
    TextView txtNotification;
    RecyclerView scheduleView;
    LinearLayoutManager llm;
    UpcomingScheduleAdapter adapter;
    List<ScheduleJoined> schedulesList;
    iHomeFragment iHome;
    View view;
    /************Class Scope Variables**********/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        AppController.logTimes("START OF HOME FRAGMENT");
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(getActivity());
        view = null;
        iHome = (iHomeFragment) getActivity();
        /************FIRST TIME USE**********/
        if(!prefs.getBoolean("firstTimeHome", false)){ // action to run on first use of app
            // run your one time code
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("firstTimeHome", true);
            editor.apply();
            view = inflater.inflate(R.layout.fragment_home_first, container, false);
            addToClass = (ImageView) view.findViewById(R.id.imgAddClass);
            addToClass.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iHome.addToClassFirst();
                }
            });
        }
        /************EVERY OTHER TIME**********/
        else{ // every other time
            DatabaseHandler dh = new DatabaseHandler(getContext());
            schedulesList = dh.getNextThreeSchedule();
                view = inflater.inflate(R.layout.fragment_home, container, false);
                txtNotification = (TextView) view.findViewById(R.id.txtNotification);
            final CardView notificationCard = (CardView) view.findViewById(R.id.notificationCard);
                ImageView closeNotificationCard = (ImageView) view.findViewById(R.id.close_notification);
                FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);

                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Snackbar.make(view, "Add a class!", Snackbar.LENGTH_LONG).show();
                        iHome.addToClassFirst();
                    }
                });


                closeNotificationCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) { //what to do to our notification card on click
                        notificationCard.animate().alpha(0.0f).setListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                notificationCard.setVisibility(View.GONE);
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        });//fade out
                    }
                });


                if(schedulesList!=null)setupRecyclerView();
            }
        AppController.logTimes("END HOME FRAGMENT");
        return view;
    }

    private void setupRecyclerView(){
        String previousDay = "";
        for (int i = 0; i < schedulesList.size(); i++) { // add in the day headers
            Breakouts breakout = schedulesList.get(i).breakout;
            if (!breakout.getDayOfWeek().equals(previousDay)) {
                schedulesList.add(i, null);
                i++;
                previousDay = breakout.getDayOfWeek();
            }
        }
        scheduleView = (RecyclerView) view.findViewById(R.id.recyclerview);
        scheduleView.setHasFixedSize(true);
        llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        scheduleView.setLayoutManager(llm);
        adapter = new UpcomingScheduleAdapter(schedulesList, getActivity(), this);
        scheduleView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();

        if(txtNotification!=null) {
            DatabaseHandler dh = new DatabaseHandler(getContext());
            RequestSpreadsheets requester = new RequestSpreadsheets(getActivity(), false, false, true);
            requester.getNotificationSpreadsheet(getActivity().getString(R.string.spreadsheet_url) + dh.getSpreadsheetKey(Spreadsheets.NOTIFICATIONS_SHEET));
            txtNotification.setText(dh.getLastNotification());
            dh.close();
        }
    }

    public void notificationResult(){
        if(txtNotification!=null) {
            DatabaseHandler dh = new DatabaseHandler(getContext());
            txtNotification.setText(dh.getLastNotification());
            dh.close();
        }
    }

    @Override
    public void removeItem(int selected_id) {
        adapter.removeItem(selected_id);
    }

    @Override
    public void notifyItemsChanged(List<HashMap<Integer,Boolean>> changed) {
        for(int i = 0; i < changed.size(); i++){
            for(HashMap.Entry<Integer, Boolean> entry : changed.get(i).entrySet()) {
                if (entry.getValue()) {
                    adapter.notifyItemRemoved(entry.getKey());
                    adapter.notifyItemInserted(entry.getKey());
                } else {
                    adapter.notifyItemRemoved(entry.getKey());
                }
            }

        }
    }

    @Override
    public void addClass() {
        iHome.addToClassFirst();
    }

    public interface iHomeFragment {
        void addToClassFirst();
    }
}
