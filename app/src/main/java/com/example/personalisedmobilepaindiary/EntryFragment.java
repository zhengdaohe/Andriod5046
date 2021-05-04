package com.example.personalisedmobilepaindiary;

import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.personalisedmobilepaindiary.databinding.EntryFragmentBinding;
import com.example.personalisedmobilepaindiary.room.DatabaseViewModel;

import java.util.ArrayList;
import java.util.List;

public class EntryFragment extends Fragment {
    private EntryFragmentBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = EntryFragmentBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
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
        List<SpannableStringBuilder> list = new ArrayList<>();
        list.add(mood1);
        list.add(mood2);
        list.add(mood3);
        list.add(mood4);
        list.add(mood5);
        binding.stepTakenInput.setText("0");
        ArrayAdapter<SpannableStringBuilder> moodArrayAdapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_item, list);
        moodArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.moodSpinner.setAdapter(moodArrayAdapter);
        binding.moodSpinner.setOnItemSelectedListener
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
        DatabaseViewModel datebaseViewModel =
                ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication()).create(DatabaseViewModel.class);

        return view;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}