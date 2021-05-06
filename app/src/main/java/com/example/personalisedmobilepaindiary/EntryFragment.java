package com.example.personalisedmobilepaindiary;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.personalisedmobilepaindiary.databinding.EntryFragmentBinding;
import com.example.personalisedmobilepaindiary.room.DatabaseViewModel;
import com.example.personalisedmobilepaindiary.room.PainRecord;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

public class EntryFragment extends Fragment {
    private EntryFragmentBinding binding;
    private DatabaseViewModel datebaseViewModel;
    private static String saveNotification = "";
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = EntryFragmentBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        datebaseViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication()).create(DatabaseViewModel.class);
        ArrayAdapter<CharSequence> spinnerArrayAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.locations_array,
                android.R.layout.simple_spinner_item);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.painLocationSpinner.setAdapter(spinnerArrayAdapter);
        binding.painLocationSpinner.setOnItemSelectedListener
                (new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        Toast.makeText(parent.getContext(),"Movie selected is ",Toast.LENGTH_LONG)
                                .show();
                    }
                });
        SpannableStringBuilder mood1 = new SpannableStringBuilder().append("1very low");
        mood1.setSpan(new ImageSpan(getContext(), R.drawable._1520210505045917), 0, 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        SpannableStringBuilder mood2 = new SpannableStringBuilder().append("1low");
        mood2.setSpan(new ImageSpan(getContext(), R.drawable._1520210505045734), 0, 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        SpannableStringBuilder mood3 = new SpannableStringBuilder().append("1average");
        mood3.setSpan(new ImageSpan(getContext(), R.drawable._1520210505045750), 0, 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        SpannableStringBuilder mood4 = new SpannableStringBuilder().append("1good");
        mood4.setSpan(new ImageSpan(getContext(), R.drawable._1520210505050002), 0, 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        SpannableStringBuilder mood5 = new SpannableStringBuilder().append("1very good");
        mood5.setSpan(new ImageSpan(getContext(), R.drawable._1520210505051023), 0, 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        List<SpannableStringBuilder> moodList = new ArrayList<>();
        moodList.add(mood1);
        moodList.add(mood2);
        moodList.add(mood3);
        moodList.add(mood4);
        moodList.add(mood5);
        ArrayAdapter<SpannableStringBuilder> moodArrayAdapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_item, moodList);
        moodArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.moodSpinner.setAdapter(moodArrayAdapter);
        int stepGoal = getActivity().getSharedPreferences("STEP_GOAL_PREFERENCE",
                Context.MODE_PRIVATE).getInt("goal", -1);
        binding.stepGoal.setHint("Your step goal is " + stepGoal + " (click to edit)");
        binding.textViewSave.setText(saveNotification);
        binding.textViewSave.setTextColor(Color.RED);
        saveNotification = "";
        initializeAction(moodList, spinnerArrayAdapter, moodArrayAdapter);

        return view;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void initializeAction(List<SpannableStringBuilder> moodList, ArrayAdapter<CharSequence> spinnerArrayAdapter, ArrayAdapter<SpannableStringBuilder> moodArrayAdapter){
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+10:00"));
        String date = "" + calendar.get(Calendar.DAY_OF_MONTH) + "/" + (calendar.get(Calendar.MONTH) + 1) +
                "/" + calendar.get(Calendar.YEAR);
        CompletableFuture<PainRecord> painRecord = datebaseViewModel.findRecordByDate(date);
        painRecord.thenApply(p -> {
                    if (p == null){
                        binding.editButton.post(() -> {
                            binding.editButton.setEnabled(false);
                            binding.textViewSave.setText("record has been inserted");

                        });
                        binding.saveButton.post(() -> binding.saveButton.setOnClickListener(v -> {
                            binding.saveButton.setEnabled(false);
                            binding.editButton.setEnabled(true);
                            PainRecord record = new PainRecord((int)binding.painIntensityLevelSlider.getValue(),
                                    binding.painLocationSpinner.getSelectedItem().toString(),
                                    moodList.get(binding.moodSpinner.getSelectedItemPosition()).toString(),
                                    Integer.parseInt(binding.stepTakenInput.getText().toString()),
                                    date, FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                                    new PainRecord.Weather(getActivity().getSharedPreferences("WEATHER_PREFERENCE",
                                            Context.MODE_PRIVATE).getInt("temperature", -1),
                                            getActivity().getSharedPreferences("WEATHER_PREFERENCE",
                                                    Context.MODE_PRIVATE).getInt("humidity", -1),
                                            getActivity().getSharedPreferences("WEATHER_PREFERENCE",
                                                    Context.MODE_PRIVATE).getInt("pressure", -1)));
                            datebaseViewModel.insert(record);
                            saveNotification = "Great! your record has been added.";
                            ((NavHostFragment) requireActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host)).getNavController().navigate(R.id.entryFragment);
                        }));
                    }
                    else {
                        requireActivity().runOnUiThread(() -> {
                            binding.painIntensityLevelSlider.setEnabled(false);
                            binding.painLocationSpinner.setEnabled(false);
                            binding.stepTakenInput.setEnabled(false);
                            binding.stepGoalInput.setEnabled(false);
                            binding.moodSpinner.setEnabled(false);
                            binding.saveButton.setEnabled(false);
                            binding.editButton.setOnClickListener(v -> {
                                binding.painIntensityLevelSlider.setEnabled(true);
                                binding.painLocationSpinner.setEnabled(true);
                                binding.stepTakenInput.setEnabled(true);
                                binding.stepGoalInput.setEnabled(true);
                                binding.moodSpinner.setEnabled(true);
                                binding.saveButton.setEnabled(true);
                                binding.editButton.setEnabled(false);
                                binding.textViewSave.setText("");
                                binding.saveButton.setOnClickListener(c -> {
                                    p.mood = moodList.get(binding.moodSpinner.getSelectedItemPosition()).toString();
                                    p.painIntensityLevel = (int)binding.painIntensityLevelSlider.getValue();
                                    p.painLocation = binding.painLocationSpinner.getSelectedItem().toString();
                                    p.stepTaken = Integer.parseInt(binding.stepTakenInput.getText().toString());
                                    datebaseViewModel.update(p);
                                    if (!binding.stepGoalInput.getText().toString().equals("")){
                                        SharedPreferences dailyStepGoal = requireActivity().getSharedPreferences("STEP_GOAL_PREFERENCE", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor stEditor = dailyStepGoal.edit();
                                        stEditor.putInt("goal", Integer.parseInt(binding.stepGoalInput.getText().toString()));
                                        stEditor.apply();
                                    }
                                    saveNotification = "Great! your modification has been saved.";
                                    ((NavHostFragment) requireActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host)).getNavController().navigate(R.id.entryFragment);
                                });
                            });
                            binding.painIntensityLevelSlider.setValue(p.painIntensityLevel);
                            binding.painLocationSpinner.setSelection(spinnerArrayAdapter.getPosition(p.painLocation));
                            SpannableStringBuilder selectedMood = null;
                            for (SpannableStringBuilder index : moodList){
                                if (Pattern.matches(p.mood,index)){
                                    selectedMood = index;
                                }
                            }
                            binding.moodSpinner.setSelection(moodArrayAdapter.getPosition(selectedMood));
                            binding.stepTakenInput.setText(""+p.stepTaken);
                        });

                    }
                    return null;
                }
        );
    }
}