package com.example.personalisedmobilepaindiary.room;

import android.app.Application;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class Repository {
    private RecordDao recordDao;
    private LiveData<List<PainRecord>> allRecords;
    public Repository(Application application){
        PainRecordDatabase db = PainRecordDatabase.getInstance(application);
        recordDao =db.getRecordDao();
        allRecords= recordDao.getAllRecords(FirebaseAuth.getInstance().getCurrentUser().getEmail());
    }
    public LiveData<List<PainRecord>> getAllRecords() {
        return allRecords;
    }
    public void insert(PainRecord painRecord){
        PainRecordDatabase.databaseExecutor.execute(new Runnable() {
            @Override
            public void run() {
                recordDao.insertRecord(painRecord);
            }
        });
    }
    public void delete(PainRecord painRecord){
        PainRecordDatabase.databaseExecutor.execute(new Runnable() {
            @Override
            public void run() {
                recordDao.deleteRecord(painRecord);
            }
        });
    }
    public void update(PainRecord painRecord){
        PainRecordDatabase.databaseExecutor.execute(new Runnable() {
            @Override
            public void run() {
                recordDao.updateRecord(painRecord);
            }
        });
    }
    public void deleteAllRecords(String email){
        PainRecordDatabase.databaseExecutor.execute(new Runnable() {
            @Override
            public void run() {
                recordDao.deleteAllRecords(email);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public CompletableFuture<PainRecord> findByDate(String date, String email) {
        return CompletableFuture.supplyAsync(new Supplier<PainRecord>() {
            @Override
            public PainRecord get() {
                return recordDao.getRecordByDate(date, email);
            }
        }, PainRecordDatabase.databaseExecutor);
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public CompletableFuture<List<PainRecord>> getAllByListAndUser(String email) {
        return CompletableFuture.supplyAsync(new Supplier<List<PainRecord>>() {
            @Override
            public List<PainRecord> get() {
                return recordDao.getAllRecordsByListAndUser(email);
            }
        }, PainRecordDatabase.databaseExecutor);
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public CompletableFuture<List<PainRecord>> getDailyPushData(String date) {
        return CompletableFuture.supplyAsync(new Supplier<List<PainRecord>>() {
            @Override
            public List<PainRecord> get() {
                return recordDao.getPushRecordByDate(date);
            }
        }, PainRecordDatabase.databaseExecutor);
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public CompletableFuture<List<LocationFrequencyModel>> getLocationFrequency(String email) {
        return CompletableFuture.supplyAsync(new Supplier<List<LocationFrequencyModel>>() {
            @Override
            public List<LocationFrequencyModel> get() {
                return recordDao.getLocationFrequency(email);
            }
        }, PainRecordDatabase.databaseExecutor);
    }
}