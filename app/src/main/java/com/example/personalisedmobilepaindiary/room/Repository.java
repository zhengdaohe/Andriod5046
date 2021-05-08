package com.example.personalisedmobilepaindiary.room;

import android.app.Application;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class Repository {
    private RecordDao recordDao;
    private LiveData<List<PainRecord>> allRecords;
    public Repository(Application application){
        PainRecordDatabase db = PainRecordDatabase.getInstance(application);
        recordDao =db.getRecordDao();
        allRecords= recordDao.getAllRecords();
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
    public void deleteAllRecords(){
        PainRecordDatabase.databaseExecutor.execute(new Runnable() {
            @Override
            public void run() {
                recordDao.deleteAllRecords();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public CompletableFuture<PainRecord> findByDate(String date) {
        return CompletableFuture.supplyAsync(new Supplier<PainRecord>() {
            @Override
            public PainRecord get() {
                return recordDao.getRecordByDate(date);
            }
        }, PainRecordDatabase.databaseExecutor);
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public CompletableFuture<List<PainRecord>> getAllByList() {
        return CompletableFuture.supplyAsync(new Supplier<List<PainRecord>>() {
            @Override
            public List<PainRecord> get() {
                return recordDao.getAllRecordsByList();
            }
        }, PainRecordDatabase.databaseExecutor);
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public CompletableFuture<List<LocationFrequencyModel>> getLocationFrequency() {
        return CompletableFuture.supplyAsync(new Supplier<List<LocationFrequencyModel>>() {
            @Override
            public List<LocationFrequencyModel> get() {
                return recordDao.getLocationFrequency();
            }
        }, PainRecordDatabase.databaseExecutor);
    }
}