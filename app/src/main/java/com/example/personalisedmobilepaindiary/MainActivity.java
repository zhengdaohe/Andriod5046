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
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;

import com.example.personalisedmobilepaindiary.databinding.ActivityMainBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private FirebaseAuth auth;
    private AppBarConfiguration appBarConfiguration;
    private LocationViewModel model;

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
        model = new ViewModelProvider(this).get(LocationViewModel.class);
        if (auth.getCurrentUser() == null) {
            Intent intent = new Intent(MainActivity.this, SigninupActivity.class);
            intent.putExtra("message", "This is a message from the First Activity");
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
        }

    }

}