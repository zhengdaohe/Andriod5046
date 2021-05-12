package com.example.personalisedmobilepaindiary.room;

import android.app.Application;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DatabaseViewModel extends AndroidViewModel{
    private Repository repository;
    private LiveData<List<PainRecord>> allRecords;
    public DatabaseViewModel (Application application) {
    super(application);
        repository = new Repository(application);
        allRecords = repository.getAllRecords();
    }
    public LiveData<List<PainRecord>> getAllRecords() {
        return allRecords;
    }
    public void insert(PainRecord painRecord) {
        repository.insert(painRecord);
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public CompletableFuture<List<PainRecord>> getAllBylistAndUser(String email) {
        return repository.getAllByListAndUser(email);
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public CompletableFuture<List<PainRecord>> getDailyPushRecord(String date) {
        return repository.getDailyPushData(date);
    }
    public void update(PainRecord painRecord) {
        repository.update(painRecord);
    }
    public void delete(PainRecord painRecord) {
        repository.delete(painRecord);
    }
    public void deleteAllRecords(String email) {
        repository.deleteAllRecords(email);
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public CompletableFuture<PainRecord> findRecordByDate(String date, String email) {
        return repository.findByDate(date, email);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public CompletableFuture<List<LocationFrequencyModel>> getLocationFrequency(String email){
        return repository.getLocationFrequency(email);
    }
}
