package com.example.personalisedmobilepaindiary.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {PainRecord.class}, version = 1, exportSchema =false)
public abstract class PainRecordDatabase extends RoomDatabase {
    public abstract RecordDao getRecordDao();

    private static PainRecordDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;

    public static synchronized PainRecordDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            INSTANCE
                    = Room.databaseBuilder(context.getApplicationContext(),
                    PainRecordDatabase.class, "PainRecordDatabase")
                    .build();
        }
        return INSTANCE;
    }
    public static final ExecutorService databaseExecutor
            = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
}