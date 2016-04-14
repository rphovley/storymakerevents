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

import innatemobile.storymakerevents.Activities.AddScheduleActivity;
import innatemobile.storymakerevents.Activities.MainActivity;
import innatemobile.storymakerevents.Activities.PresentationActivity;
import innatemobile.storymakerevents.Models.Breakouts;
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

    public List<ScheduleJoined> schedulesList;
    public Activity activity;
    DatabaseHandler dh;
    AdapterChanged ac;
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
    public void onBindViewHolder(AddScheduleCardViewHolder holder, int position) {
        dh = new DatabaseHandler(activity);
        int presentation_id = schedulesList.get(position).presentation.getId();
        Presentations presentation = dh.getPresentation(presentation_id);
        int speaker_id      = presentation.getSpeaker_id();
        Speakers speaker = dh.getSpeaker(speaker_id);
        if(speaker!=null) {
            String speakerName = speaker.getName();
            holder.txtSpeakerName.setText(speakerName);
        }
        if(presentation!=null) {
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
        }

        dh.close();
    }
    @Override
    public AddScheduleCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
        protected TextView txtPresentationName, txtSpeakerName, txtRoom, txtDescription;
        protected TextView btnAdd, btnRemove;
        protected View addCardView;
        public AddScheduleCardViewHolder(final View itemView) {
            super(itemView);
            txtPresentationName = (TextView) itemView.findViewById(R.id.txtPresentationTitle);
            txtSpeakerName      = (TextView) itemView.findViewById(R.id.txtSpeakerName);
            txtRoom             = (TextView) itemView.findViewById(R.id.txtRoom);
            txtDescription      = (TextView) itemView.findViewById(R.id.txtPresentationDescription);
            btnAdd              = (TextView) itemView.findViewById(R.id.btnAddToSchedule);
            btnRemove           = (TextView) itemView.findViewById(R.id.btnRemoveFromSchedule);
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
                    snackBarNotifySchedule(itemView, " Added to Schedule");
                    AppController.switchToMain(activity, AppController.SCHEDULE_POS, sched.getId());
                    break;
                case R.id.btnRemoveFromSchedule: //remove presentation from schedule
                    dh = new DatabaseHandler(activity);
                    dh.removeFromSchedule(schedulesList.get(this.getAdapterPosition()).presentation.getId());
                    dh.close();
                    snackBarNotifySchedule(itemView, " Removed From Schedule");
                    break;
                case R.id.add_schedule_card:
                    AppController.switchToPresentation(activity, schedulesList, getAdapterPosition());

            }
        }
        /**
         * calls the snackbar to notify the user that the item has been added to their schedule
         * */
        private void snackBarNotifySchedule(View itemView, String message){
            Presentations presents = dh.getPresentation(schedulesList.get(this.getAdapterPosition()).presentation.getId());
            ac.removedPresentation(this.getAdapterPosition());
            String presName;
            if(presents.getTitle().length()>15){
                presName = presents.getTitle().substring(0,15) + "...";
            }else{
                presName = presents.getTitle();
            }
            Snackbar.make(itemView,  presName + message, Snackbar.LENGTH_LONG).show();
        }
    }

    public interface AdapterChanged{
        void addedPresentation(int position);
        void removedPresentation(int position);
    }
}
