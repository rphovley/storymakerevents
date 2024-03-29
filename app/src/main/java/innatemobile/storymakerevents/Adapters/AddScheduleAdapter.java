package innatemobile.storymakerevents.Adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import innatemobile.storymakerevents.Models.Presentations;
import innatemobile.storymakerevents.Models.ScheduleJoined;
import innatemobile.storymakerevents.Models.Schedules;
import innatemobile.storymakerevents.Models.Speakers;
import innatemobile.storymakerevents.R;
import innatemobile.storymakerevents.Utils.AppController;
import innatemobile.storymakerevents.Utils.DatabaseHandler;

/**
 * Created by rphovley on 2/14/2016.
 * Adapter to inflate schedule items for a particular breakout in the AddSchedule Activity
 */
public class AddScheduleAdapter extends RecyclerView.Adapter<AddScheduleAdapter.AddScheduleCardViewHolder> {

    /******************Class Scope Variables***********************/
    /**
     * Add presentation layout flag for the item
     * */
    private static final int ADD_PRESENTATION_LAYOUT    = 0;
    /**
     * Remove presentation layout flag for the item
     * */
    private static final int REMOVE_PRESENTATION_LAYOUT = 1;

    public Activity activity;
    private List<ScheduleJoined> schedulesList;
    private DatabaseHandler dh;
    private AdapterChanged ac;
    /******************Class Scope Variables***********************/

    public AddScheduleAdapter(List<ScheduleJoined> schedulesList, Activity activity)
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
    public void onBindViewHolder(@NonNull AddScheduleCardViewHolder holder, int position) {
        dh = new DatabaseHandler(activity);
        int presentation_id = schedulesList.get(position).presentation.getId();
        Presentations presentation = dh.getPresentation(presentation_id);
        int speaker_id      = presentation.getSpeaker_id();
        Speakers speaker = dh.getSpeaker(speaker_id);
        if(speaker!=null) {
            String speakerName = speaker.getName();
            holder.txtSpeakerName.setText(speakerName);
        }
        String presentationName = presentation.getTitle();
        if(presentationName.length()>45){
            presentationName = presentationName.substring(0,45).concat("...");
        }
        String description = presentation.getDescription();
        if(presentation.getDescription().length()>65){
            description = description.substring(0,65).concat("...");
        }
        holder.txtPresentationName.setText(presentationName);
        holder.txtDescription.setText(description);

        dh.close();
    }
    @NonNull
    @Override
    public AddScheduleCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
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
        int presentation_id = schedulesList.get(position).presentation.getId();
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
        TextView txtPresentationName, txtSpeakerName, txtRoom, txtDescription;
        TextView btnAdd, btnRemove;
        View addCardView;
        AddScheduleCardViewHolder(final View itemView) {
            super(itemView);
            txtPresentationName = itemView.findViewById(R.id.txtPresentationTitle);
            txtSpeakerName      = itemView.findViewById(R.id.txtSpeakerName);
            txtRoom             = itemView.findViewById(R.id.txtRoom);
            txtDescription      = itemView.findViewById(R.id.txtPresentationDescription);
            btnAdd              = itemView.findViewById(R.id.btnAddToSchedule);
            btnRemove           = itemView.findViewById(R.id.btnRemoveFromSchedule);
            addCardView         = itemView.findViewById(R.id.add_schedule_card);

            if(btnAdd!=null) {
                btnAdd.setOnClickListener(this);
            }
            if(btnRemove!=null){
                btnRemove.setOnClickListener(this);
            }
            if(addCardView!=null){
                addCardView.setOnClickListener(this);
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
                    Schedules sched = schedulesList.get(this.getAdapterPosition()).schedule;
                    Presentations present = dh.getPresentation(sched.getPresentation_id());

                    if(present.isIntensive()==1) {
                        List<Schedules> intensive = dh.getScheduleIntByPresentation(present.getId(), sched.getSection_id());
                        for(int i = 0; i< intensive.size(); i++){
                            dh.addMySchedule(intensive.get(i));
                        }
                    }else{
                        dh.addMySchedule(schedulesList.get(this.getAdapterPosition()).schedule);
                    }
                    ac.addedPresentation(this.getAdapterPosition());
                    dh.close();
                    AppController.switchToMain(activity, AppController.SCHEDULE_POS, sched.getId());
                    break;
                case R.id.btnRemoveFromSchedule: //remove presentation from schedule
                    dh = new DatabaseHandler(activity);
                    dh.removeFromSchedule(schedulesList.get(this.getAdapterPosition()).presentation.getId());
                    dh.close();
                    break;
                case R.id.add_schedule_card:
                    AppController.switchToPresentation(activity, schedulesList, getAdapterPosition());

            }
        }
    }

    public interface AdapterChanged{
        void addedPresentation(int position);
    }
}
