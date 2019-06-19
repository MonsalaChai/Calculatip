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

import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ParticipantAdapter extends RecyclerView.Adapter<ParticipantAdapter.ViewHolder> {
    public class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(View view) {
            super(view);
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

    public List<Participant> getDataSet() {
        return participants;
    }

    public void clear() {
        participants = new ArrayList<>();
        notifyDataSetChanged();
    }

    public float getTotalContributions ()
    {
        float sumCont = 0;

        for (Participant subjectA: participants)
        {
            sumCont = sumCont + subjectA.getPortion();
        }

        return sumCont;
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

        ((EditText) holder.itemView.findViewById(R.id.portion)).setText(String.format("%.2f", p.getPortion()));

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

        // do the report fields need updating too?

    }

    @Override
    public int getItemCount() {
        return participants.size();
    }

}
