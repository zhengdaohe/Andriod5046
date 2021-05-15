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
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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

public class EntryFragment extends Fragment
{
    // A temporary field for use in inter-fragment communication.
    private static String saveNotification = "";
    private EntryFragmentBinding binding;
    private DatabaseViewModel datebaseViewModel;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        binding = EntryFragmentBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        datebaseViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication()).create(DatabaseViewModel.class);
        // Create an ArrayAdapter to store locations for selection in spinner
        ArrayAdapter<CharSequence> spinnerArrayAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.locations_array,
                android.R.layout.simple_spinner_item);
        // Initial location spinner
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.painLocationSpinner.setAdapter(spinnerArrayAdapter);
        // Use spannable string to build texts with icons.
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
        // Initialize mood spinner
        ArrayAdapter<SpannableStringBuilder> moodArrayAdapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_item, moodList);
        moodArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.moodSpinner.setAdapter(moodArrayAdapter);
        // Initialize step goal input box, and show current goal in the hint.
        int stepGoal = getActivity().getSharedPreferences("STEP_GOAL_PREFERENCE",
                Context.MODE_PRIVATE).getInt("goal", -1);
        binding.stepGoal.setHint("Your step goal is " + stepGoal + " (click to edit)(optional)");
        // Show notification message from previous operation.
        binding.textViewSave.setText(saveNotification);
        binding.textViewSave.setTextColor(Color.RED);
        // A listener to clear soft input keyboard when user click none input space.
        binding.getRoot().setOnClickListener(v ->
        {
            ((MainActivity) requireActivity()).clearSoftKeyboard();
        });
        // Disable soft input for alarm setting input box
        binding.alarmDateInput.setShowSoftInputOnFocus(false);
        binding.alarmDateInput.setOnFocusChangeListener((v, hasFocus) ->
        {
            if (hasFocus)
            {
                // Display a time picker for alarm setting input box when focused
                ((MainActivity) requireActivity()).clearSoftKeyboard();
                TimePickerDialog dialog = new TimePickerDialog(requireActivity(), (view1, hourOfDay, minute) -> binding.alarmDateInput.setText(hourOfDay + ":" + minute), 0, 0, true);
                dialog.show();
            }
        });
        binding.alarmDateInput.setOnClickListener(v ->
        {
            // Display a time picker for alarm setting input box when clicked
            TimePickerDialog dialog = new TimePickerDialog(requireActivity(), (view1, hourOfDay, minute) -> binding.alarmDateInput.setText(hourOfDay + ":" + minute), 0, 0, true);
            dialog.show();

        });
        // Clear notification message.
        saveNotification = "";
        // Initialize button events and entry data for input.
        initializeAction(moodList, spinnerArrayAdapter, moodArrayAdapter);
        // Initialize alarm input box, and show current alarm time in the hint.
        SharedPreferences alarmParam = requireActivity().getSharedPreferences("ALARM_PARAM", Context.MODE_PRIVATE);
        binding.alarmDate.setHint("Daily Recording Time (Current time: " + alarmParam.getInt("hour", -1) + ":" + alarmParam.getInt("minute", -1) + ")(optional)");
        // Set step goal and alarm time on button click event.
        binding.configButton.setOnClickListener(v ->
        {
            if (!binding.alarmDateInput.getText().toString().equals(""))
            {
                // Create an intent with alarm message for use in main activity toast.
                Intent intent = new Intent(requireActivity(), AlarmService.class);
                intent.putExtra("alarm", 1);
                AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Service.ALARM_SERVICE);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(requireActivity(), 0, intent, 0);
                // Create a calendar storing today's new alarm time.
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(binding.alarmDateInput.getText().toString().split(":")[0]));
                calendar.set(Calendar.MINUTE, Integer.parseInt(binding.alarmDateInput.getText().toString().split(":")[1]));
                calendar.set(Calendar.SECOND, 1);
                // Store new alarm time into a preference
                SharedPreferences.Editor wtEditor = alarmParam.edit();
                wtEditor.putInt("hour", Integer.parseInt(binding.alarmDateInput.getText().toString().split(":")[0]));
                wtEditor.putInt("minute", Integer.parseInt(binding.alarmDateInput.getText().toString().split(":")[1]));
                wtEditor.apply();
                // Compare the alarm time and current time, if current time is 2 minutes earlier than the alarm time, set an alarm for today, if not, set an alarm for tomorrow.
                if ((calendar.getTimeInMillis()-System.currentTimeMillis()) > 120000)
                {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() - 120000, pendingIntent);
                    Log.e("alarm activated", "activate time: today");
                } else
                {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() - 120000 + 86400000, pendingIntent);
                    Log.e("alarm activated", "activate time: tomorrow");
                }
            }
            // If the user set a new goal, update it in the preference.
            if (!binding.stepGoalInput.getText().toString().equals(""))
            {
                SharedPreferences dailyStepGoal = requireActivity().getSharedPreferences("STEP_GOAL_PREFERENCE", Context.MODE_PRIVATE);
                SharedPreferences.Editor stEditor = dailyStepGoal.edit();
                stEditor.putInt("goal", Integer.parseInt(binding.stepGoalInput.getText().toString()));
                stEditor.apply();
            }
            // After configuration, refresh the current fragment and show a toast to indicate success.
            ((NavHostFragment) requireActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host)).getNavController().navigate(R.id.entryFragment);
            Toast toast = Toast.makeText(requireActivity(), "Successfully configure alarm and goal", Toast.LENGTH_LONG);
            toast.show();
        });

        return view;
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        binding = null;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void initializeAction(List<SpannableStringBuilder> moodList, ArrayAdapter<CharSequence> spinnerArrayAdapter, ArrayAdapter<SpannableStringBuilder> moodArrayAdapter)
    {
        // Find today's pain record
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+10:00"));
        String date = "" + calendar.get(Calendar.DAY_OF_MONTH) + "/" + (calendar.get(Calendar.MONTH) + 1) +
                "/" + calendar.get(Calendar.YEAR);
        CompletableFuture<PainRecord> painRecord = datebaseViewModel.findRecordByDate(date, FirebaseAuth.getInstance().getCurrentUser().getEmail());
        painRecord.thenApply(p ->
                {
                    // If there is no record today, enable save button and disable edit button
                    if (p == null)
                    {
                        binding.editButton.post(() ->
                        {
                            binding.editButton.setEnabled(false);

                        });
                        binding.saveButton.post(() -> binding.saveButton.setOnClickListener(v ->
                        {
                            // If pain level is 0, then the location must be "no pain"
                            if (((binding.painIntensityLevelSlider.getValue()) == 0 && (binding.painLocationSpinner.getSelectedItemPosition() != spinnerArrayAdapter.getCount() - 1)) ||
                                    ((binding.painIntensityLevelSlider.getValue()) != 0 && (binding.painLocationSpinner.getSelectedItemPosition() == spinnerArrayAdapter.getCount() - 1)))
                            {
                                binding.textViewSave.setText("Unmatched intensity level and pain location");
                                return;
                            }
                            // When the save button is clicked, store current entry into database.
                            binding.saveButton.setEnabled(false);
                            binding.editButton.setEnabled(true);
                            PainRecord record = new PainRecord((int) binding.painIntensityLevelSlider.getValue(),
                                    binding.painLocationSpinner.getSelectedItem().toString(),
                                    moodList.get(binding.moodSpinner.getSelectedItemPosition()).toString().substring(1),
                                    Integer.parseInt(binding.stepTakenInput.getText().toString()),
                                    date, FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                                    new PainRecord.Weather(getActivity().getSharedPreferences("WEATHER_PREFERENCE",
                                            Context.MODE_PRIVATE).getInt("temperature", -1),
                                            getActivity().getSharedPreferences("WEATHER_PREFERENCE",
                                                    Context.MODE_PRIVATE).getInt("humidity", -1),
                                            getActivity().getSharedPreferences("WEATHER_PREFERENCE",
                                                    Context.MODE_PRIVATE).getInt("pressure", -1)));
                            datebaseViewModel.insert(record);
                            // Clear soft input after button click.
                            ((MainActivity) requireActivity()).clearSoftKeyboard();
                            saveNotification = "Great! your record has been added.";
                            // Refresh entry page after inserting.
                            ((NavHostFragment) requireActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host)).getNavController().navigate(R.id.entryFragment);
                        }));
                    }
                    // If there is record for today, update the UI components.
                    else
                    {
                        requireActivity().runOnUiThread(() ->
                        {
                            // Disable the entries until user click edit button.
                            binding.painIntensityLevelSlider.setEnabled(false);
                            binding.painLocationSpinner.setEnabled(false);
                            binding.stepTakenInput.setEnabled(false);
                            binding.stepGoalInput.setEnabled(false);
                            binding.moodSpinner.setEnabled(false);
                            binding.saveButton.setEnabled(false);
                            // Show current stored value for today.
                            binding.painIntensityLevelSlider.setValue(p.painIntensityLevel);
                            binding.painLocationSpinner.setSelection(spinnerArrayAdapter.getPosition(p.painLocation));
                            SpannableStringBuilder selectedMood = null;
                            for (SpannableStringBuilder index : moodList)
                            {
                                if (Pattern.matches(p.mood, index))
                                {
                                    selectedMood = index;
                                }
                            }
                            binding.moodSpinner.setSelection(moodArrayAdapter.getPosition(selectedMood));
                            binding.stepTakenInput.setText("" + p.stepTaken);
                            binding.editButton.setOnClickListener(v ->
                            {
                                binding.painIntensityLevelSlider.setEnabled(true);
                                binding.painLocationSpinner.setEnabled(true);
                                binding.stepTakenInput.setEnabled(true);
                                binding.stepGoalInput.setEnabled(true);
                                binding.moodSpinner.setEnabled(true);
                                binding.saveButton.setEnabled(true);
                                binding.editButton.setEnabled(false);
                                // Clear notification message.
                                binding.textViewSave.setText("");
                                // When user save after editing, update today's record.
                                binding.saveButton.setOnClickListener(c ->
                                {
                                    // If pain level is 0, then the location must be "no pain"
                                    if (((binding.painIntensityLevelSlider.getValue()) == 0 && (binding.painLocationSpinner.getSelectedItemPosition() != spinnerArrayAdapter.getCount() - 1)) ||
                                            ((binding.painIntensityLevelSlider.getValue()) != 0 && (binding.painLocationSpinner.getSelectedItemPosition() == spinnerArrayAdapter.getCount() - 1)))
                                    {
                                        binding.textViewSave.setText("Unmatched intensity level and pain location");
                                        return;
                                    }
                                    p.mood = moodList.get(binding.moodSpinner.getSelectedItemPosition()).toString().substring(1);
                                    p.painIntensityLevel = (int) binding.painIntensityLevelSlider.getValue();
                                    p.painLocation = binding.painLocationSpinner.getSelectedItem().toString();
                                    p.stepTaken = Integer.parseInt(binding.stepTakenInput.getText().toString());
                                    datebaseViewModel.update(p);
                                    saveNotification = "Great! your modification has been saved.";
                                    ((MainActivity) requireActivity()).clearSoftKeyboard();
                                    ((NavHostFragment) requireActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host)).getNavController().navigate(R.id.entryFragment);
                                });
                            });

                        });

                    }
                    return null;
                }
        );
    }
}