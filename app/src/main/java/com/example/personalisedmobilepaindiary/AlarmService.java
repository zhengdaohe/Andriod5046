package com.example.personalisedmobilepaindiary;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.provider.Settings;
import android.util.Log;
import android.widget.MediaController;
import android.widget.Toast;

import java.util.Calendar;

public class AlarmService extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        // A sound that notify the user of the alarm
        MediaPlayer.create(context, Settings.System.DEFAULT_NOTIFICATION_URI).start();
        // Get current alarm time using preferences
        SharedPreferences alarmParam = context.getSharedPreferences("ALARM_PARAM", Context.MODE_PRIVATE);
        if (alarmParam.getInt("hour", -1) != -1)
        {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Service.ALARM_SERVICE);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
            // Create a calendar of alarm time for today
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, alarmParam.getInt("hour", -1));
            calendar.set(Calendar.MINUTE, alarmParam.getInt("minute", -1));
            calendar.set(Calendar.SECOND, 1);
            // Set the alarm tomorrow.
            alarmManager.setExact(AlarmManager.RTC, calendar.getTimeInMillis() - 120000 + 86400000, pendingIntent);
            Log.e("alarm activated", "activate time: tomorrow");
        Toast.makeText(context,"Please remember to enter your daily pain record!!", Toast.LENGTH_LONG).show();
    }
}}
