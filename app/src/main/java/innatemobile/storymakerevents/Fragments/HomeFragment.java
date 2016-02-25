package innatemobile.storymakerevents.Fragments;


import android.animation.Animator;
import android.app.ActionBar;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;

import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import innatemobile.storymakerevents.Adapters.AddScheduleAdapter;
import innatemobile.storymakerevents.Adapters.UpcomingScheduleAdapter;
import innatemobile.storymakerevents.Models.Breakouts;
import innatemobile.storymakerevents.Models.Presentations;
import innatemobile.storymakerevents.Models.Schedules;
import innatemobile.storymakerevents.Models.Speakers;
import innatemobile.storymakerevents.Models.Spreadsheets;
import innatemobile.storymakerevents.R;
import innatemobile.storymakerevents.Utils.DatabaseHandler;
import innatemobile.storymakerevents.Utils.RequestSpreadsheets;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements UpcomingScheduleAdapter.iUpcomingAdapter {

    public HomeFragment() {
        // Required empty public constructor
    }
    ImageView synch, addToClass;
    TextView txtNotification;

    RecyclerView scheduleView;
    LinearLayoutManager llm;
    UpcomingScheduleAdapter adapter;
    List<Schedules> schedulesList;
    iHomeFragment iHome;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(getActivity());
        View view = null;
        iHome = (iHomeFragment) getActivity();
        if(!prefs.getBoolean("firstTimeHome", false)){
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
        }else{
            DatabaseHandler dh = new DatabaseHandler(getContext());
            schedulesList = dh.getNextThreeSchedule();
            //check for schedulesList size, if has something proceed as normally
            if(schedulesList!=null && schedulesList.size()>0)
            {
                view = inflater.inflate(R.layout.fragment_home, container, false);
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

                //all of our big home card views to insert information into
                closeNotificationCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
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


            /*SET UP --RECYCLERVIEW*/
                String previousDay = "";
                for (int i = 0; i < schedulesList.size(); i++) { // add in the day headers
                    Breakouts breakout = dh.getBreakout(schedulesList.get(i).getBreakout_id());
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
            }else{//if there's nothing, show only the synch button to get schedule
                view = inflater.inflate(R.layout.fragment_synch_error, container, false);
                ImageView synchSched = (ImageView) view.findViewById(R.id.imgSyncSchedule);
                synchSched.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ConnectivityManager cm =
                                (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

                        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                        boolean isConnected = activeNetwork != null &&
                                activeNetwork.isConnectedOrConnecting();
                        if(isConnected) {
                            RequestSpreadsheets requestSpreadsheets = new RequestSpreadsheets(getActivity(), true, false, false);
                            requestSpreadsheets.getSpreadsheetKeys();
                        }else{
                            Snackbar.make(v, "No Connection, please try again later.", Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
            }
            dh.close();
        }

        /*SET UP --RECYCLERVIEW*/
        return view;
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

    @Override
    public void viewPresentation(Breakouts breakout, int pres_id) {
        iHome.viewPresentation(breakout, pres_id);
    }

    public interface iHomeFragment {
        public void addToClassFirst();
        public void viewPresentation(Breakouts breakout, int pres_id);
    }
}
