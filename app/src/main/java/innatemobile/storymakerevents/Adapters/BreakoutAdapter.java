package innatemobile.storymakerevents.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import innatemobile.storymakerevents.Activities.AddScheduleActivity;
import innatemobile.storymakerevents.Activities.MainActivity;
import innatemobile.storymakerevents.Models.Breakouts;
import innatemobile.storymakerevents.Models.Speakers;
import innatemobile.storymakerevents.R;
import innatemobile.storymakerevents.Utils.AppController;

/**
 * Created by rphovley on 2/13/2016.
 */
public class BreakoutAdapter  extends RecyclerView.Adapter<BreakoutAdapter.BreakoutCardViewHolder> {

    public static int TYPE_PAGE_HEADER = 1;
    public static int TYPE_DEFAULT = 0;

    public List<Breakouts> breakoutList;
    public Activity activity;
    public static String BREAKOUT_ID_TAG = "breakout_id";
    public static String BREAKOUT_START_TAG = "start_time";
    public static String BREAKOUT_END_TAG = "end_time";
    public static String BREAKOUT_DAY_TAG = "day";
    public static final String BREAKOUT_CAME_FROM_BREAKOUT = "came_from_breakout";
    public BreakoutAdapter(List<Breakouts> breakoutList, Activity activity)
    {
        this.breakoutList = breakoutList;
        this.breakoutList.add(0,null);
        this.activity = activity;
    }

    @Override
    public int getItemCount() {
        return breakoutList.size();
    }

    @Override
    public void onBindViewHolder(BreakoutCardViewHolder holder, int position) {

        if(this.getItemViewType(position)==TYPE_PAGE_HEADER){
            holder.txtHeaderPage.setText("Add Class");
        }else if(this.getItemViewType(position)==TYPE_DEFAULT){
            Breakouts breakout = breakoutList.get(position);
            String breakoutName = breakout.getDayOfWeek()+ " " + "Breakout "+breakout.getBreakoutName();
            String breakoutTime = breakout.getStartReadable()+"-"+breakout.getEndReadable();
            holder.txtBreakoutName.setText(breakoutName);
            holder.txtBreakoutTime.setText(breakoutTime);
        }
    }
    @Override
    public int getItemViewType(int position) {
        int viewType = TYPE_DEFAULT;
        if(position==0){
            viewType = TYPE_PAGE_HEADER;
        }
        return viewType;
    }
    @Override
    public BreakoutCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_schedule_breakout_cards, parent, false);
        if(viewType == TYPE_PAGE_HEADER) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.page_header, parent, false);
        }
        return new BreakoutCardViewHolder(itemView);
    }


    public class BreakoutCardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        protected TextView txtBreakoutName, txtBreakoutTime, txtHeaderPage;
        protected View cardBreakout, help;

        public BreakoutCardViewHolder(final View itemView) {
            super(itemView);
            txtBreakoutName = (TextView) itemView.findViewById(R.id.breakoutName);
            txtBreakoutTime = (TextView) itemView.findViewById(R.id.breakoutTime);
            cardBreakout = itemView.findViewById(R.id.breakout_card);
            txtHeaderPage = (TextView) itemView.findViewById(R.id.txtPageHeader);
            help = itemView.findViewById(R.id.help);
            if(help!=null){
                help.setOnClickListener(this);
            }
            if(cardBreakout!=null) {
                cardBreakout.setOnClickListener(this);
            }
        }

        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.breakout_card:
                    Intent i = new Intent(activity, AddScheduleActivity.class);
                    int id = breakoutList.get(this.getAdapterPosition()).getId();
                    String start = breakoutList.get(this.getAdapterPosition()).getStartReadable();
                    String end = breakoutList.get(this.getAdapterPosition()).getEndReadable();
                    String day = breakoutList.get(this.getAdapterPosition()).getDayOfWeek();
                    i.putExtra(BREAKOUT_ID_TAG, id);
                    i.putExtra(BREAKOUT_START_TAG, start);
                    i.putExtra(BREAKOUT_END_TAG, end);
                    i.putExtra(BREAKOUT_DAY_TAG, day);
                    i.putExtra(BREAKOUT_CAME_FROM_BREAKOUT, true);
                    activity.startActivity(i);
                    break;
                case R.id.help:
                    AppController.switchToHelp(activity, AppController.ADD_POS);
                    break;
            }
        }
    }
}