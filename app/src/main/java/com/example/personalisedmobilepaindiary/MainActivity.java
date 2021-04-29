package com.example.personalisedmobilepaindiary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.personalisedmobilepaindiary.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        auth = FirebaseAuth.getInstance();
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        if (auth.getCurrentUser() == null){
            Intent intent = new Intent(MainActivity.this, SigninupActivity.class);
            //intent.putExtra("message", "This is a message from the First Activity");
            startActivity(intent);
        }

    }
}