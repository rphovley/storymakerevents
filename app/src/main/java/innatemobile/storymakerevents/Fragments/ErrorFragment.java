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
import innatemobile.storymakerevents.R;
import innatemobile.storymakerevents.Utils.RequestSpreadsheets;

/**
 * This Fragment is used to display on the main page when the app
 * has not received all of the scheduling information it needs.  The
 * only option presented is to sync the schedule to attempt to get the
 * needed information
 */
public class ErrorFragment extends Fragment {


    public ErrorFragment() {
        // Required empty public constructor
    }

    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_synch_error, container, false);
        bindViews();
        return view;
    }

    /**
     * Binds the views to the data
     * */
    public void bindViews(){
        View syncPage = view.findViewById(R.id.synchImgContainer);
        syncPage.setOnClickListener(new View.OnClickListener() {
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
    }
}
