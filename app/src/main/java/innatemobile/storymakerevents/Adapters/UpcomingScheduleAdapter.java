package innatemobile.storymakerevents.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import innatemobile.storymakerevents.Activities.BioActivity;
import innatemobile.storymakerevents.Activities.PresentationActivity;
import innatemobile.storymakerevents.Models.Breakouts;
import innatemobile.storymakerevents.Models.Presentations;
import innatemobile.storymakerevents.Models.ScheduleJoined;
import innatemobile.storymakerevents.Models.Schedules;
import innatemobile.storymakerevents.Models.Speakers;
import innatemobile.storymakerevents.Models.Spreadsheets;
import innatemobile.storymakerevents.R;
import innatemobile.storymakerevents.Utils.DatabaseHandler;
import innatemobile.storymakerevents.Utils.RequestSpreadsheets;

/**
 * Created by rphovley on 2/14/2016.
 */
public class UpcomingScheduleAdapter extends RecyclerView.Adapter<UpcomingScheduleAdapter.UpcomingScheduleCardViewHolder> {

    private static final int TYPE_FIXED_SCHEDULE = 0;
    private static final int TYPE_MY_SCHEDULE    = 1;
    private static final int TYPE_DAY_HEADER     = 3;
    private static final int TYPE_HEADER         = 4;
    private static final int TYPE_HEADER_FIRST   = 5;
    private static final int TYPE_HELP_TEXT      = 6;

    public List<ScheduleJoined> schedulesList;
    public Activity activity;
    DatabaseHandler dh;
    iUpcomingAdapter iUpcoming;

    public UpcomingScheduleAdapter(List<ScheduleJoined> schedulesList, Activity activity, Fragment f)
    {
        this.schedulesList     = schedulesList;
        schedulesList.add(0,null); //add the fixed item for the next card
        schedulesList.add(null); //add the swipe right suggestion at the end
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
        if(schedulesList.get(position)!=null && !schedulesList.get(position).schedule.isEmptyBreakout()) {

            int presentation_id = schedulesList.get(position).schedule.getPresentation_id();
            Presentations presentation = dh.getPresentation(presentation_id);
            int speaker_id = presentation.getSpeaker_id();
            Speakers speaker = dh.getSpeaker(speaker_id);
            int breakout_id =schedulesList.get(position).schedule.getBreakout_id();
            Breakouts breakout = dh.getBreakout(breakout_id);
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

        }else if(position!=0 && position!=schedulesList.size() - 1&& schedulesList.get(position)==null){ //DAY HEADER
            int breakout_id =schedulesList.get(position + 1).schedule.getBreakout_id();
            Breakouts breakout = dh.getBreakout(breakout_id);
            holder.txtDayHeader.setText(breakout.getDayOfWeek());
        }else if(getItemViewType(position)==TYPE_HEADER){ //NEXT CLASS HEADER
            Schedules nextSchedule = dh.getNextSchedule();
            Presentations nextPresentation = dh.getPresentation(nextSchedule.getPresentation_id());
            Breakouts nextBreakout = null;
            String startTime, endTime;
            if(nextPresentation.isIntensive()==1){
                List<Schedules> nextScheds = dh.getScheduleIntByPresentation(nextPresentation.getId(), nextSchedule.getSection_id());
                Breakouts startBreak = dh.getBreakout(nextScheds.get(0).getBreakout_id());
                Breakouts endBreak = dh.getBreakout(nextScheds.get(nextScheds.size()-1).getBreakout_id());
                startTime = startBreak.getStartReadable();
                endTime = endBreak.getEndReadable();
            }else{
                nextBreakout = dh.getBreakout(nextSchedule.getBreakout_id());
                startTime = nextBreakout.getStartReadable();
                endTime = nextBreakout.getEndReadable();
            }
            Speakers nextSpeaker = dh.getSpeaker(nextPresentation.getSpeaker_id());


            String title = "";
            title = nextPresentation.getTitle();
            if(title.length()>20){
                title = title.substring(0,20) + "...";
            }
            String description = "";
            description = nextPresentation.getDescription();
            if(description!=null && description.length()>150){
                description = description.substring(0,150) + "...";
            }
            String location = "";
            if(nextSchedule.getLocation()!=null) {
                location = nextSchedule.getLocation();
            }
            String timeLocation = startTime + "-" + endTime + " " + location;
            holder.txtNextTitle.setText(title);
            if(nextSpeaker!=null) {
                holder.txtNextSpeaker.setText(nextSpeaker.getName());
            }
            holder.txtNextDescription.setText(description);
            holder.txtNextTime.setText(timeLocation);
        }else if(getItemViewType(position)==TYPE_HEADER_FIRST){

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
            case TYPE_DAY_HEADER:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.day_header, parent, false);
                break;
            case TYPE_HEADER:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fixed_home_recyclerview_content, parent, false);
                break;
            case TYPE_HEADER_FIRST:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_home_first, parent, false);
                break;
            case TYPE_HELP_TEXT:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.swipe_right_help, parent, false);
                break;
        }
        return new UpcomingScheduleCardViewHolder(itemView);
    }

    @Override
    public int getItemViewType(int position) {
        int viewType = TYPE_FIXED_SCHEDULE;
        List<Schedules> myScheduleList = null;
        Breakouts breakout = null;
        if(position!=0 && schedulesList.get(position)!=null) {
            dh = new DatabaseHandler(activity);
            int presentation_id = schedulesList.get(position).schedule.getPresentation_id();
            myScheduleList = dh.getMyScheduleByPresentation(presentation_id);
            int breakout_id =schedulesList.get(position).schedule.getBreakout_id();
            breakout = dh.getBreakout(breakout_id);
            dh.close();
        }
        if (position == 0) {
            dh = new DatabaseHandler(activity);
            int count = dh.getMyScheduleCount();
            if(count >0) {
                viewType = TYPE_HEADER;
            }else{
                viewType = TYPE_HEADER_FIRST;
            }
        }else if (position != 0 && position != schedulesList.size() - 1 && schedulesList.get(position)==null) {//New day header
            viewType = TYPE_DAY_HEADER;
        }else if(position == schedulesList.size() - 1){
            viewType = TYPE_HELP_TEXT;
        }else if (myScheduleList.get(0).isPresentation()) {
            viewType = TYPE_MY_SCHEDULE;
        }else if (!myScheduleList.get(0).isPresentation() && !schedulesList.get(position).schedule.isEmptyBreakout()) {
            viewType = TYPE_FIXED_SCHEDULE;
        }
        return viewType;
    }

    public void removeItem(int selected_id) {
        DatabaseHandler dh = new DatabaseHandler(activity);
        Presentations pres = dh.getPresentation(schedulesList.get(selected_id).schedule.getPresentation_id());
        dh.removeFromSchedule(pres.getId());
        ArrayList<HashMap<Integer, Boolean>> changed = new ArrayList<>();
        HashMap<Integer,Boolean> hash = new HashMap<>();
        if(pres.isIntensive()==1) {
            for (int i = 0; i < schedulesList.size(); i++) {
                if(schedulesList.get(i)!=null && schedulesList.get(i).schedule.getPresentation_id()==pres.getId()){
                    Breakouts breakout = dh.getBreakout(schedulesList.get(i).schedule.getBreakout_id());
                    schedulesList.remove(i);
                    int sizeBefore = schedulesList.size();
                    schedulesList = MyScheduleAdapter.isBreakoutInSchedule(schedulesList, breakout ,activity);
                    if(sizeBefore != schedulesList.size()) {//a breakout empty holder was added
                        hash.put(selected_id, true);
                    }else{
                        hash.put(selected_id, false);
                    }
                    changed.add(hash);
                }
            }
        }else {
            Breakouts breakout = dh.getBreakout(schedulesList.get(selected_id).schedule.getBreakout_id());
            schedulesList.remove(selected_id);
            int sizeBefore = schedulesList.size();
            schedulesList = MyScheduleAdapter.isBreakoutInSchedule(schedulesList, breakout ,activity);
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
                txtDayHeader, txtBreakoutName, txtFeedback, txtViewBio;
        protected TextView txtNotification, txtNextTitle, txtNextSpeaker, txtNextDescription, txtNextTime;
        protected ImageView synch, imgAddClass;
        protected View btnLayoutRemove, nextHomeCard;
        public UpcomingScheduleCardViewHolder(final View itemView) {
            super(itemView);
            txtPresentationName = (TextView) itemView.findViewById(R.id.txtPresentationTitle);
            txtSpeakerName      = (TextView) itemView.findViewById(R.id.txtSpeakerName);
            txtRoom             = (TextView) itemView.findViewById(R.id.txtRoom);
            txtTime             = (TextView) itemView.findViewById(R.id.txtTime);
            txtDayHeader        = (TextView) itemView.findViewById(R.id.txtDayHeader);
            txtBreakoutName     = (TextView) itemView.findViewById(R.id.txtBreakoutName);
            btnLayoutRemove     = itemView.findViewById(R.id.btnLayoutRemoveFromSchedule);
            txtNotification     = (TextView) itemView.findViewById(R.id.txtNotification);
            txtNextTitle        = (TextView) itemView.findViewById(R.id.txtNextTitle);
            txtNextSpeaker      = (TextView) itemView.findViewById(R.id.txtNextSpeaker);
            txtNextDescription  = (TextView) itemView.findViewById(R.id.txtNextDescription);
            txtNextTime         = (TextView) itemView.findViewById(R.id.txtNextTimeLocation);
            txtFeedback         = (TextView) itemView.findViewById(R.id.txtFeedback);
            txtViewBio          = (TextView) itemView.findViewById(R.id.txtViewBio);
            imgAddClass         = (ImageView) itemView.findViewById(R.id.imgAddClass);
            nextHomeCard        = itemView.findViewById(R.id.nextHomeCard);

            synch = (ImageView) itemView.findViewById(R.id.synch);
            if(txtFeedback!=null){
                txtFeedback.setOnClickListener(this);
            }
            if(txtViewBio!=null){
                txtViewBio.setOnClickListener(this);
            }
            if(synch!=null) {
                synch.setOnClickListener(this);
            }
            if(btnLayoutRemove!=null){
                btnLayoutRemove.setOnClickListener(this);
                btnLayoutRemove.setOnLongClickListener(this);
            }
            if(imgAddClass!=null){
                imgAddClass.setOnClickListener(this);
            }
            if(nextHomeCard!=null){
                nextHomeCard.setOnClickListener(this);
            }

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
                case R.id.btnLayoutRemoveFromSchedule:
                    switchToPresentation();
                    break;
                case R.id.imgAddClass:
                    iUpcoming.addClass();
                    break;
                case R.id.txtFeedback:
                    switchToFeedback();
                    break;
                case R.id.txtViewBio:
                    switchToBio();
                    break;
                case R.id.nextHomeCard:
                    DatabaseHandler dh3 = new DatabaseHandler(activity);
                    final Schedules sched = dh3.getNextSchedule();
                    final Breakouts breakout3 = dh3.getBreakout(sched.getBreakout_id());
                    iUpcoming.viewPresentation(breakout3, sched.getPresentation_id());
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
        public void switchToPresentation(){
            DatabaseHandler dh = new DatabaseHandler(activity);
            Intent i = new Intent(activity, PresentationActivity.class);
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
            i.putExtra(AddScheduleAdapter.PRESENTATION_ID, schedulesList.get(this.getAdapterPosition()).schedule.getPresentation_id());
            dh.close();
            activity.startActivity(i);
        }
        public void switchToFeedback(){
            DatabaseHandler dh = new DatabaseHandler(activity);
            String url = dh.getSpreadsheetLink(Spreadsheets.COURSE_SHEET);
            Schedules sched = dh.getNextSchedule();
            if(sched !=null && sched.getPresentation_id() != -1){
                url += dh.getSpreadsheetKey(Spreadsheets.COURSE_SHEET);
                Presentations pres = dh.getPresentation(sched.getPresentation_id());
                try {
                    url += URLEncoder.encode(pres.getTitle(), "UTF-8");
                }catch(UnsupportedEncodingException e){
                    Log.d("FeedbackFragment", "UnsupportedEncoding", e);
                }
            }

            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            activity.startActivity(i);
        }
        public void switchToBio(){
            Schedules sched = dh.getNextSchedule();
            Presentations pres = dh.getPresentation(sched.getPresentation_id());
            Intent i = new Intent(activity, BioActivity.class);
            i.putExtra(PresentationActivity.SPEAKER_ID, pres.getSpeaker_id());
            i.putExtra(PresentationActivity.SCHEDULE_ID, sched.getId());
            activity.startActivity(i);
        }

    }

    public interface iUpcomingAdapter{
        public void removeItem(int selected_id);
        public void notifyItemsChanged(List<HashMap<Integer, Boolean>> changed);
        public void addClass();
        public void viewPresentation(Breakouts breakout, int pres_id);
    }
}
