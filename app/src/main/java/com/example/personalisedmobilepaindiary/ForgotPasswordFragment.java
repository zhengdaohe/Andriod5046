package com.example.personalisedmobilepaindiary;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.personalisedmobilepaindiary.databinding.ForgotPasswordFragmentBinding;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordFragment extends Fragment
{
    private ForgotPasswordFragmentBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        binding = ForgotPasswordFragmentBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        // When a user click reset, send an email for resetting, and if input box is empty, show an error message.
        binding.resetButton.setOnClickListener(v ->
        {
            if (binding.email.getText().toString().equals(""))
            {
                binding.errorMessage.setTextSize(14);
                binding.errorMessage.setText("ERROR: You must enter an email!!");
            } else
            {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.sendPasswordResetEmail(binding.email.getText().toString())
                        .addOnCompleteListener(task ->
                        {
                            if (task.isSuccessful())
                            {
                                // After sending email, return to the sign in page.
                                Toast toast = Toast.makeText(requireActivity(), "reset email has been sent", Toast.LENGTH_LONG);
                                toast.show();
                                FragmentTransaction fragmentTransaction =
                                        fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.fragment_container_view,
                                        new SigninFragment());
                                fragmentTransaction.commit();
                            }
                        })
                        .addOnFailureListener(task ->
                        {
                            binding.errorMessage.setTextSize(14);
                            binding.errorMessage.setText("ERROR: Reset failed, email does not exist or network issue!");
                        });
            }
        });
        // Go back to sign in page if the user click sign in button.
        binding.signinButton.setOnClickListener(v ->
        {
            FragmentTransaction fragmentTransaction =
                    fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container_view,
                    new SigninFragment());
            fragmentTransaction.commit();
        });
        return view;
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        binding = null;
    }
}