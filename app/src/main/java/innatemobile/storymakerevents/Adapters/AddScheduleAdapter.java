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
import innatemobile.storymakerevents.Activities.PresentationActivity;
import innatemobile.storymakerevents.Models.Breakouts;
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
    public static String BREAKOUT_ID_TAG = "breakout_id";
    public static String BREAKOUT_START_TAG = "start_time";
    public static String BREAKOUT_END_TAG   = "end_time";
    public static String BREAKOUT_DAY_TAG   = "day";
    public static String PRESENTATION_ID    = "presenation_id";
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
            if(presentationName.length()>10){
                presentationName = presentationName.substring(0,10).concat("...");
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
                    Schedules sched = schedulesList.get(this.getAdapterPosition());
                    Presentations present = dh.getPresentation(sched.getPresentation_id());

                    if(present.isIntensive()==1) {
                        List<Schedules> intensive = dh.getScheduleIntByPresentation(present.getId(), sched.getSection_id());
                        for(int i = 0; i< intensive.size(); i++){
                            dh.addMySchedule(intensive.get(i));
                        }
                    }else{
                        dh.addMySchedule(schedulesList.get(this.getAdapterPosition()));
                    }
                    ac.addedPresentation(this.getAdapterPosition());
                    dh.close();
                    String presName = "";
                    if(present.getTitle().length()>15){
                        presName = present.getTitle().substring(0,15) + "...";
                    }else{
                        presName = present.getTitle();
                    }
                    Snackbar.make(itemView, presName + " Added to Schedule", Snackbar.LENGTH_LONG).show();
                    break;
                case R.id.btnRemoveFromSchedule:
                    //remove presentation from schedule
                    dh = new DatabaseHandler(activity);
                    dh.removeFromSchedule(schedulesList.get(this.getAdapterPosition()).getPresentation_id());
                    Presentations presents = dh.getPresentation(schedulesList.get(this.getAdapterPosition()).getPresentation_id());
                    ac.removedPresentation(this.getAdapterPosition());
                    String presName2 = "";
                    if(presents.getTitle().length()>15){
                        presName2 = presents.getTitle().substring(0,15) + "...";
                    }else{
                        presName2 = presents.getTitle();
                    }
                    Snackbar.make(itemView,  presName2 + " Removed From Schedule", Snackbar.LENGTH_LONG).show();
                    break;
                case R.id.add_schedule_card:
                    Intent i = new Intent(activity, PresentationActivity.class);
                    int breakout_id = schedulesList.get(this.getAdapterPosition()).getBreakout_id();
                    Breakouts breakout = dh.getBreakout(breakout_id);

                    int id = breakout.getId();
                    String start = breakout.getStartReadable();
                    String end = breakout.getEndReadable();
                    String day = breakout.getDayOfWeek();
                    int pres_id = schedulesList.get(this.getAdapterPosition()).getPresentation_id();
                    i.putExtra(BREAKOUT_ID_TAG, id);
                    i.putExtra(BREAKOUT_START_TAG, start);
                    i.putExtra(BREAKOUT_END_TAG, end);
                    i.putExtra(BREAKOUT_DAY_TAG, day);
                    i.putExtra(PRESENTATION_ID, pres_id);
                    activity.startActivity(i);

            }
        }
    }

    public interface AdapterChanged{
        public void addedPresentation(int position);
        public void removedPresentation(int position);
    }
}
