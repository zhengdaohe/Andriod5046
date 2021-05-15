package com.example.personalisedmobilepaindiary.room;

import android.text.SpannableString;
import android.text.SpannableStringBuilder;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/*
 * Entity class mapping data into database
 */
@Entity(tableName = "PainRecord")
public class PainRecord
{
    @PrimaryKey(autoGenerate = true)
    public int rid;
    @ColumnInfo(name = "pain_intensity_level")
    public int painIntensityLevel;
    @ColumnInfo(name = "pain_location")
    @NonNull
    public String painLocation;
    @ColumnInfo(name = "mood")
    @NonNull
    public String mood;
    @ColumnInfo(name = "step_taken")
    public int stepTaken;
    @ColumnInfo(name = "date")
    @NonNull
    public String date;
    @ColumnInfo(name = "user_email")
    @NonNull
    public String userEmail;
    @Embedded
    public Weather weather;
    public PainRecord(int painIntensityLevel, @NonNull String painLocation,
                      @NonNull String mood, int stepTaken, @NonNull String date,
                      @NonNull String userEmail, Weather weather)
    {
        this.painIntensityLevel = painIntensityLevel;
        this.painLocation = painLocation;
        this.mood = mood;
        this.stepTaken = stepTaken;
        this.date = date;
        this.userEmail = userEmail;
        this.weather = weather;
    }

    public static class Weather
    {
        @ColumnInfo(name = "temperature")
        public double temperature;
        @ColumnInfo(name = "humidity")
        public double humidity;
        @ColumnInfo(name = "pressure")
        public double pressure;

        public Weather(double temperature, double humidity, double pressure)
        {
            this.temperature = temperature;
            this.humidity = humidity;
            this.pressure = pressure;
        }
    }
}

