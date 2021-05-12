package com.example.personalisedmobilepaindiary.chartandmapfragments;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.personalisedmobilepaindiary.databinding.StepFragmentBinding;
import com.example.personalisedmobilepaindiary.room.DatabaseViewModel;
import com.example.personalisedmobilepaindiary.room.PainRecord;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.CompletableFuture;

public class StepFragment extends Fragment {
    private StepFragmentBinding binding;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = StepFragmentBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        DatabaseViewModel datebaseViewModel =
                ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication()).create(DatabaseViewModel.class);
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+10:00"));
        String date = "" + calendar.get(Calendar.DAY_OF_MONTH) + "/" + (calendar.get(Calendar.MONTH) + 1) +
                "/" + calendar.get(Calendar.YEAR);
        CompletableFuture<PainRecord> painRecordCompletableFuture = datebaseViewModel.findRecordByDate(date, FirebaseAuth.getInstance().getCurrentUser().getEmail());
        painRecordCompletableFuture.thenApply(f -> {
            List<PieEntry> entries = new ArrayList<>();
            int stepGoal = getActivity().getSharedPreferences("STEP_GOAL_PREFERENCE",
                    Context.MODE_PRIVATE).getInt("goal", -1);
            entries.add(new PieEntry(f.stepTaken, "steps taken today"));
            int remainingSteps = stepGoal - f.stepTaken;
            if(remainingSteps < 0){
                remainingSteps = 0;
            }
            entries.add(new PieEntry(remainingSteps, "remaining steps"));
            PieDataSet set = new PieDataSet(entries, "Steps taken pie chart");
            ArrayList<Integer> colors = new ArrayList<Integer>();
            for (int c : ColorTemplate.VORDIPLOM_COLORS)
                colors.add(c);
            set.setColors(colors);
            PieData data = new PieData(set);
            Description description = new Description();
            description.setText("");
            data.setValueTextSize(12f);
            binding.stepPieChart.setData(data);
            binding.stepPieChart.setEntryLabelColor(ColorTemplate.rgb("#000000"));
            binding.stepPieChart.setDescription(description);
            binding.stepPieChart.getLegend().setOrientation(Legend.LegendOrientation.VERTICAL);
            binding.stepPieChart.getLegend().setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
            binding.stepPieChart.getLegend().setTextSize(12f);
            binding.stepPieChart.invalidate();
            return null;

        });
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
