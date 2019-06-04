package com.monsalachai.calculatip.model.daos;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.monsalachai.calculatip.model.entities.BasicInfo;

import java.util.List;

@Dao
public interface BasicInfoDao {
    @Insert
    long insert(BasicInfo bi);

    @Update
    void update(BasicInfo bi);

    @Delete
    void remove(BasicInfo bi);

    @Query("SELECT * FROM basicinfo")
    List<BasicInfo> getAll();

}
