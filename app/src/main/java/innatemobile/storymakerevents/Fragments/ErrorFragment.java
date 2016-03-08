package innatemobile.storymakerevents.Fragments;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import innatemobile.storymakerevents.R;
import innatemobile.storymakerevents.Utils.RequestSpreadsheets;

/**
 * A simple {@link Fragment} subclass.
 */
public class ErrorFragment extends Fragment {


    public ErrorFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_synch_error, container, false);
        ImageView synchSched = (ImageView) view.findViewById(R.id.imgSyncSchedule);
        synchSched.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager cm =
                        (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null &&
                        activeNetwork.isConnectedOrConnecting();
                if (isConnected) {
                    RequestSpreadsheets requestSpreadsheets = new RequestSpreadsheets(getActivity(), true, false, false);
                    requestSpreadsheets.getSpreadsheetKeys();
                } else {
                    Snackbar.make(v, "No Connection, please try again later.", Snackbar.LENGTH_LONG).show();
                }
            }
        });
        return view;

    }

}
