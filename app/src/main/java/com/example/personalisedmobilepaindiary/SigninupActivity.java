package com.example.personalisedmobilepaindiary;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.personalisedmobilepaindiary.databinding.ActivitySigninupBinding;
import com.google.firebase.auth.FirebaseAuth;

public class SigninupActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private ActivitySigninupBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        auth = FirebaseAuth.getInstance();
        super.onCreate(savedInstanceState);
        binding = ActivitySigninupBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        replaceFragment(new SigninFragment());
    }
    public void replaceFragment(Fragment nextFragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction =
                fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container_view,
                nextFragment);
        fragmentTransaction.commit();
    }
}