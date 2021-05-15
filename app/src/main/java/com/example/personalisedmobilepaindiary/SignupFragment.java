package com.example.personalisedmobilepaindiary;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.personalisedmobilepaindiary.databinding.SignupFragmentBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class SignupFragment extends Fragment
{
    private SignupFragmentBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        binding = SignupFragmentBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        binding.signupButton.setOnClickListener(v ->
        {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            String email = binding.emailText.getText().toString();
            String password = binding.password.getText().toString();
            // Show error message when user don't input all the details or password is shorter that 6 characters.
            if (email.equals("") || password.equals("") || password.length() < 6)
            {
                binding.errorMessage.setTextSize(14);
                binding.errorMessage.setText("ERROR: You must enter email and password with 6 more characters.");
            } else if (!Pattern.matches("^\\w+@\\w+\\..+$", email))
            {
                binding.errorMessage.setTextSize(14);
                binding.errorMessage.setText("ERROR: You must enter a valid email");
            } else
            {
                // If succeed, return to sign in page, if not, show error message for invalid email format.
                binding.progressBar.setVisibility(View.VISIBLE);
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(
                        t ->
                        {
                            if (t.isSuccessful())
                            {
                                ((SigninupActivity) getActivity()).replaceFragment(new SigninFragment());
                                Toast.makeText(requireContext(), "Signed up Successfully!", Toast.LENGTH_LONG).show();
                            } else
                            {
                                binding.progressBar.setVisibility(View.INVISIBLE);
                                binding.errorMessage.setTextSize(14);
                                binding.errorMessage.setText("ERROR: You must enter valid email format");
                            }
                        }
                );
            }
        });
        // Return to sign in page when sign in button clicked.
        binding.signinButton.setOnClickListener(v ->
        {
            ((SigninupActivity) getActivity()).replaceFragment(new SigninFragment());
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