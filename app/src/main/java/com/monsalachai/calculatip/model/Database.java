package com.monsalachai.calculatip.model;

import android.arch.persistence.room.RoomDatabase;
import android.util.Log;

import com.monsalachai.calculatip.model.daos.ParticipantDao;
import com.monsalachai.calculatip.model.entities.Participant;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@android.arch.persistence.room.Database(entities = {Participant.class}, version = 1)
public abstract class Database extends RoomDatabase {
    public abstract ParticipantDao participantDao();

    public List<Participant> getStoredParticipants() {
        // load all stored participants
        // ensure ordered correctly
        // return.

        List<Participant> participants = participantDao().getAll();
        Collections.sort(participants, new Comparator<Participant>() {
            @Override
            public int compare(Participant o1, Participant o2) {
                return o1.getPosition() - o2.getPosition();
            }
        });

        for (Participant p: participants) {
            Log.d("Database", "Restoring participant: " + p.getName());
        }

        return participants;
    }
}
