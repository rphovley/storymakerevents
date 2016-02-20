package innatemobile.storymakerevents.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
public class AddScheduleAdapter extends RecyclerView.Adapter<AddScheduleAdapter.AddScheduleCardViewHolder> {

    public List<Schedules> schedulesList;
    public Activity activity;
    DatabaseHandler dh;

    public AddScheduleAdapter(List<Schedules> schedulesList, Activity activity)
    {
        this.schedulesList     = schedulesList;
        this.activity          = activity;
    }

    @Override
    public int getItemCount() {
        return schedulesList.size();
    }

    @Override
    public void onBindViewHolder(AddScheduleCardViewHolder holder, int position) {
        dh = new DatabaseHandler(activity);
        int presentation_id = schedulesList.get(position).getPresentation_id();
        Presentations presentation = dh.getPresentation(presentation_id);
        int speaker_id      = presentation.getSpeaker_id();
        Speakers speaker = dh.getSpeaker(speaker_id);
        if(speaker!=null) {
            String speakerName = speaker.getName();
            holder.txtSpeakerName.setText(speakerName);
        }
        if(presentation!=null) {
            String presentationName = presentation.getTitle();
            holder.txtPresentationName.setText(presentationName);
        }

        dh.close();
    }
    @Override
    public AddScheduleCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_schedule_card, parent, false);
        return new AddScheduleCardViewHolder(itemView);
    }


    public class AddScheduleCardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        protected TextView txtPresentationName, txtSpeakerName;
        protected ImageView btnAdd;
        public AddScheduleCardViewHolder(final View itemView) {
            super(itemView);
            txtPresentationName = (TextView) itemView.findViewById(R.id.txtPresentationTitle);
            txtSpeakerName      = (TextView) itemView.findViewById(R.id.txtPresenterName);
            btnAdd              = (ImageView) itemView.findViewById(R.id.btnAddToSchedule);
            btnAdd.setOnClickListener(this);
            /*txtBreakoutName = (TextView) itemView.findViewById(R.id.breakoutName);
            txtBreakoutTime = (TextView) itemView.findViewById(R.id.breakoutTime);
            cardBreakout    = itemView.findViewById(R.id.breakout_card);
            cardBreakout.setOnClickListener(this);*/
        }

        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.btnAddToSchedule:
                    dh = new DatabaseHandler(activity);
                    dh.addMySchedule(schedulesList.get(this.getAdapterPosition()));
                    dh.close();
                    break;
            }
        }
    }
}
