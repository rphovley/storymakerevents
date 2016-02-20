package innatemobile.storymakerevents.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import innatemobile.storymakerevents.Adapters.BreakoutAdapter;
import innatemobile.storymakerevents.Adapters.SpeakerAdapter;
import innatemobile.storymakerevents.Models.Breakouts;
import innatemobile.storymakerevents.Models.Presentations;
import innatemobile.storymakerevents.Models.Schedules;
import innatemobile.storymakerevents.Models.Speakers;
import innatemobile.storymakerevents.R;
import innatemobile.storymakerevents.Utils.DatabaseHandler;


public class BreakoutFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    RecyclerView breakoutView;
    LinearLayoutManager llm;
    BreakoutAdapter adapter;
    List<Breakouts> breakoutList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.recyclerview_container, container, false);
        breakoutView = (RecyclerView) view.findViewById(R.id.recyclerview);
        breakoutView.setHasFixedSize(true);
        llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        breakoutView.setLayoutManager(llm);
        DatabaseHandler dh = new DatabaseHandler(getContext());
        breakoutList = dh.getAllBreakoutsRefined();
        dh.close();
        adapter = new BreakoutAdapter(breakoutList, getActivity());
        breakoutView.setAdapter(adapter);
        return view;
    }


}
