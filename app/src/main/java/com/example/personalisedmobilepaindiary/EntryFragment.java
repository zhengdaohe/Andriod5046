package com.example.personalisedmobilepaindiary;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.personalisedmobilepaindiary.databinding.EntryFragmentBinding;
import com.example.personalisedmobilepaindiary.room.DatabaseViewModel;
import com.example.personalisedmobilepaindiary.room.PainRecord;
import com.google.firebase.auth.FirebaseAuth;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
        binding.stepGoal.setHint("Your step goal is " + stepGoal + " (click to edit)(optional)");
        binding.textViewSave.setText(saveNotification);
        binding.textViewSave.setTextColor(Color.RED);
        binding.getRoot().setOnClickListener(v -> {
            ((MainActivity)requireActivity()).clearSoftKeyboard();
        });
        binding.alarmDateInput.setShowSoftInputOnFocus(false);
        binding.alarmDateInput.setOnFocusChangeListener((v,hasFocus) -> {
            if (hasFocus){
                ((MainActivity)requireActivity()).clearSoftKeyboard();
                TimePickerDialog dialog=new TimePickerDialog(requireActivity(), (view1, hourOfDay, minute) -> binding.alarmDateInput.setText(hourOfDay + ":" + minute),0,0,true);
                dialog.show();
            }
        });
        binding.alarmDateInput.setOnClickListener(v -> {
            TimePickerDialog dialog=new TimePickerDialog(requireActivity(), (view1, hourOfDay, minute) -> binding.alarmDateInput.setText(hourOfDay + ":" + minute),0,0,true);
            dialog.show();

        });
        saveNotification = "";
        initializeAction(moodList, spinnerArrayAdapter, moodArrayAdapter);
        SharedPreferences alarmParam = requireActivity().getSharedPreferences("ALARM_PARAM", Context.MODE_PRIVATE);
        binding.alarmDate.setHint(" Configure Alarm Time (Current time: " +  alarmParam.getInt("hour",-1) + ":" + alarmParam.getInt("minute",-1)+ ")(optional)" );
        binding.configButton.setOnClickListener(v -> {
            if (!binding.alarmDateInput.getText().toString().equals("")){
                Intent intent = new Intent(requireActivity(),MainActivity.class);
                intent.putExtra("alarm", 1);
                AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Service.ALARM_SERVICE);
                PendingIntent pendingIntent = PendingIntent.getActivity(requireActivity(), 0, intent, 0);
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(binding.alarmDateInput.getText().toString().split(":")[0]));
                calendar.set(Calendar.MINUTE, Integer.parseInt(binding.alarmDateInput.getText().toString().split(":")[1]));
                calendar.set(Calendar.SECOND, 1);
                SharedPreferences.Editor wtEditor = alarmParam.edit();
                wtEditor.putInt("hour", Integer.parseInt(binding.alarmDateInput.getText().toString().split(":")[0]));
                wtEditor.putInt("minute", Integer.parseInt(binding.alarmDateInput.getText().toString().split(":")[1]));
                wtEditor.apply();
                if (alarmParam.getInt("hour", -1) > Calendar.getInstance().get(Calendar.HOUR_OF_DAY) ||
                        (alarmParam.getInt("minute", -1) > Calendar.getInstance().get(Calendar.MINUTE) &&
                                alarmParam.getInt("hour", -1) == Calendar.getInstance().get(Calendar.HOUR_OF_DAY))){
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                }
                else {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() + 86400000, pendingIntent);
                }
            }

            if (!binding.stepGoalInput.getText().toString().equals("")){
                SharedPreferences dailyStepGoal = requireActivity().getSharedPreferences("STEP_GOAL_PREFERENCE", Context.MODE_PRIVATE);
                SharedPreferences.Editor stEditor = dailyStepGoal.edit();
                stEditor.putInt("goal", Integer.parseInt(binding.stepGoalInput.getText().toString()));
                stEditor.apply();
            }
            ((NavHostFragment) requireActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host)).getNavController().navigate(R.id.entryFragment);
            Toast toast = Toast.makeText(requireActivity(),"Successfully configure alarm and goal", Toast.LENGTH_LONG);
            toast.show();
        });

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
                    if (true){
                        binding.editButton.post(() -> {
                            binding.editButton.setEnabled(false);

                        });
                        binding.saveButton.post(() -> binding.saveButton.setOnClickListener(v -> {
                            binding.saveButton.setEnabled(false);
                            binding.editButton.setEnabled(true);
                            PainRecord record = new PainRecord((int)binding.painIntensityLevelSlider.getValue(),
                                    binding.painLocationSpinner.getSelectedItem().toString(),
                                    moodList.get(binding.moodSpinner.getSelectedItemPosition()).toString().substring(1),
                                    Integer.parseInt(binding.stepTakenInput.getText().toString()),
                                    "5/5/2021", FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                                    new PainRecord.Weather(18,
                                            getActivity().getSharedPreferences("WEATHER_PREFERENCE",
                                                    Context.MODE_PRIVATE).getInt("humidity", -1),
                                            getActivity().getSharedPreferences("WEATHER_PREFERENCE",
                                                    Context.MODE_PRIVATE).getInt("pressure", -1)));
                            datebaseViewModel.insert(record);
                            ((MainActivity)requireActivity()).clearSoftKeyboard();
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
                                    saveNotification = "Great! your modification has been saved.";
                                    ((MainActivity)requireActivity()).clearSoftKeyboard();
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