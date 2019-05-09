package com.monsalachai.calculatip.model.daos;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.monsalachai.calculatip.model.entities.Participant;

import java.util.List;

@Dao
public interface ParticipantDao {
    @Insert
    long insert(Participant participant);

    @Delete
    void remove(Participant participant);

    @Update
    void update(Participant participant);

    @Query("SELECT * FROM participant")
    List<Participant> getAll();

    @Query("SELECT * FROM participant WHERE uid IS :id")
    Participant getById(long id);

    @Query("SELECT * FROM participant WHERE name LIKE :name LIMIT 1")
    Participant getByName(String name);

}
