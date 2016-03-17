package innatemobile.storymakerevents.Adapters;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
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
import innatemobile.storymakerevents.Models.Breakouts;
import innatemobile.storymakerevents.Models.Presentations;
import innatemobile.storymakerevents.Models.ScheduleJoined;
import innatemobile.storymakerevents.Models.Schedules;
import innatemobile.storymakerevents.Models.Speakers;
import innatemobile.storymakerevents.R;
import innatemobile.storymakerevents.Utils.AppController;
import innatemobile.storymakerevents.Utils.DatabaseHandler;
import innatemobile.storymakerevents.Utils.RequestSpreadsheets;

/**
 * Created by rphovley on 2/14/2016.
 */
public class MyScheduleAdapter extends RecyclerView.Adapter<MyScheduleAdapter.MyScheduleCardViewHolder> {

    private static final int TYPE_FIXED_SCHEDULE = 0;
    private static final int TYPE_MY_SCHEDULE    = 1;
    private static final int TYPE_EMPTY_SCHEDULE = 2;
    private static final int TYPE_DAY_HEADER     = 3;
    private static final int TYPE_PAGE_HEADER    = 4;

    public List<ScheduleJoined> schedulesList;
    public Activity activity;
    int selectedIndex = -1;
    DatabaseHandler dh;
    iUpcomingAdapter iUpcoming;

    public MyScheduleAdapter(List<ScheduleJoined> schedulesList, Activity activity, Fragment f, int selectedIndex)
    {
        this.schedulesList     = schedulesList;
        this.activity          = activity;
        this.iUpcoming = (iUpcomingAdapter) f;
        this.selectedIndex = selectedIndex;
    }

    @Override
    public int getItemCount() {
        return schedulesList.size();
    }

    @Override
    public void onBindViewHolder(MyScheduleCardViewHolder holder, int position) {
        int test = holder.getItemViewType();
        dh = new DatabaseHandler(activity);
        AppController.logTimes("ON BIND VIEWHOLDER");
        if(schedulesList.get(position)!=null && !schedulesList.get(position).schedule.isEmptyBreakout()) {
            Presentations presentation = schedulesList.get(position).presentation;
            Speakers speaker = schedulesList.get(position).speakers;
            Breakouts breakout = schedulesList.get(position).breakout;
            holder.txtRoom.setText(schedulesList.get(position).schedule.getLocation());
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
            //int breakout_id = schedulesList.get(position + 1).schedule.getBreakout_id();
            Breakouts breakout = schedulesList.get(position + 1).breakout;
            holder.txtDayHeader.setText(breakout.getDayOfWeek());
        }else if(schedulesList.get(position)!=null && schedulesList.get(position).schedule.isEmptyBreakout()){
            holder.txtBreakoutName.setText("Breakout " + schedulesList.get(position).schedule.getBreakout_id());
            dh = new DatabaseHandler(activity);
            Breakouts breakout = schedulesList.get(position).breakout;
            holder.txtEmptyTime.setText(breakout.getStartReadable() + "-" + breakout.getEndReadable());
        }
        if(selectedIndex == position && AppController.firstTimeFlash){
            highlightSelected(holder);
        }
        dh.close();
    }
    @Override
    public MyScheduleCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
        return new MyScheduleCardViewHolder(itemView);
    }

    @Override
    public int getItemViewType(int position) {
        AppController.logTimes("GET ITEMVIEW TYPE");
        int viewType = TYPE_FIXED_SCHEDULE;
        if(position==0){
            viewType = TYPE_PAGE_HEADER;
        }else if(schedulesList.get(position)==null) {//New day header
            viewType = TYPE_DAY_HEADER;
        }else if(schedulesList.get(position) != null && schedulesList.get(position).schedule.isEmptyBreakout()){
            viewType = TYPE_EMPTY_SCHEDULE;
        }else if (schedulesList.get(position) != null && schedulesList.get(position).schedule.isPresentation()) {//My Schedule Items
            viewType = TYPE_MY_SCHEDULE;
        }else if (schedulesList.get(position) != null && !schedulesList.get(position).schedule.isPresentation() && !schedulesList.get(position).schedule.isEmptyBreakout()) { //Empty Items
            viewType = TYPE_FIXED_SCHEDULE;
        }
        return viewType;
    }

    public void removeItem(int selected_id) {
        DatabaseHandler dh = new DatabaseHandler(activity);
        Presentations pres = schedulesList.get(selected_id).presentation;
        dh.removeFromSchedule(pres.getId());
        ArrayList<HashMap<Integer, Boolean>> changed = new ArrayList<>();
        HashMap<Integer,Boolean> hash = new HashMap<>();
        if(pres.isIntensive()==1) {
            for (int i = 0; i < schedulesList.size(); i++) {
                if(schedulesList.get(i)!=null && schedulesList.get(i).schedule.getPresentation_id()==pres.getId()){
                    Breakouts breakout = schedulesList.get(i).breakout;
                    schedulesList.remove(i);
                    int sizeBefore = schedulesList.size();
                    schedulesList = isBreakoutInSchedule(schedulesList, breakout, activity);
                    if(sizeBefore != schedulesList.size()) {//a breakout empty holder was added
                        hash.put(selected_id, true);
                    }else{
                        hash.put(selected_id, false);
                    }
                    changed.add(hash);
                }
            }
        }else {
            Breakouts breakout = schedulesList.get(selected_id).breakout;
            schedulesList.remove(selected_id);
            int sizeBefore = schedulesList.size();
            schedulesList = isBreakoutInSchedule(schedulesList, breakout, activity);
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

    public class MyScheduleCardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        protected TextView txtPresentationName, txtSpeakerName, txtRoom, txtTime,
                txtDayHeader, txtBreakoutName, txtEmptyTime, btnAddEmpty;
        protected ImageView synch, help;
        protected View btnLayoutRemove, fixedScheduleLayout;
        protected TextView txtPageHeader;

        public MyScheduleCardViewHolder(final View itemView) {
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
            fixedScheduleLayout = itemView.findViewById(R.id.fixedScheduleLayout);
            txtPageHeader       = (TextView) itemView.findViewById(R.id.txtPageHeader);
            synch = (ImageView) itemView.findViewById(R.id.synch);
            help = (ImageView) itemView.findViewById(R.id.help);
            if(synch!=null) {
                synch.setOnClickListener(this);
            }
            if(help!=null){
                help.setOnClickListener(this);
            }
            if(btnAddEmpty!=null){
                btnAddEmpty.setOnClickListener(this);
            }
            if(btnLayoutRemove!=null){
                btnLayoutRemove.setOnClickListener(this);
                btnLayoutRemove.setOnLongClickListener(this);
            }
            if(fixedScheduleLayout!=null){
                fixedScheduleLayout.setOnClickListener(this);
            }
            AppController.logTimes("CARD VIEW HOLDER");

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
                case R.id.help:
                    AppController.switchToHelp(activity, AppController.SCHEDULE_POS);
                    break;
                case R.id.btnAddEmpty:
                    DatabaseHandler dh = new DatabaseHandler(activity);
                    Intent i = new Intent(activity, AddScheduleActivity.class);
                    int id = schedulesList.get(this.getAdapterPosition()).schedule.getBreakout_id();
                    Breakouts breakout = schedulesList.get(this.getAdapterPosition()).breakout;
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
                    AppController.switchToPresentation(activity, schedulesList, this.getAdapterPosition());
                    break;
                case R.id.fixedScheduleLayout:
                    AppController.switchToPresentation(activity, schedulesList, this.getAdapterPosition());
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

    public void highlightSelected(final MyScheduleCardViewHolder holder){
        int colorFrom = ContextCompat.getColor(activity, R.color.amber_400);
        int colorTo = ContextCompat.getColor(activity, R.color.white);
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(1500); // milliseconds
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                //textView.setBackgroundColor((int) animator.getAnimatedValue());
                if (holder.btnLayoutRemove != null) {
                    Snackbar.make(holder.btnLayoutRemove, "Added to Schedule", Snackbar.LENGTH_SHORT).show();
                    holder.btnLayoutRemove.setBackgroundColor((Integer) animator.getAnimatedValue());

                }
            }

        });
        colorAnimation.start();
        AppController.firstTimeFlash = false;
    }


    public static List<ScheduleJoined> isBreakoutInSchedule(List<ScheduleJoined> mySchedule, Breakouts breakout, Context c){
        int index = 0;
        Boolean isBreakoutInSchedule = false;
        Date mySchedTime = new Date(0);
        //LinkedHashMapIndex<String, Schedules> tests = dh.getAllMyScheduleHash();
        AppController.logTimes("BEFORE INDEX TEST");

        while (!isBreakoutInSchedule && mySchedTime.getTime() <= breakout.getDateAndStartTime().getTime()){ //while we are still looking for the time
            //gets the time for the schedule item currently being checked
            if(mySchedule.get(index)!=null) {
                Breakouts mySchedBreakout = mySchedule.get(index).breakout;
                mySchedTime.setTime(mySchedBreakout.getDateAndStartTime().getTime());
                Date test = breakout.getDateAndStartTime();
                Date test2 = mySchedTime;
                if (mySchedule.get(index).schedule.isPresentation() && test.getTime() == test2.getTime()) {
                    isBreakoutInSchedule = true;
                }
            }
            index++;
        }

        AppController.logTimes("AFTER INDEX TEST");
        if(!isBreakoutInSchedule){
            Schedules schedule = new Schedules();
            schedule.setId(1000 + index - 1);
            schedule.setIsPresentation(false);
            schedule.setBreakout_id(breakout.getId());
            schedule.setIsEmptyBreakout(true);
            ScheduleJoined scheduleJoined = new ScheduleJoined(schedule, breakout, null, null);
            mySchedule.add(index-1, scheduleJoined);
        }


        return mySchedule;
    }
    public interface iUpcomingAdapter{
        void removeItem(int selected_id);
        void notifyItemsChanged(List<HashMap<Integer, Boolean>> changed);
    }
}
