package innatemobile.storymakerevents.Fragments;


import android.animation.Animator;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import innatemobile.storymakerevents.Adapters.MyScheduleAdapter;
import innatemobile.storymakerevents.Adapters.UpcomingScheduleAdapter;
import innatemobile.storymakerevents.Models.Schedules;
import innatemobile.storymakerevents.Models.Spreadsheets;
import innatemobile.storymakerevents.R;
import innatemobile.storymakerevents.Utils.DatabaseHandler;
import innatemobile.storymakerevents.Utils.RequestSpreadsheets;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyScheduleFragment extends Fragment {


    public MyScheduleFragment() {
        // Required empty public constructor
    }
    ImageView synch;
    TextView txtNotification;

    RecyclerView scheduleView;
    LinearLayoutManager llm;
    MyScheduleAdapter adapter;
    List<Schedules> schedulesList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.recyclerview_container, container, false);

        /*SET UP --RECYCLERVIEW*/
        DatabaseHandler dh = new DatabaseHandler(getContext());
        schedulesList = dh.getAllMySchedule();
        dh.close();
        scheduleView = (RecyclerView) view.findViewById(R.id.recyclerview);
        scheduleView.setHasFixedSize(true);
        llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        scheduleView.setLayoutManager(llm);
        adapter = new MyScheduleAdapter(schedulesList,getActivity());
        scheduleView.setAdapter(adapter);
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

}
