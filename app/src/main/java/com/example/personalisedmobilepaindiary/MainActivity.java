package com.example.personalisedmobilepaindiary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

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
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.personalisedmobilepaindiary.databinding.ActivityMainBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
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
}