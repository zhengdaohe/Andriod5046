package com.example.personalisedmobilepaindiary.firebasedatabase;

import android.content.Context;
import android.os.Build;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.personalisedmobilepaindiary.MainActivity;
import com.example.personalisedmobilepaindiary.room.DatabaseViewModel;
import com.example.personalisedmobilepaindiary.room.PainRecord;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*
 * Worker class to perform database pushing operation
 */
public class MyWorker extends Worker
{
    public MyWorker(@NonNull Context context, @NonNull WorkerParameters params)
    {
        super(context, params);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public Result doWork()
    {
        // Get data model from current activity
        DatabaseViewModel datebaseViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(MainActivity.currentInstance.getApplication()).create(DatabaseViewModel.class);
        Calendar calendar = Calendar.getInstance();
        // Get date of today.
        String date = "" + calendar.get(Calendar.DAY_OF_MONTH) + "/" + (calendar.get(Calendar.MONTH) + 1) +
                "/" + calendar.get(Calendar.YEAR);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("DailyPainRecord");
        datebaseViewModel.getDailyPushRecord(date).thenApply(v ->
        {
            // Push today's new record into Firebase database.
            String date1 = "" + calendar.get(Calendar.DAY_OF_MONTH) + "-" + (calendar.get(Calendar.MONTH) + 1) +
                    "-" + calendar.get(Calendar.YEAR);
            myRef.child(date1).setValue(v);

            Log.e("database push", "data has been pushed");

            return null;
        });

        return Result.success();
    }


}