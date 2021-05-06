package com.example.personalisedmobilepaindiary;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.personalisedmobilepaindiary.databinding.RecyclerItemBinding;
import com.example.personalisedmobilepaindiary.room.PainRecord;

import java.util.List;

public class PainRecordRCAdapter extends RecyclerView.Adapter<PainRecordRCAdapter.ViewHolder> {
    private List<PainRecord> painRecords;
    public PainRecordRCAdapter(List<PainRecord> painRecords){
        this.painRecords = painRecords;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerItemBinding binding=
                RecyclerItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new PainRecordRCAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PainRecordRCAdapter.ViewHolder holder, int position) {
        PainRecord painRecord = painRecords.get(position);
        holder.binding.rcDate.setText(painRecord.date);
        holder.binding.rcIntensity.setText(""+painRecord.painIntensityLevel);
        holder.binding.rcLocation.setText(painRecord.painLocation);
        holder.binding.rcMood.setText(painRecord.mood);
        holder.binding.rcStepTaken.setText(""+painRecord.stepTaken);
    }

    public  void update(List<PainRecord> painRecords){
        this.painRecords = painRecords;
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return painRecords.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private RecyclerItemBinding binding;

        public ViewHolder(RecyclerItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}