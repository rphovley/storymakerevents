package innatemobile.storymakerevents.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import innatemobile.storymakerevents.Models.Speakers;
import innatemobile.storymakerevents.R;

/**
 * Created by rphovley on 1/23/2016.
 */
public class SpeakerAdapter extends RecyclerView.Adapter<SpeakerAdapter.SpeakerCardViewHolder> {

    private List<Speakers> speakerListList;

    public SpeakerAdapter(List<Speakers> speakerListList) {
        this.speakerListList = speakerListList;
    }

    @Override
    public int getItemCount() {
        return speakerListList.size();
    }

    @Override
    public void onBindViewHolder(SpeakerCardViewHolder holder, int position) {
        Speakers speaker = speakerListList.get(position);
        holder.txtSpeakerName.setText(speaker.getName());
    }
    @Override
    public SpeakerCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.speaker_card_view, parent, false);
        return new SpeakerCardViewHolder(itemView);
    }


    public static class SpeakerCardViewHolder extends RecyclerView.ViewHolder{
        protected TextView txtSpeakerName;

        public SpeakerCardViewHolder(View itemView) {
            super(itemView);

            txtSpeakerName   = (TextView) itemView.findViewById(R.id.txtSpeaker);

        }
    }
}
