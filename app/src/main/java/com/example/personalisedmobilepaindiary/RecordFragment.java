package com.example.personalisedmobilepaindiary;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.personalisedmobilepaindiary.databinding.RecordFragmentBinding;
import com.example.personalisedmobilepaindiary.room.DatabaseViewModel;
import com.example.personalisedmobilepaindiary.room.PainRecord;
import com.example.personalisedmobilepaindiary.room.PainRecordDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class RecordFragment extends Fragment {
    private RecordFragmentBinding binding;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = RecordFragmentBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        DatabaseViewModel datebaseViewModel =
                ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication()).create(DatabaseViewModel.class);



        datebaseViewModel.getAllRecords().observe(getViewLifecycleOwner(), v -> {
            if (this.isAdded()){
                PainRecordRCAdapter adapter = new PainRecordRCAdapter(v);
                binding.recordList.addItemDecoration(new
                        DividerItemDecoration(requireContext(),LinearLayoutManager.VERTICAL));
                binding.recordList.setAdapter(adapter);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireContext());
                binding.recordList.setLayoutManager(layoutManager);
            }

        });
        return view;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}