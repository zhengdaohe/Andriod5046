package com.example.personalisedmobilepaindiary;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.work.Configuration;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.personalisedmobilepaindiary.databinding.ActivityMainBinding;
import com.example.personalisedmobilepaindiary.firebasedatabase.MyWorker;
import com.example.personalisedmobilepaindiary.room.DatabaseViewModel;
import com.example.personalisedmobilepaindiary.room.PainRecord;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity{
    public static MainActivity currentInstance = null;
    private ActivityMainBinding binding;
    private FirebaseAuth auth;
    private AppBarConfiguration appBarConfiguration;

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Boolean a = true;
        for (int i : grantResults) {
            if (i != PackageManager.PERMISSION_GRANTED) {
                a = false;
            }
        }
        if (!a) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }
        else {
            recreate();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            Intent oldIntent = getIntent();
            if (oldIntent != null){
                if (oldIntent.getIntExtra("alarm", 0) == 1){
                    oldIntent.setClass(MainActivity.this, SigninupActivity.class);
                    startActivity(oldIntent);
                }
            }
            Intent intent = new Intent(MainActivity.this, SigninupActivity.class);
            startActivity(intent);
        } else {
            MainActivity.currentInstance = this;
            binding = ActivityMainBinding.inflate(getLayoutInflater());
            View view = binding.getRoot();
            setContentView(view);
            setSupportActionBar(binding.toolbar);
            appBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.homeFragment,
                    R.id.recordFragment,
                    R.id.reportFragment,
                    R.id.entryFragment).setOpenableLayout(binding.mainLayout)
                    .build();

            FragmentManager fragmentManager = getSupportFragmentManager();
            NavHostFragment navHostFragment = (NavHostFragment)
                    fragmentManager.findFragmentById(R.id.nav_host);
            NavController navController = navHostFragment.getNavController();
            NavigationUI.setupWithNavController(binding.navView, navController);
            NavigationUI.setupWithNavController(binding.toolbar, navController,
                    appBarConfiguration);
            Toast.makeText(this,"Login Successfully!", Toast.LENGTH_LONG).show();
            Intent oldIntent = getIntent();
            if (oldIntent != null){
                if (oldIntent.getIntExtra("alarm", 0) == 1){
                    Toast toast = Toast.makeText(this,"Please remember to record your daily pain!!", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
            if (getSharedPreferences("STEP_GOAL_PREFERENCE", Context.MODE_PRIVATE).getInt("goal",-1) == -1){
                SharedPreferences dailyStepGoal = getSharedPreferences("STEP_GOAL_PREFERENCE", Context.MODE_PRIVATE);
                SharedPreferences.Editor stEditor = dailyStepGoal.edit();
                stEditor.putInt("goal", 10000);
                stEditor.apply();
            }
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            }
            SharedPreferences alarmParam = getSharedPreferences("ALARM_PARAM", Context.MODE_PRIVATE);
            if (alarmParam.getInt("hour", -1) != -1){
                Intent intent = new Intent(this,MainActivity.class);
                intent.putExtra("alarm",1);
                AlarmManager alarmManager = (AlarmManager) getSystemService(Service.ALARM_SERVICE);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.set(Calendar.HOUR_OF_DAY, alarmParam.getInt("hour", -1));
                calendar.set(Calendar.MINUTE, alarmParam.getInt("minute", -1));
                calendar.set(Calendar.SECOND,1);
                if (alarmParam.getInt("hour", -1) > Calendar.getInstance().get(Calendar.HOUR_OF_DAY) &&
                        alarmParam.getInt("minute", -1) > Calendar.getInstance().get(Calendar.MINUTE)){
                    alarmManager.setExact(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);
                }
                else {
                    alarmManager.setExact(AlarmManager.RTC, calendar.getTimeInMillis() + 86400000, pendingIntent);
                }
            }
            else {
                SharedPreferences.Editor wtEditor = alarmParam.edit();
                wtEditor.putInt("hour", 16);
                wtEditor.putInt("minute", 0);
                wtEditor.apply();
                recreate();
            }
            Thread thread=new Thread(new Runnable() {
                @Override
                public void run() {
                    pushToFirebase();
                }
            });

            thread.start();
        }

    }
    public void clearSoftKeyboard(){
        InputMethodManager keyboard = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        if (keyboard.isActive()) {
            if(keyboard.isActive()&&getCurrentFocus()!=null){
                if (getCurrentFocus().getWindowToken()!=null) {
                    keyboard.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }

        }
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void pushToFirebase(){
        long delay = 0;
        Calendar startTime = Calendar.getInstance();
        startTime.set(Calendar.HOUR_OF_DAY, 3);
        startTime.set(Calendar.MINUTE, 0);
        if (Calendar.getInstance().getTimeInMillis() > startTime.getTimeInMillis()){
            delay = startTime.getTimeInMillis() + 86400000  - Calendar.getInstance().getTimeInMillis();
        }
        else {
            delay = startTime.getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
        }
        Log.e("a", "delay: " + delay);
        long finalDelay = delay;
//        if (WorkManager.getInstance(this).enqueue(new OneTimeWorkRequest.Builder(MyWorker.class).setInitialDelay(Duration.ofMillis(finalDelay)).build()).getResult().isDone()){
//            Log.e("a", "single queued");
//        }
//        else {
//            Log.e("a", "single failed queue or exist ");
//        }
        PeriodicWorkRequest saveRequest =
                new PeriodicWorkRequest.Builder(MyWorker.class, 1, TimeUnit.DAYS).setInitialDelay(Duration.ofMillis(finalDelay))
                        .build();
        Log.e("a", "builded");
        if (WorkManager.getInstance(MainActivity.this).enqueueUniquePeriodicWork(
                "databasePush",
                ExistingPeriodicWorkPolicy.REPLACE,
                saveRequest).getResult().isDone()){
            Log.e("a", "queued");
        }
        else {
            Log.e("a", "failed queue or exist ");
        }
    }

}