package com.monsalachai.calculatip.model.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class BasicInfo {
    @PrimaryKey(autoGenerate = true)
    private long uid;

    @ColumnInfo
    private float storedPortion;

    @ColumnInfo
    private float storedTip;


    public BasicInfo() {
        storedPortion = 0f;
        storedTip = 0f;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public float getStoredPortion() {
        return storedPortion;
    }

    public void setStoredPortion(float storedValue) {
        this.storedPortion = storedValue;
    }

    public float getStoredTip() {
        return storedTip;
    }

    public void setStoredTip(float storedTip) {
        this.storedTip = storedTip;
    }
}
