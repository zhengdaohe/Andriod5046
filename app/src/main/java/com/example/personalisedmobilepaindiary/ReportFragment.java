package com.example.personalisedmobilepaindiary;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.personalisedmobilepaindiary.chartandmapfragments.MapFragment;
import com.example.personalisedmobilepaindiary.chartandmapfragments.PainLocationFragment;
import com.example.personalisedmobilepaindiary.chartandmapfragments.PainWeatherFragment;
import com.example.personalisedmobilepaindiary.chartandmapfragments.StepFragment;
import com.example.personalisedmobilepaindiary.databinding.ReportFragmentBinding;

public class ReportFragment extends Fragment
{
    private ReportFragmentBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        binding = ReportFragmentBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        // Initialize with the first chart.
        getChildFragmentManager().beginTransaction().replace(R.id.chart_container_view, new PainLocationFragment()).commit();
        binding.painLocationChart.setEnabled(false);
        // For each button navigate the fragment by button.
        binding.stepChart.setOnClickListener(v ->
        {
            getChildFragmentManager().beginTransaction().replace(R.id.chart_container_view, new StepFragment()).commit();
            binding.painLocationChart.setEnabled(true);
            binding.painWeatherLocationChart.setEnabled(true);
            binding.stepChart.setEnabled(false);
            binding.map.setEnabled(true);
        });
        binding.painLocationChart.setOnClickListener(v ->
        {
            getChildFragmentManager().beginTransaction().replace(R.id.chart_container_view, new PainLocationFragment()).commit();
            binding.painLocationChart.setEnabled(false);
            binding.painWeatherLocationChart.setEnabled(true);
            binding.stepChart.setEnabled(true);
            binding.map.setEnabled(true);
        });
        binding.painWeatherLocationChart.setOnClickListener(v ->
        {
            getChildFragmentManager().beginTransaction().replace(R.id.chart_container_view, new PainWeatherFragment()).commit();
            binding.painLocationChart.setEnabled(true);
            binding.painWeatherLocationChart.setEnabled(false);
            binding.stepChart.setEnabled(true);
            binding.map.setEnabled(true);
        });
        binding.map.setOnClickListener(v ->
        {
            getChildFragmentManager().beginTransaction().replace(R.id.chart_container_view, new MapFragment()).commit();
            binding.painLocationChart.setEnabled(true);
            binding.map.setEnabled(false);
            binding.stepChart.setEnabled(true);
            binding.painWeatherLocationChart.setEnabled(true);
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