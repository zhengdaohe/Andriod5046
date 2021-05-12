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

import com.example.personalisedmobilepaindiary.chartandmapfragments.PainLocationFragment;
import com.example.personalisedmobilepaindiary.databinding.ActivityMainBinding;
import com.example.personalisedmobilepaindiary.firebasedatabase.MyWorker;
import com.google.firebase.auth.FirebaseAuth;

import java.time.Duration;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity
{
    public static MainActivity currentInstance = null;
    private ActivityMainBinding binding;
    private FirebaseAuth auth;
    private AppBarConfiguration appBarConfiguration;
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Check if the permissions have been granted.
        Boolean a = true;
        for (int i : grantResults)
        {
            if (i != PackageManager.PERMISSION_GRANTED)
            {
                a = false;
            }
        }
        // If there is some permission not granted, request until granted.
        if (!a)
        {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }
        // If succeed, reload main activity so the functionality requiring special permissions work properly.
        else
        {
            ((NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host)).getNavController().navigate(R.id.homeFragment);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // Firebase authentication
        auth = FirebaseAuth.getInstance();
        // Check if there is authenticated user.
        if (auth.getCurrentUser() == null)
        {
            Intent oldIntent = getIntent();
            if (oldIntent != null)
            {
                //Check if main activity is launched by alarm function. If so, use the intent passed from alarm function to start login activity.
                if (oldIntent.getIntExtra("alarm", 0) == 1)
                {
                    oldIntent.setClass(MainActivity.this, SigninupActivity.class);
                    startActivity(oldIntent);
                }
            }
            // If not, use a new intent to start login page
            Intent intent = new Intent(MainActivity.this, SigninupActivity.class);
            startActivity(intent);

        }
        else
        {
            // Store current instance of main activity for work manager use
            MainActivity.currentInstance = this;
            // initialize the layout of the activity
            binding = ActivityMainBinding.inflate(getLayoutInflater());
            View view = binding.getRoot();
            setContentView(view);
            // Initialize app bar with toolbar
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
            // Check if the activity is launched by the alarm function, if so, show notification message using a Toast.
            Intent oldIntent = getIntent();
            if (oldIntent != null)
            {
                if (oldIntent.getIntExtra("alarm", 0) == 1)
                {
                    Toast toast = Toast.makeText(this, "Please remember to record your daily pain \n ignore this if entered!!", Toast.LENGTH_LONG);
                    toast.show();
                }
                // If it is not alarm that launch the main activity, notify that login is successful.
                else
                {
                    Toast.makeText(this, "Login Successfully!", Toast.LENGTH_LONG).show();
                }
            }
            // Check if the daily step goal has been set before, if not, set a default value of 10000
            if (getSharedPreferences("STEP_GOAL_PREFERENCE", Context.MODE_PRIVATE).getInt("goal", -1) == -1)
            {
                SharedPreferences dailyStepGoal = getSharedPreferences("STEP_GOAL_PREFERENCE", Context.MODE_PRIVATE);
                SharedPreferences.Editor stEditor = dailyStepGoal.edit();
                stEditor.putInt("goal", 10000);
                stEditor.apply();
            }
            // Check if the application has hte permission to use location services, if not call the requesting function to get the permissions.
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            }
            // Get the preference storing alarm details, if the alarm is set, create an alarm for the next expected point of time.
            SharedPreferences alarmParam = getSharedPreferences("ALARM_PARAM", Context.MODE_PRIVATE);
            if (alarmParam.getInt("hour", -1) != -1)
            {
                Intent intent = new Intent(this, MainActivity.class);
                // Indicating that this intent is launched by an alarm.
                intent.putExtra("alarm", 1);
                AlarmManager alarmManager = (AlarmManager) getSystemService(Service.ALARM_SERVICE);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
                // Set a calendar of alarm time for today
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.set(Calendar.HOUR_OF_DAY, alarmParam.getInt("hour", -1));
                calendar.set(Calendar.MINUTE, alarmParam.getInt("minute", -1));
                calendar.set(Calendar.SECOND, 1);
                // Compare the alarm time and current time, if current time is earlier than the alarm time, set an alarm for today, if not, set an alarm for tomorrow.
                if (alarmParam.getInt("hour", -1) > Calendar.getInstance().get(Calendar.HOUR_OF_DAY) &&
                        alarmParam.getInt("minute", -1) > Calendar.getInstance().get(Calendar.MINUTE))
                {
                    alarmManager.setExact(AlarmManager.RTC, calendar.getTimeInMillis() - 120000, pendingIntent);
                } else
                {
                    alarmManager.setExact(AlarmManager.RTC, calendar.getTimeInMillis() - 120000 + 86400000, pendingIntent);
                }
            }
            // If no daily alarm time has been set, set a default value, which is 16:00, and relaunch the activity to put this into effect.
            else
            {
                SharedPreferences.Editor wtEditor = alarmParam.edit();
                wtEditor.putInt("hour", 16);
                wtEditor.putInt("minute", 0);
                wtEditor.apply();
                recreate();
            }
            // Start a new thread to handle operation of firebase database pushing.
            pushToFirebase();
        }

    }

    /*A method to clear soft input keyboard, which will be called when necessary.*/
    public void clearSoftKeyboard()
    {
        InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (keyboard.isActive())
        {
            if (keyboard.isActive() && getCurrentFocus() != null)
            {
                if (getCurrentFocus().getWindowToken() != null)
                {
                    keyboard.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }

        }
    }

    /*Method to set work manager*/
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void pushToFirebase()
    {
        // Calculate initial delay for the initial push.
        long delay = 0;
        Calendar startTime = Calendar.getInstance();
        startTime.set(Calendar.HOUR_OF_DAY, 18);
        startTime.set(Calendar.MINUTE, 14);
        if (Calendar.getInstance().getTimeInMillis() > startTime.getTimeInMillis())
        {
            delay = startTime.getTimeInMillis() + 86400000 - Calendar.getInstance().getTimeInMillis();
        } else
        {
            delay = startTime.getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
        }
        Log.e("a", "delay: " + delay);
        long finalDelay = delay;
        // Create a daily work with the calculated initial delay.
        PeriodicWorkRequest saveRequest =
                new PeriodicWorkRequest.Builder(MyWorker.class, 1, TimeUnit.DAYS).setInitialDelay(Duration.ofMillis(finalDelay))
                        .build();
        Log.e("a", "PeriodicWorkRequest built");
        // Start daily pushing, if there is an old work, replace it.
        WorkManager.getInstance(MainActivity.this).enqueueUniquePeriodicWork(
                "databasePush",
                ExistingPeriodicWorkPolicy.REPLACE,
                saveRequest);
        Log.e("a", "PeriodicWorkRequest queued");
    }

}