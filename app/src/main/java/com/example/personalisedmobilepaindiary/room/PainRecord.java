package com.example.personalisedmobilepaindiary.room;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "PainRecord")
public class PainRecord {
    public static class Weather{
        @ColumnInfo(name = "temperature")
        @NonNull
        public double temperature;
        @ColumnInfo(name = "humidity")
        @NonNull
        public double humidity;
        @ColumnInfo(name = "pressure")
        @NonNull
        public double pressure;
    }
    @PrimaryKey(autoGenerate = true)
    public int rid;
    @ColumnInfo(name = "pain_intensity_level")
    @NonNull
    public String painIntensityLevel;
    @ColumnInfo(name = "pain_location")
    @NonNull
    public String painLocation;
    @ColumnInfo(name = "mood")
    @NonNull
    public String mood;
    @ColumnInfo(name = "step_goal")
    @NonNull
    public int stepGoal;
    @ColumnInfo(name = "step_taken")
    @NonNull
    public int stepTaken;
    @ColumnInfo(name = "date")
    @NonNull
    public String date;
    @ColumnInfo(name = "user_email")
    @NonNull
    public String userEmail;
    @Embedded
    public Weather weather;

    public PainRecord(int rid, @NonNull String painIntensityLevel, @NonNull String painLocation,
                      @NonNull String mood, int stepGoal, int stepTaken, @NonNull String date,
                      @NonNull String userEmail, Weather weather) {
        this.rid = rid;
        this.painIntensityLevel = painIntensityLevel;
        this.painLocation = painLocation;
        this.mood = mood;
        this.stepGoal = stepGoal;
        this.stepTaken = stepTaken;
        this.date = date;
        this.userEmail = userEmail;
        this.weather = weather;
    }
}

