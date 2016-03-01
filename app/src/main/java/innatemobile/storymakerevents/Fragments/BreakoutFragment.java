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

import java.util.List;

import innatemobile.storymakerevents.Adapters.BreakoutAdapter;
import innatemobile.storymakerevents.Models.Breakouts;
import innatemobile.storymakerevents.R;
import innatemobile.storymakerevents.Utils.AppController;
import innatemobile.storymakerevents.Utils.DatabaseHandler;
import innatemobile.storymakerevents.Utils.RequestSpreadsheets;

/**
 * List out the available times for classes in breakout format
 */
public class BreakoutFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    /************Class Scope Variables**********/
    RecyclerView breakoutView;
    LinearLayoutManager llm;
    BreakoutAdapter adapter;
    List<Breakouts> breakoutList;
    /************Class Scope Variables**********/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        DatabaseHandler dh = new DatabaseHandler(getContext());
        breakoutList = dh.getAllBreakoutsRefined();
        View view = null;
        /************WHEN THE DATABASE HAS BEEN LOADED CORRECTLY**********/
        if(AppController.checkDatabaseForContent(getContext())) {
            /*SET UP --RECYCLERVIEW*/
            view = inflater.inflate(R.layout.fragment_breakout, container, false);
            breakoutView = (RecyclerView) view.findViewById(R.id.recyclerview);
            breakoutView.setHasFixedSize(true);
            llm = new LinearLayoutManager(getContext());
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            breakoutView.setLayoutManager(llm);
            adapter = new BreakoutAdapter(breakoutList, getActivity());
            breakoutView.setAdapter(adapter);
        }
        /************NEED TO RELOAD THE DATA**********/
        else{
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
        return view;
    }


}
