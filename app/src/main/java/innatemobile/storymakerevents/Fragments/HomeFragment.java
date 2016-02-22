package innatemobile.storymakerevents.Fragments;


import android.animation.Animator;
import android.app.ActionBar;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import java.util.List;

import innatemobile.storymakerevents.Adapters.AddScheduleAdapter;
import innatemobile.storymakerevents.Adapters.UpcomingScheduleAdapter;
import innatemobile.storymakerevents.Models.Breakouts;
import innatemobile.storymakerevents.Models.Schedules;
import innatemobile.storymakerevents.Models.Spreadsheets;
import innatemobile.storymakerevents.R;
import innatemobile.storymakerevents.Utils.DatabaseHandler;
import innatemobile.storymakerevents.Utils.RequestSpreadsheets;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {


    public HomeFragment() {
        // Required empty public constructor
    }
    ImageView synch;
    TextView txtNotification;

    RecyclerView scheduleView;
    LinearLayoutManager llm;
    UpcomingScheduleAdapter adapter;
    List<Schedules> schedulesList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        final CardView notificationCard = (CardView) view.findViewById(R.id.notificationCard);
        ImageView closeNotificationCard = (ImageView) view.findViewById(R.id.close_notification);
        txtNotification = (TextView) view.findViewById(R.id.txtNotification);
        ImageView synch = (ImageView) view.findViewById(R.id.synch);

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

        /*synch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestSpreadsheets requester = new RequestSpreadsheets(getActivity(), true, false, false);
                requester.getSpreadsheetKeys();
            }
        });*/

        /*SET UP --RECYCLERVIEW*/
        DatabaseHandler dh = new DatabaseHandler(getContext());
        schedulesList = dh.getAllMySchedule();
        String previousDay = "";
        schedulesList = setEmptyScheduleSpots(schedulesList);
        for(int i = 0; i < schedulesList.size(); i++){ // add in the day headers
            Breakouts breakout = dh.getBreakout(schedulesList.get(i).getBreakout_id());
            if(!breakout.getDayOfWeek().equals(previousDay)){
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
        adapter = new UpcomingScheduleAdapter(schedulesList,getActivity());
        scheduleView.setAdapter(adapter);
        /*SET UP --RECYCLERVIEW*/
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
                mySchedule = isBreakoutInSchedule(mySchedule, breakout);//do we have something in our schedule for that breakout?
            }
        }
        dh.close();
        return mySchedule;
    }

    public List<Schedules> isBreakoutInSchedule(List<Schedules> mySchedule, Breakouts testBreakout){
        int index = 0;
        int currentBreakoutId = 0;
        DatabaseHandler dh = new DatabaseHandler(getContext());
        Boolean isBreakoutInSchedule = false;
        Date mySchedTime = new Date(0);

        while (!isBreakoutInSchedule && mySchedTime.getTime() <= testBreakout.getDateAndStartTime().getTime()){ //while we are still looking for the time
            //gets the time for the schedule item currently being checked
            currentBreakoutId = mySchedule.get(index).getBreakout_id();
            Breakouts mySchedBreakout = dh.getBreakout(currentBreakoutId);
            mySchedTime.setTime(mySchedBreakout.getDateAndStartTime().getTime());
            Date test = testBreakout.getDateAndStartTime();
            Date test2 = mySchedTime;
            if(mySchedule.get(index).isPresentation() && test.getTime() == test2.getTime()){
                isBreakoutInSchedule = true;
            }
            index++;
        }
        if(!isBreakoutInSchedule){
            Schedules schedule = new Schedules();
            schedule.setId(1000 + index - 1);
            schedule.setIsPresentation(false);
            schedule.setBreakout_id(testBreakout.getId());
            schedule.setIsEmptyBreakout(true);
            mySchedule.add(index-1, schedule);
        }


/*
        while(currentBreakoutId <= breakout_id){//if we don't have something scheduled, we need to insert a blank into schedulesList
            if(mySchedule.get(index)!=null) {
                currentBreakoutId = mySchedule.get(index).getBreakout_id();
                Schedules sched = mySchedule.get(index);
                if (sched.isPresentation() && breakout_id == sched.getBreakout_id()) {

                    Schedules schedule = new Schedules();
                    schedule.setId(1000 + index);
                    schedule.setIsPresentation(false);
                    schedule.setBreakout_id(breakout_id);
                    schedule.setIsEmptyBreakout(true);
                    mySchedule.add(index, sched);
                }
            }

            index++;
        }*/
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

    public void notificationResult(){
        if(txtNotification!=null) {
            DatabaseHandler dh = new DatabaseHandler(getContext());
            txtNotification.setText(dh.getLastNotification());
            dh.close();
        }
    }

}
