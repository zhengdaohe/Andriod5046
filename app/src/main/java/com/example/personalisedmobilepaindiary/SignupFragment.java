package com.example.personalisedmobilepaindiary;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.personalisedmobilepaindiary.databinding.SignupFragmentBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignupFragment extends Fragment {
    private SignupFragmentBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = SignupFragmentBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        binding.signupButton.setOnClickListener(v -> {
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    String email = binding.emailText.getText().toString();
                    String password = binding.password.getText().toString();
                    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(
                            t -> {
                                    if (t.isSuccessful()) {
                                        Intent intent = new Intent(getContext(), MainActivity.class);
                                        startActivity(intent);
                                    } else {
                                        //do something, e.g. give a message
                                    }
                                }
                            );
                });
        binding.signinButton.setOnClickListener(v -> {
            FragmentTransaction fragmentTransaction =
                    fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container_view,
                    new SigninFragment());
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