package innatemobile.storymakerevents.Fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import innatemobile.storymakerevents.Adapters.SpeakerAdapter;
import innatemobile.storymakerevents.Models.Breakouts;
import innatemobile.storymakerevents.Models.Presentations;
import innatemobile.storymakerevents.Models.Schedules;
import innatemobile.storymakerevents.Models.Speakers;
import innatemobile.storymakerevents.R;
import innatemobile.storymakerevents.Utils.DatabaseHandler;

/**
 * A placeholder fragment containing a simple view.
 */
public class AllSpeakersFragment extends Fragment {

    public AllSpeakersFragment() {
    }
    RecyclerView eventView;
    LinearLayoutManager llm;
    SpeakerAdapter adapter;
    List<Speakers> speakerList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recyclerview_container, container, false);
        eventView = (RecyclerView) view.findViewById(R.id.recyclerview);
        eventView.setHasFixedSize(true);
        llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        eventView.setLayoutManager(llm);
        DatabaseHandler dh = new DatabaseHandler(getContext());
        Speakers speaker;
        speakerList = dh.getAllSpeakers();
        speaker = dh.getSpeaker(1);
        List<Schedules> shceduleList = dh.getAllSchedule();
        List<Schedules> schedule1 = dh.getScheduleByBreakout(1);
        List<Schedules> schedule2 = dh.getScheduleByPresentation(1);
        Breakouts breakout = new Breakouts();
        breakout = dh.getBreakout(1);
        List<Presentations> presentationsList = dh.getAllPresentations();
        dh.close();
        adapter = new SpeakerAdapter(speakerList);
        eventView.setAdapter(adapter);

        return view;
    }
}
