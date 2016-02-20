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

import com.android.volley.Request;

import java.util.List;

import innatemobile.storymakerevents.Adapters.AddScheduleAdapter;
import innatemobile.storymakerevents.Adapters.UpcomingScheduleAdapter;
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
