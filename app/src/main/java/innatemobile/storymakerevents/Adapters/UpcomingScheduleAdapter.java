package innatemobile.storymakerevents.Adapters;

import android.app.Activity;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import innatemobile.storymakerevents.Models.Breakouts;
import innatemobile.storymakerevents.Models.Presentations;
import innatemobile.storymakerevents.Models.Schedules;
import innatemobile.storymakerevents.Models.Speakers;
import innatemobile.storymakerevents.R;
import innatemobile.storymakerevents.Utils.DatabaseHandler;
import innatemobile.storymakerevents.Utils.RequestSpreadsheets;

/**
 * Created by rphovley on 2/14/2016.
 */
public class UpcomingScheduleAdapter extends RecyclerView.Adapter<UpcomingScheduleAdapter.UpcomingScheduleCardViewHolder> {

    private static final int TYPE_FIXED_SCHEDULE = 0;
    private static final int TYPE_MY_SCHEDULE = 1;
    private static final int TYPE_HEADER = 2;

    public List<Schedules> schedulesList;
    public Activity activity;
    DatabaseHandler dh;

    public UpcomingScheduleAdapter(List<Schedules> schedulesList, Activity activity)
    {
        this.schedulesList     = schedulesList;
        schedulesList.add(0,null);
        this.activity          = activity;
    }

    @Override
    public int getItemCount() {
        return schedulesList.size();
    }

    @Override
    public void onBindViewHolder(UpcomingScheduleCardViewHolder holder, int position) {
        if(position!=0) {
            dh = new DatabaseHandler(activity);
            int presentation_id = schedulesList.get(position).getPresentation_id();
            Presentations presentation = dh.getPresentation(presentation_id);
            int speaker_id = presentation.getSpeaker_id();
            Speakers speaker = dh.getSpeaker(speaker_id);
            int breakout_id =schedulesList.get(position).getBreakout_id();
            Breakouts breakout = dh.getBreakout(breakout_id);
            holder.txtRoom.setText(schedulesList.get(position).getLocation());
            if(breakout!=null) {
                holder.txtTime.setText(breakout.getDayOfWeek() + " " + breakout.getStartReadable() + " - " + breakout.getEndReadable());
            }
            if (speaker != null) {
                String speakerName = speaker.getName();
                holder.txtSpeakerName.setText(speakerName);
            }
            if (presentation != null) {
                String presentationName = presentation.getTitle();
                holder.txtPresentationName.setText(presentationName);
            }

            dh.close();
        }
    }
    @Override
    public UpcomingScheduleCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;
        switch(viewType){
            case TYPE_FIXED_SCHEDULE:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fixed_schedule_card, parent, false);
                break;
            case TYPE_MY_SCHEDULE:
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
        List<Schedules> myScheduleList = null;
        if(position!=0) {
            dh = new DatabaseHandler(activity);
            int presentation_id = schedulesList.get(position).getPresentation_id();
            myScheduleList = dh.getMyScheduleByPresentation(presentation_id);

            dh.close();
        }
        if(position==0){
            viewType = TYPE_HEADER;
        }else if(myScheduleList!=null && myScheduleList.get(0).isPresentation()){
            viewType = TYPE_MY_SCHEDULE;
        }else if(!myScheduleList.get(0).isPresentation()){
            viewType = TYPE_FIXED_SCHEDULE;
        }
        return viewType;
    }

    public class UpcomingScheduleCardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        protected TextView txtPresentationName, txtSpeakerName, txtRoom, txtTime;
        protected ImageView synch;
        public UpcomingScheduleCardViewHolder(final View itemView) {
            super(itemView);
            txtPresentationName = (TextView) itemView.findViewById(R.id.txtPresentationTitle);
            txtSpeakerName      = (TextView) itemView.findViewById(R.id.txtSpeakerName);
            txtRoom             = (TextView) itemView.findViewById(R.id.txtRoom);
            txtTime             = (TextView) itemView.findViewById(R.id.txtTime);
            synch = (ImageView) itemView.findViewById(R.id.synch);
            if(synch!=null) {
                synch.setOnClickListener(this);
            }


            /*txtBreakoutName = (TextView) itemView.findViewById(R.id.breakoutName);
            txtBreakoutTime = (TextView) itemView.findViewById(R.id.breakoutTime);
            cardBreakout    = itemView.findViewById(R.id.breakout_card);
            cardBreakout.setOnClickListener(this);*/
        }

        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.synch:
                    RequestSpreadsheets requester = new RequestSpreadsheets(activity, true, false, false);
                    requester.getSpreadsheetKeys();
                    break;
            }
        }
    }
}
