package com.monsalachai.calculatip.ui;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.monsalachai.calculatip.R;
import com.monsalachai.calculatip.model.entities.Participant;

import java.util.List;

public class ParticipantAdapter extends RecyclerView.Adapter<ParticipantAdapter.ViewHolder> {
    public class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(View view) {
            super(view);
        }

        void updateReports(Participant p) {
            if (p.getPortion() != 0 && p.getTipPercentage() != 0) {
                float tip = p.getPortion() * p.getTipPercentage();
                float total = p.getPortion() + tip;
                float rounded_total = (float)Math.ceil(total);
                float rounded_tip = rounded_total - p.getPortion();
                float rounded_percentage = rounded_tip / p.getPortion();

                // apply to widgets.
                ((TextView)this.itemView.findViewById(R.id.tip_report_layout).findViewById(R.id.value)).setText(Float.toString(tip));
                ((TextView)this.itemView.findViewById(R.id.total_report_layout).findViewById(R.id.value)).setText(Float.toString(total));
                ((TextView)this.itemView.findViewById(R.id.rounded_total_report_layout).findViewById(R.id.value)).setText(Float.toString(rounded_total));
                ((TextView)this.itemView.findViewById(R.id.rounded_tip_report_layout).findViewById(R.id.value)).setText(Float.toString(rounded_tip));
                ((TextView)this.itemView.findViewById(R.id.rounded_percent_report_layout).findViewById(R.id.value)).setText(Float.toString(rounded_percentage));
            }
        }
    }

    public interface OnDataStateChangeListener {
        void onParticipantUpdate(Participant participant);
    }

    private List<Participant> participants;
    private OnDataStateChangeListener listener;

    public ParticipantAdapter(List<Participant> participants) {
        this.participants = participants;
        listener = null;
    }

    public void setEventListener(OnDataStateChangeListener listener) {
        this.listener = listener;
    }

    public void addParticipant(Participant participant) {
        participants.add(participant);
        this.notifyDataSetChanged();
        Log.d("PAdapter", "Added new participant to dataset: " + participant.getName());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_invested_party, parent, false);

        final ViewHolder vh = new ViewHolder(view);

        InvestedPartyView ipv = (InvestedPartyView) view.findViewById(R.id.invested_party);

        ipv.setEventListener(new InvestedPartyView.OnInvestedPartyStateChangeListener() {
            @Override
            public void onTipChanged(float tip) {
                Participant p = participants.get(vh.getAdapterPosition());
                p.setTipPercentage(tip);
                vh.updateReports(p);
                if (listener != null)
                    listener.onParticipantUpdate(p);
            }

            @Override
            public void onNameChanged(String name) {
                Participant p = participants.get(vh.getAdapterPosition());
                p.setName(name);
                if (listener != null)
                    listener.onParticipantUpdate(p);
            }

            @Override
            public void onPortionChanged(float portion) {
                Participant p = participants.get(vh.getAdapterPosition());
                p.setPortion(portion);
                vh.updateReports(p);
                if (listener != null)
                    listener.onParticipantUpdate(p);
            }
        });

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Participant p = participants.get(position);

        ((EditText) holder.itemView.findViewById(R.id.name_entry)).setText(p.getName());
        ((EditText) holder.itemView.findViewById(R.id.portion)).setText(Float.toString(p.getPortion()));

        // set the active radio button based on participant.getTipPercentage():
        RadioButton rbtn;
        if (p.getTipPercentage() == 0.15f) {
            rbtn = holder.itemView.findViewById(R.id.percent_fifteen);
        }
        else if (p.getTipPercentage() == 0.18f) {
            rbtn = holder.itemView.findViewById(R.id.percent_eighteen);
        }
        else if (p.getTipPercentage() == 0.2f) {
            rbtn = holder.itemView.findViewById(R.id.percent_twenty);
        }
        else {
            rbtn = holder.itemView.findViewById(R.id.percent_other);

            // Apply text value to other_entry:
            ((EditText)holder.itemView.findViewById(R.id.percent_other_entry)).setText(Float.toString(p.getTipPercentage() * 100));
        }
        rbtn.setChecked(true);

        holder.updateReports(p);
    }

    @Override
    public int getItemCount() {
        return participants.size();
    }
}
