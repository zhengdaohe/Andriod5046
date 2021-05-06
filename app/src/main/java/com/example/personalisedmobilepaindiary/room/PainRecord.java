package com.example.personalisedmobilepaindiary.room;

import android.text.SpannableString;
import android.text.SpannableStringBuilder;

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

        public Weather(double temperature, double humidity, double pressure) {
            this.temperature = temperature;
            this.humidity = humidity;
            this.pressure = pressure;
        }

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
    public int painIntensityLevel;
    @ColumnInfo(name = "pain_location")
    @NonNull
    public String painLocation;
    @ColumnInfo(name = "mood")
    @NonNull
    public String mood;
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

    public PainRecord(@NonNull int painIntensityLevel, @NonNull String painLocation,
                      @NonNull String mood, int stepTaken, @NonNull String date,
                      @NonNull String userEmail, Weather weather) {
        this.painIntensityLevel = painIntensityLevel;
        this.painLocation = painLocation;
        this.mood = mood;
        this.stepTaken = stepTaken;
        this.date = date;
        this.userEmail = userEmail;
        this.weather = weather;
    }
}

