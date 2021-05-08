package com.example.personalisedmobilepaindiary.chartandmapfragments;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.personalisedmobilepaindiary.databinding.PainLocationFragmentBinding;
import com.example.personalisedmobilepaindiary.room.DatabaseViewModel;
import com.example.personalisedmobilepaindiary.room.LocationFrequencyModel;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PainLocationFragment extends Fragment {
    private PainLocationFragmentBinding binding;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = PainLocationFragmentBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        DatabaseViewModel datebaseViewModel =
                ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication()).create(DatabaseViewModel.class);
        datebaseViewModel.getAllRecords().observe(getViewLifecycleOwner(), v -> {
            CompletableFuture<List<LocationFrequencyModel>> locationFrequency = datebaseViewModel.getLocationFrequency();
            locationFrequency.thenApply(f -> {
                List<PieEntry> entries = new ArrayList<>();
                for(LocationFrequencyModel locationFreq : f){
                    entries.add(new PieEntry(locationFreq.frequency, locationFreq.location));
                }
                PieDataSet set = new PieDataSet(entries, "Pain location frequency");
                ArrayList<Integer> colors = new ArrayList<Integer>();
                for (int c : ColorTemplate.VORDIPLOM_COLORS)
                    colors.add(c);
                set.setColors(colors);
                PieData data = new PieData(set);
                Description description = new Description();
                description.setText("");
                data.setDrawValues(true);
                data.setValueTextSize(12f);
                data.setHighlightEnabled(true);
                data.setValueFormatter(new PercentFormatter(binding.locationPieChart));
                binding.locationPieChart.setData(data);
                binding.locationPieChart.setEntryLabelColor(ColorTemplate.rgb("#000000"));
                binding.locationPieChart.setUsePercentValues(true);
                binding.locationPieChart.setHoleRadius(0f);
                binding.locationPieChart.setDescription(description);
                binding.locationPieChart.setTransparentCircleRadius(0f);
                binding.locationPieChart.getLegend().setOrientation(Legend.LegendOrientation.VERTICAL);
                binding.locationPieChart.getLegend().setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
                binding.locationPieChart.getLegend().setTextSize(12f);
                binding.locationPieChart.invalidate();
                return null;
            });

        });
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
