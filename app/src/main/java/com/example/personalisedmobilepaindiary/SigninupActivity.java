package com.example.personalisedmobilepaindiary;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.personalisedmobilepaindiary.databinding.ActivitySigninupBinding;
import com.google.firebase.auth.FirebaseAuth;

public class SigninupActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private ActivitySigninupBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySigninupBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
    }
}