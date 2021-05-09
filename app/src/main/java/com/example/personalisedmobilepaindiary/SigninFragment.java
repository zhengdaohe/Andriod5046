package com.example.personalisedmobilepaindiary;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.personalisedmobilepaindiary.databinding.SigninFragmentBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SigninFragment extends Fragment {
    private SigninFragmentBinding binding;
    private FirebaseAuth auth;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = SigninFragmentBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        binding.signinButton.setOnClickListener(v -> {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            String email = binding.emailText.getText().toString();
            String password = binding.password.getText().toString();
            if (email.equals("") || password.equals("")){
                binding.errorMessage.setTextSize(14);
                binding.errorMessage.setText("ERROR: You must enter email and password");
            }
            else {
                auth.signInWithEmailAndPassword(email,
                        password).addOnSuccessListener(authResult -> {
                            if(authResult.getUser() != null){
                                Intent intent = new Intent(getContext(), MainActivity.class);
                                startActivity(intent);
                            }
                }).addOnFailureListener(authResult -> {
                    binding.errorMessage.setTextSize(14);
                    binding.errorMessage.setText("ERROR: Incorrect password or email or network issue!!");
                });
            }
        });
        binding.signupButton.setOnClickListener(v -> {
            FragmentTransaction fragmentTransaction =
                    fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container_view,
                    new SignupFragment());
            fragmentTransaction.commit();
        });
        binding.forgotPassword.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        binding.forgotPassword.setOnClickListener(v -> {
            FragmentTransaction fragmentTransaction =
                    fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container_view,
                    new ForgotPasswordFragment());
            fragmentTransaction.commit();
        });
        return view;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
