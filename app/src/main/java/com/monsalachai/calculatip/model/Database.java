package com.monsalachai.calculatip.model;

import android.arch.persistence.room.RoomDatabase;
import android.util.Log;

import com.monsalachai.calculatip.model.daos.BasicInfoDao;
import com.monsalachai.calculatip.model.daos.ParticipantDao;
import com.monsalachai.calculatip.model.entities.BasicInfo;
import com.monsalachai.calculatip.model.entities.Participant;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@android.arch.persistence.room.Database(entities = {Participant.class, BasicInfo.class}, version = 2)
public abstract class Database extends RoomDatabase {
    public abstract ParticipantDao participantDao();
    public abstract BasicInfoDao basicInfoDao();

    public BasicInfo getStoredData() {
        // There should only ever be one row of BasicInfo (as it stores single-instance data.)
        // So, try to find one, if there aren't any add one, if there are too many (panic) then
        // prune.
        List<BasicInfo> rows = basicInfoDao().getAll();
        if (rows.size() == 0) {
            // create a default row.
            BasicInfo bi = new BasicInfo();
            bi.setUid(basicInfoDao().insert(bi));
            return bi;
        }

        if (rows.size() == 1) {
            return rows.get(0);
        }

        else {
            Log.e("Database", "Too many rows in BasicInfo table!!");
            for (BasicInfo bi : rows.subList(1, rows.size()))
                basicInfoDao().remove(bi);

            return rows.get(0);
        }
    }

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
