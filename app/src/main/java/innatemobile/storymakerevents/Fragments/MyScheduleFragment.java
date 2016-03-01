package innatemobile.storymakerevents.Fragments;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import innatemobile.storymakerevents.Adapters.MyScheduleAdapter;
import innatemobile.storymakerevents.Models.Breakouts;
import innatemobile.storymakerevents.Models.Schedules;
import innatemobile.storymakerevents.Models.Spreadsheets;
import innatemobile.storymakerevents.R;
import innatemobile.storymakerevents.Utils.AppController;
import innatemobile.storymakerevents.Utils.DatabaseHandler;
import innatemobile.storymakerevents.Utils.RequestSpreadsheets;

/**
 * Displays the users schedule
 */
public class MyScheduleFragment extends Fragment implements MyScheduleAdapter.iUpcomingAdapter {


    public MyScheduleFragment() {
        // Required empty public constructor
    }
    /************Class Scope Variables**********/
    ImageView synch;
    TextView txtNotification;
    RecyclerView scheduleView;
    LinearLayoutManager llm;
    MyScheduleAdapter adapter;
    List<Schedules> schedulesList;
    iMySchedule iMySched;
    /************Class Scope Variables**********/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = null;


        DatabaseHandler dh = new DatabaseHandler(getContext());
        schedulesList = dh.getAllMySchedule();
        iMySched = (iMySchedule) getActivity();
        /************WHEN THE DATABASE HAS BEEN LOADED CORRECTLY**********/
        if(AppController.checkDatabaseForContent(getContext())) {
            view = inflater.inflate(R.layout.fragment_my_schedule, container, false);

            dh.close();
            /*SET UP --RECYCLERVIEW*/
            String previousDay = "";
            schedulesList = setEmptyScheduleSpots(schedulesList);
            for (int i = 0; i < schedulesList.size(); i++) { // add in the day headers
                Breakouts breakout = dh.getBreakout(schedulesList.get(i).getBreakout_id());
                if (!breakout.getDayOfWeek().equals(previousDay)) {
                    schedulesList.add(i, null);
                    i++;
                    previousDay = breakout.getDayOfWeek();
                }
            }

            dh.close();
            scheduleView = (RecyclerView) view.findViewById(R.id.recyclerview);
            scheduleView.setHasFixedSize(true);
            llm = new LinearLayoutManager(getContext());
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            scheduleView.setLayoutManager(llm);
            adapter = new MyScheduleAdapter(schedulesList, getActivity(), this);
            scheduleView.setAdapter(adapter);
            /*SET UP --RECYCLERVIEW*/
        }
        /************NEED TO RELOAD THE DATA**********/
        else{//if there's nothing, show only the synch button to get schedule
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
        return view;
    }
    private List<Schedules>  setEmptyScheduleSpots(List<Schedules> mySchedule){
        DatabaseHandler dh = new DatabaseHandler(getContext());
        List<Breakouts> breakoutsList = dh.getAllBreakouts();
        List<Integer> ar = new ArrayList<>();
        for(int i = 0; i < breakoutsList.size(); i++){ // Get all the breakouts into a list
            int breakout_id = -1;
            try {
                breakout_id = Integer.parseInt(breakoutsList.get(i).getBreakoutName());
            }catch(NumberFormatException e){
                //Toast.makeText(getContext(), "Not an Integer!: " + breakoutsList.get(i).getBreakoutName(), Toast.LENGTH_SHORT).show();
            }
            if(breakout_id != -1){//Check to see if we have something scheduled in that breakout
                Breakouts breakout = dh.getBreakout(breakout_id);
                mySchedule = MyScheduleAdapter.isBreakoutInSchedule(mySchedule, breakout, getContext());//do we have something in our schedule for that breakout?
            }
        }
        dh.close();
        return mySchedule;
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
        iMySched.scheduleChanged();

    }

    public interface iMySchedule{
        void scheduleChanged();
    }
}
