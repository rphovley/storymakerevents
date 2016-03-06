package innatemobile.storymakerevents.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.SystemClock;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import innatemobile.storymakerevents.Activities.AddScheduleActivity;
import innatemobile.storymakerevents.Activities.PresentationActivity;
import innatemobile.storymakerevents.Fragments.HomeFragment;
import innatemobile.storymakerevents.Models.Breakouts;
import innatemobile.storymakerevents.Models.Presentations;
import innatemobile.storymakerevents.Models.Schedules;
import innatemobile.storymakerevents.Models.Speakers;
import innatemobile.storymakerevents.R;
import innatemobile.storymakerevents.Utils.AppController;
import innatemobile.storymakerevents.Utils.DatabaseHandler;
import innatemobile.storymakerevents.Utils.RequestSpreadsheets;

/**
 * Created by rphovley on 2/14/2016.
 */
public class MyScheduleAdapter extends RecyclerView.Adapter<MyScheduleAdapter.UpcomingScheduleCardViewHolder> {

    private static final int TYPE_FIXED_SCHEDULE = 0;
    private static final int TYPE_MY_SCHEDULE    = 1;
    private static final int TYPE_EMPTY_SCHEDULE = 2;
    private static final int TYPE_DAY_HEADER     = 3;
    private static final int TYPE_PAGE_HEADER    = 4;

    public List<Schedules> schedulesList;
    public Activity activity;
    DatabaseHandler dh;
    iUpcomingAdapter iUpcoming;

    public MyScheduleAdapter(List<Schedules> schedulesList, Activity activity, Fragment f)
    {
        this.schedulesList     = schedulesList;
        this.schedulesList.add(0, null);
        this.activity          = activity;
        this.iUpcoming = (iUpcomingAdapter) f;
    }

    @Override
    public int getItemCount() {
        return schedulesList.size();
    }

    @Override
    public void onBindViewHolder(UpcomingScheduleCardViewHolder holder, int position) {
        int test = holder.getItemViewType();
        dh = new DatabaseHandler(activity);
        AppController.timeSinceLoad = SystemClock.currentThreadTimeMillis() - AppController.startTime;
        Log.d("ON BIND VIEWHOLDER", "Time since Main Activity Load: " + String.valueOf(AppController.timeSinceLoad + " ms"));
        if(schedulesList.get(position)!=null && !schedulesList.get(position).isEmptyBreakout()) {
            int presentation_id = schedulesList.get(position).getPresentation_id();
            Presentations presentation = dh.getPresentation(presentation_id);
            int speaker_id = presentation.getSpeaker_id();
            Speakers speaker = dh.getSpeaker(speaker_id);
            int breakout_id =schedulesList.get(position).getBreakout_id();
            Breakouts breakout = dh.getBreakout(breakout_id);
            holder.txtRoom.setText(schedulesList.get(position).getLocation());
            if(breakout!=null) {
                holder.txtTime.setText(breakout.getStartReadable() + " - " + breakout.getEndReadable());
            }
            if (speaker != null && holder.txtSpeakerName!=null) {
                String speakerName = speaker.getName();
                holder.txtSpeakerName.setText(speakerName);
            }
            if (presentation != null) {
                String presentationName = presentation.getTitle();
                holder.txtPresentationName.setText(presentationName);
            }

        }else if(position==0 && schedulesList.get(position)==null){
            holder.txtPageHeader.setText("My Schedule");
        }else if(schedulesList.get(position)==null){
            int breakout_id = schedulesList.get(position + 1).getBreakout_id();
            Breakouts breakout = dh.getBreakout(breakout_id);
            holder.txtDayHeader.setText(breakout.getDayOfWeek());
        }else if(schedulesList.get(position)!=null && schedulesList.get(position).isEmptyBreakout()){
            holder.txtBreakoutName.setText("Breakout " + schedulesList.get(position).getBreakout_id());
            dh = new DatabaseHandler(activity);
            Breakouts breakout = dh.getBreakout(schedulesList.get(position).getBreakout_id());
            holder.txtEmptyTime.setText(breakout.getStartReadable() + "-" + breakout.getEndReadable());
        }
        dh.close();
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
            case TYPE_EMPTY_SCHEDULE:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.empty_schedule_card, parent, false);
                break;
            case TYPE_DAY_HEADER:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.day_header, parent, false);
                break;
            case TYPE_PAGE_HEADER:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.page_header, parent, false);
                break;
        }
        return new UpcomingScheduleCardViewHolder(itemView);
    }

    @Override
    public int getItemViewType(int position) {
        AppController.timeSinceLoad = SystemClock.currentThreadTimeMillis() - AppController.startTime;
        Log.d("GET ITEMVIEW TYPE", "Time since Main Activity Load: " + String.valueOf(AppController.timeSinceLoad + " ms"));
        int viewType = TYPE_FIXED_SCHEDULE;
        List<Schedules> myScheduleList = null;
        if(position!=0 && schedulesList.get(position)!=null) {
            dh = new DatabaseHandler(activity);
            int presentation_id = schedulesList.get(position).getPresentation_id();
            myScheduleList = dh.getMyScheduleByPresentation(presentation_id);
            dh.close();
        }
        if(position==0){
            viewType = TYPE_PAGE_HEADER;
        }else if(schedulesList.get(position)==null) {//New day header
            viewType = TYPE_DAY_HEADER;
        }else if(schedulesList.get(position) != null && schedulesList.get(position).isEmptyBreakout()){
            viewType = TYPE_EMPTY_SCHEDULE;
        }else if (myScheduleList!=null && myScheduleList.get(0).isPresentation()) {
            viewType = TYPE_MY_SCHEDULE;
        }else if (myScheduleList!=null && !myScheduleList.get(0).isPresentation() && !schedulesList.get(position).isEmptyBreakout()) {
            viewType = TYPE_FIXED_SCHEDULE;
        }
        return viewType;
    }

    public void removeItem(int selected_id) {
        DatabaseHandler dh = new DatabaseHandler(activity);
        Presentations pres = dh.getPresentation(schedulesList.get(selected_id).getPresentation_id());
        dh.removeFromSchedule(pres.getId());
        ArrayList<HashMap<Integer, Boolean>> changed = new ArrayList<>();
        HashMap<Integer,Boolean> hash = new HashMap<>();
        if(pres.isIntensive()==1) {
            for (int i = 0; i < schedulesList.size(); i++) {
                if(schedulesList.get(i)!=null && schedulesList.get(i).getPresentation_id()==pres.getId()){
                    Breakouts breakout = dh.getBreakout(schedulesList.get(i).getBreakout_id());
                    schedulesList.remove(i);
                    int sizeBefore = schedulesList.size();
                    schedulesList = isBreakoutInSchedule(schedulesList, breakout ,activity);
                    if(sizeBefore != schedulesList.size()) {//a breakout empty holder was added
                        hash.put(selected_id, true);
                    }else{
                        hash.put(selected_id, false);
                    }
                    changed.add(hash);
                }
            }
        }else {
            Breakouts breakout = dh.getBreakout(schedulesList.get(selected_id).getBreakout_id());
            schedulesList.remove(selected_id);
            int sizeBefore = schedulesList.size();
            schedulesList = isBreakoutInSchedule(schedulesList, breakout ,activity);
            if(sizeBefore != schedulesList.size()) {//a breakout empty holder was added
                hash.put(selected_id, true);
            }else{
                hash.put(selected_id, false);
            }
            changed.add(hash);
        }
        iUpcoming.notifyItemsChanged(changed);
        dh.close();
    }

    public class UpcomingScheduleCardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        protected TextView txtPresentationName, txtSpeakerName, txtRoom, txtTime,
                txtDayHeader, txtBreakoutName, txtEmptyTime, btnAddEmpty;
        protected ImageView synch;
        protected View btnLayoutRemove;
        protected TextView txtPageHeader;

        public UpcomingScheduleCardViewHolder(final View itemView) {
            super(itemView);
            txtPresentationName = (TextView) itemView.findViewById(R.id.txtPresentationTitle);
            txtSpeakerName      = (TextView) itemView.findViewById(R.id.txtSpeakerName);
            txtRoom             = (TextView) itemView.findViewById(R.id.txtRoom);
            txtTime             = (TextView) itemView.findViewById(R.id.txtTime);
            txtDayHeader        = (TextView) itemView.findViewById(R.id.txtDayHeader);
            txtBreakoutName     = (TextView) itemView.findViewById(R.id.txtBreakoutName);
            txtEmptyTime        = (TextView) itemView.findViewById(R.id.txtEmptyTime);
            btnAddEmpty         = (TextView) itemView.findViewById(R.id.btnAddEmpty);
            btnLayoutRemove     = itemView.findViewById(R.id.btnLayoutRemoveFromSchedule);
            txtPageHeader       = (TextView) itemView.findViewById(R.id.txtPageHeader);
            synch = (ImageView) itemView.findViewById(R.id.synch);
            if(synch!=null) {
                synch.setOnClickListener(this);
            }
            if(btnAddEmpty!=null){
                btnAddEmpty.setOnClickListener(this);
            }
            if(btnLayoutRemove!=null){
                btnLayoutRemove.setOnClickListener(this);
                btnLayoutRemove.setOnLongClickListener(this);
            }
            AppController.timeSinceLoad = SystemClock.currentThreadTimeMillis() - AppController.startTime;
            Log.d("CARD VIEW HOLDER", "Time since Main Activity Load: " + String.valueOf(AppController.timeSinceLoad + " ms"));

        }

        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.synch:
                    ConnectivityManager cm =
                            (ConnectivityManager)activity.getSystemService(Context.CONNECTIVITY_SERVICE);

                    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                    boolean isConnected = activeNetwork != null &&
                            activeNetwork.isConnectedOrConnecting();
                    if(isConnected) {
                        RequestSpreadsheets requester = new RequestSpreadsheets(activity, true, false, false);
                        requester.getSpreadsheetKeys();
                    }else{
                        Snackbar.make(v, "No Connection, please try again later.", Snackbar.LENGTH_LONG).show();
                    }
                    break;
                case R.id.btnAddEmpty:
                    DatabaseHandler dh = new DatabaseHandler(activity);
                    Intent i = new Intent(activity, AddScheduleActivity.class);
                    int id = schedulesList.get(this.getAdapterPosition()).getBreakout_id();
                    Breakouts breakout = dh.getBreakout(id);
                    String start = breakout.getStartReadable();
                    String end = breakout.getEndReadable();
                    String day = breakout.getDayOfWeek();
                    i.putExtra(BreakoutAdapter.BREAKOUT_ID_TAG, id);
                    i.putExtra(BreakoutAdapter.BREAKOUT_START_TAG, start);
                    i.putExtra(BreakoutAdapter.BREAKOUT_END_TAG, end);
                    i.putExtra(BreakoutAdapter.BREAKOUT_DAY_TAG, day);
                    i.putExtra(BreakoutAdapter.BREAKOUT_CAME_FROM_BREAKOUT, false);
                    dh.close();
                    activity.startActivity(i);
                    break;
                case R.id.btnLayoutRemoveFromSchedule:
                    /*final int selected_id = this.getAdapterPosition();
                    Snackbar.make(itemView, "Remove from schedule?", Snackbar.LENGTH_LONG)
                            .setAction("Remove", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    iUpcoming.removeItem(selected_id);
                                }
                            }).show();*/
                    //go to class
                    DatabaseHandler dh2 = new DatabaseHandler(activity);
                    Intent i2 = new Intent(activity, PresentationActivity.class);
                    int id2 = schedulesList.get(this.getAdapterPosition()).getBreakout_id();
                    Breakouts breakout2 = dh2.getBreakout(id2);
                    String start2 = breakout2.getStartReadable();
                    String end2 = breakout2.getEndReadable();
                    String day2 = breakout2.getDayOfWeek();
                    i2.putExtra(BreakoutAdapter.BREAKOUT_ID_TAG, id2);
                    i2.putExtra(BreakoutAdapter.BREAKOUT_START_TAG, start2);
                    i2.putExtra(BreakoutAdapter.BREAKOUT_END_TAG, end2);
                    i2.putExtra(BreakoutAdapter.BREAKOUT_DAY_TAG, day2);
                    i2.putExtra(BreakoutAdapter.BREAKOUT_CAME_FROM_BREAKOUT, false);
                    i2.putExtra(AddScheduleAdapter.PRESENTATION_ID, schedulesList.get(this.getAdapterPosition()).getPresentation_id());
                    dh2.close();
                    activity.startActivity(i2);
                    break;
            }
        }



        @Override
        public boolean onLongClick(View v) {
            switch(v.getId()) {
                case R.id.btnLayoutRemoveFromSchedule:
                    final int selected_id = this.getAdapterPosition();
                    Snackbar.make(itemView, "Remove from schedule?", Snackbar.LENGTH_LONG)
                            .setAction("Remove", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    iUpcoming.removeItem(selected_id);
                                }
                            }).show();
                    return true;
            }
            return false;
        }
    }

    public interface iUpcomingAdapter{
        public void removeItem(int selected_id);
        public void notifyItemsChanged(List<HashMap<Integer, Boolean>> changed);
    }


    public static List<Schedules> isBreakoutInSchedule(List<Schedules> mySchedule, Breakouts testBreakout, Context c){
        int index = 0;
        int currentBreakoutId = 0;
        DatabaseHandler dh = new DatabaseHandler(c);
        Boolean isBreakoutInSchedule = false;
        Date mySchedTime = new Date(0);

        while (!isBreakoutInSchedule && mySchedTime.getTime() <= testBreakout.getDateAndStartTime().getTime()){ //while we are still looking for the time
            //gets the time for the schedule item currently being checked
            if(mySchedule.get(index)!=null) {
                currentBreakoutId = mySchedule.get(index).getBreakout_id();
                Breakouts mySchedBreakout = dh.getBreakout(currentBreakoutId);
                mySchedTime.setTime(mySchedBreakout.getDateAndStartTime().getTime());
                Date test = testBreakout.getDateAndStartTime();
                Date test2 = mySchedTime;
                if (mySchedule.get(index).isPresentation() && test.getTime() == test2.getTime()) {
                    isBreakoutInSchedule = true;
                }
            }
            index++;
        }
        if(!isBreakoutInSchedule){
            Schedules schedule = new Schedules();
            schedule.setId(1000 + index - 1);
            schedule.setIsPresentation(false);
            schedule.setBreakout_id(testBreakout.getId());
            schedule.setIsEmptyBreakout(true);
            mySchedule.add(index-1, schedule);
        }


        return mySchedule;
    }
}
