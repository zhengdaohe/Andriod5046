package com.example.personalisedmobilepaindiary.firebasedatabase;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.personalisedmobilepaindiary.MainActivity;
import com.example.personalisedmobilepaindiary.room.DatabaseViewModel;
import com.example.personalisedmobilepaindiary.room.PainRecord;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.Map;

public class MyWorker extends Worker {
    public MyWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public Result doWork() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("DailyPainRecord");
        DatabaseViewModel datebaseViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(MainActivity.currentInstance.getApplication()).create(DatabaseViewModel.class);
        datebaseViewModel.getAllBylist().thenApply(v -> {
            myRef.setValue(v);
            Log.e("a", "pushed");
            return null;
        });

        return Result.success();
    }
}