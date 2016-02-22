package innatemobile.storymakerevents.Adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import innatemobile.storymakerevents.Models.Presentations;
import innatemobile.storymakerevents.Models.Schedules;
import innatemobile.storymakerevents.Models.Speakers;
import innatemobile.storymakerevents.R;
import innatemobile.storymakerevents.Utils.DatabaseHandler;

/**
 * Created by rphovley on 2/14/2016.
 */
public class MyScheduleAdapter extends RecyclerView.Adapter<MyScheduleAdapter.UpcomingScheduleCardViewHolder> {

    private static final int TYPE_FIXED_SCHEDULE = 0;
    private static final int TYPE_HEADER = 1;
    public List<Schedules> schedulesList;
    public Activity activity;
    DatabaseHandler dh;

    public MyScheduleAdapter(List<Schedules> schedulesList, Activity activity)
    {
        this.schedulesList     = schedulesList;
        this.activity          = activity;
    }

    @Override
    public int getItemCount() {
        return schedulesList.size();
    }

    @Override
    public void onBindViewHolder(UpcomingScheduleCardViewHolder holder, int position) {
        dh = new DatabaseHandler(activity);
        if(!schedulesList.get(position).isEmptyBreakout()) {
            int presentation_id = schedulesList.get(position).getPresentation_id();
            Presentations presentation = dh.getPresentation(presentation_id);
            int speaker_id = presentation.getSpeaker_id();
            Speakers speaker = dh.getSpeaker(speaker_id);
            if (speaker != null) {
                String speakerName = speaker.getName();
                holder.txtSpeakerName.setText(speakerName);
            }
            if (presentation != null) {
                String presentationName = presentation.getTitle();
                holder.txtPresentationName.setText(presentationName);
            }
        }

        dh.close();
    }
    @Override
    public UpcomingScheduleCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;
        switch(viewType){
            case TYPE_FIXED_SCHEDULE:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_schedule_card, parent, false);
                break;
            case TYPE_HEADER:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fixed_home_recyclerview_content, parent, false);
                break;
        }
        return new UpcomingScheduleCardViewHolder(itemView);
    }

    @Override
    public int getItemViewType(int position) {
        int viewType = TYPE_FIXED_SCHEDULE;
        /*if(position==0){
            viewType = TYPE_HEADER;
        }*/
        return viewType;
    }

    public class UpcomingScheduleCardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        protected TextView txtPresentationName, txtSpeakerName;

        public UpcomingScheduleCardViewHolder(final View itemView) {
            super(itemView);
            txtPresentationName = (TextView) itemView.findViewById(R.id.txtPresentationTitle);
            txtSpeakerName      = (TextView) itemView.findViewById(R.id.txtSpeakerName);
        }

        @Override
        public void onClick(View v) {
            switch(v.getId()) {
            }
        }
    }
}
