package com.monsalachai.calculatip.model.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Participant {
    @PrimaryKey(autoGenerate = true)
    private long uid;

    @ColumnInfo
    private String name;

    @ColumnInfo
    private float portion;

    @ColumnInfo
    private boolean round;

    @ColumnInfo
    private float tipPercentage;

    @ColumnInfo
    private int position;


    public Participant(long uid, String name, float portion, boolean round, float tipPercentage) {
        this.uid = uid;
        this.name = name;
        this.portion = portion;
        this.round = round;
        this.tipPercentage = tipPercentage;
    }

    public Participant() {
        name = "";
        portion = 0;
        round = false;
        tipPercentage = 0;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getPortion() {
        return portion;
    }

    public void setPortion(float portion) {
        this.portion = portion;
    }

    public boolean isRound() {
        return round;
    }

    public void setRound(boolean round) {
        this.round = round;
    }

    public float getTipPercentage() {
        return tipPercentage;
    }

    public void setTipPercentage(float tipPercentage) {
        this.tipPercentage = tipPercentage;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

}
