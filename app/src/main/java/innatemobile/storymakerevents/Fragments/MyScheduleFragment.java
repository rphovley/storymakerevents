package innatemobile.storymakerevents.Fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;
import innatemobile.storymakerevents.Adapters.MyScheduleAdapter;
import innatemobile.storymakerevents.Models.Breakouts;
import innatemobile.storymakerevents.Models.ScheduleJoined;
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

    public static MyScheduleFragment newInstance(int selectedSchedId){
        MyScheduleFragment f = new MyScheduleFragment();
        AppController.firstTimeFlash = true;
        Bundle args = new Bundle();
        args.putInt(SELECTED_ID, selectedSchedId);
        f.setArguments(args);
        return f;
    }
    /************Class Scope Variables**********/
    private static final String SELECTED_ID = "selected_id";
    TextView txtNotification;
    RecyclerView scheduleView;
    LinearLayoutManager llm;
    MyScheduleAdapter adapter;
    List<ScheduleJoined> schedulesList;
    iMySchedule iMySched;
    int selectedIndex = 0;
    View view;
    /************Class Scope Variables**********/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_my_schedule, container, false);
        AppController.logTimes("START MY SCHEDULE");

        DatabaseHandler dh = new DatabaseHandler(getContext());
        schedulesList = dh.getMyScheduleJoin();
        iMySched = (iMySchedule) getActivity();

        dh.close();
        /*SET UP --RECYCLERVIEW*/
        String previousDay = "";

        AppController.logTimes("BEFORE EMPTYSPOTS LIST");
        schedulesList = setEmptyScheduleSpots(schedulesList);

        AppController.logTimes("BEFORE HEADERS LIST");
        for (int i = 0; i < schedulesList.size(); i++) { // add in the day headers
            Breakouts breakout = schedulesList.get(i).breakout;

            AppController.logTimes("IN HEADERS LIST");
            if (!breakout.getDayOfWeek().equals(previousDay)) {
                schedulesList.add(i, null);
                i++;
                previousDay = breakout.getDayOfWeek();
            }
        }
        schedulesList.add(0, null);
        if(getArguments().containsKey(SELECTED_ID)) {
            selectedIndex = getPosFromSchedID(schedulesList,getArguments().getInt(SELECTED_ID));
        }
        AppController.logTimes("AFTER MODIFIED LIST");
        dh.close();
        scheduleView = (RecyclerView) view.findViewById(R.id.recyclerview);
        scheduleView.setHasFixedSize(true);
        llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        scheduleView.setLayoutManager(llm);
        adapter = new MyScheduleAdapter(schedulesList, getActivity(), this, selectedIndex);
        scheduleView.setAdapter(adapter);
        if(AppController.firstTimeFlash) {
            scheduleView.getLayoutManager().smoothScrollToPosition(scheduleView, null, selectedIndex + 1);
        }
        /*SET UP --RECYCLERVIEW*/



        AppController.logTimes("MY SCHEDULE FRAGMENT");
        return view;
    }
    private List<ScheduleJoined>  setEmptyScheduleSpots(List<ScheduleJoined> mySchedule){
        DatabaseHandler dh = new DatabaseHandler(getContext());
        List<Breakouts> breakoutsList = dh.getAllBreakouts();

        AppController.logTimes("BEFOREISBREAKOUTIN");
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

        AppController.logTimes("AFTERISBREAKOUTIN");
        return mySchedule;
    }
    private int getPosFromSchedID(List<ScheduleJoined> schedules, int scheduleID){
        for(int i = 0; i < schedules.size(); i++){
            if(schedules.get(i)!=null && schedules.get(i).schedule.getId()==scheduleID){
                return i;
            }
        }
        return 0;
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
