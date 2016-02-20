package innatemobile.storymakerevents.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

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
    private static final int ADD_PRESENTATION_LAYOUT    = 0;
    private static final int REMOVE_PRESENTATION_LAYOUT = 1;
    DatabaseHandler dh;
    AdapterChanged ac;

    public AddScheduleAdapter(List<Schedules> schedulesList, Activity activity)
    {
        this.ac = (AdapterChanged) activity;
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
            String description = presentation.getDescription();
            if(presentation.getDescription().length()>90){
                description = description.substring(0,90).concat("...");
            }
            holder.txtPresentationName.setText(presentationName);
            holder.txtDescription.setText(description);
        }

        dh.close();
    }
    @Override
    public AddScheduleCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;
        if(viewType==ADD_PRESENTATION_LAYOUT) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_schedule_card, parent, false);
        }else{
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.remove_schedule_card, parent, false);
        }
        return new AddScheduleCardViewHolder(itemView);
    }

    @Override
    public int getItemViewType(int position) {
        dh = new DatabaseHandler(activity);
        int presentation_id = schedulesList.get(position).getPresentation_id();
        List<Schedules> myScheduleList = dh.getMyScheduleByPresentation(presentation_id);
        dh.close();
        if(myScheduleList!= null){
            //remove presentation layout
            return REMOVE_PRESENTATION_LAYOUT;
        }else{
            //add presentation layout
            return ADD_PRESENTATION_LAYOUT;
        }
    }

    public class AddScheduleCardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        protected TextView txtPresentationName, txtSpeakerName, txtRoom, txtDescription;
        protected TextView btnAdd, btnRemove;
        public AddScheduleCardViewHolder(final View itemView) {
            super(itemView);
            txtPresentationName = (TextView) itemView.findViewById(R.id.txtPresentationTitle);
            txtSpeakerName      = (TextView) itemView.findViewById(R.id.txtSpeakerName);
            txtRoom             = (TextView) itemView.findViewById(R.id.txtRoom);
            txtDescription      = (TextView) itemView.findViewById(R.id.txtPresentationDescription);
            btnAdd              = (TextView) itemView.findViewById(R.id.btnAddToSchedule);
            btnRemove           = (TextView) itemView.findViewById(R.id.btnRemoveFromSchedule);
            if(btnAdd!=null) {
                btnAdd.setOnClickListener(this);
            }
            if(btnRemove!=null){
                btnRemove.setOnClickListener(this);
            }
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
                    ac.addedPresentation(this.getAdapterPosition());
                    dh.close();
                    Snackbar.make(itemView, "Added to Schedule", Snackbar.LENGTH_LONG)
                            .setAction("Undo", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Toast.makeText(activity.getApplicationContext(), "UNDO!", Toast.LENGTH_SHORT).show();
                                }
                            }).show();
                    break;
                case R.id.btnRemoveFromSchedule:
                    //remove presentation from schedule
                    dh = new DatabaseHandler(activity);
                    dh.removeFromSchedule(schedulesList.get(this.getAdapterPosition()).getPresentation_id());
                    ac.removedPresentation(this.getAdapterPosition());
                    Snackbar.make(itemView, "Removed From Schedule", Snackbar.LENGTH_LONG)
                            .setAction("Undo", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Toast.makeText(activity.getApplicationContext(), "UNDO!", Toast.LENGTH_SHORT).show();
                                }
                            }).show();
                    break;
            }
        }
    }

    public interface AdapterChanged{
        public void addedPresentation(int position);
        public void removedPresentation(int position);
    }
}
