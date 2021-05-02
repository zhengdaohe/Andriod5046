package com.example.personalisedmobilepaindiary;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.personalisedmobilepaindiary.databinding.SigninFragmentBinding;
import com.google.android.gms.tasks.OnSuccessListener;
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
            auth.signInWithEmailAndPassword(email,
                    password).addOnSuccessListener(authResult -> {
                        Intent intent = new Intent(getContext(), MainActivity.class);
                        startActivity(intent);
                    });
        });
        binding.signupButton.setOnClickListener(v -> {
            FragmentTransaction fragmentTransaction =
                    fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container_view,
                    new SignupFragment());
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
