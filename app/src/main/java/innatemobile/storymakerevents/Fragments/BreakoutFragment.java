package innatemobile.storymakerevents.Fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;
import innatemobile.storymakerevents.Adapters.BreakoutAdapter;
import innatemobile.storymakerevents.Models.Breakouts;
import innatemobile.storymakerevents.R;
import innatemobile.storymakerevents.Utils.AppController;
import innatemobile.storymakerevents.Utils.DatabaseHandler;

/**
 * List out the available times for classes in breakout format
 */
public class BreakoutFragment extends Fragment {

    /************Class Scope Variables**********/
    RecyclerView breakoutView;
    LinearLayoutManager llm;
    BreakoutAdapter adapter;
    List<Breakouts> breakoutList;

    /*VIEWS*/
    View view;
    /************Class Scope Variables**********/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_breakout, container, false);

        // Inflate the layout for this fragment
        DatabaseHandler dh = new DatabaseHandler(getContext());
        breakoutList = dh.getAllBreakoutsRefined();
        dh.close();
        setRecyclerView();
        AppController.logTimes("BREAKOUT FRAGMENT");
        return view;
    }

    public void setRecyclerView(){
        breakoutView = (RecyclerView) view.findViewById(R.id.recyclerview);
        breakoutView.setHasFixedSize(true);
        llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        breakoutView.setLayoutManager(llm);
        adapter = new BreakoutAdapter(breakoutList, getActivity());
        breakoutView.setAdapter(adapter);
    }

}
