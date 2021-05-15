package com.example.personalisedmobilepaindiary.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
 * Customized Database class
 */
@Database(entities = {PainRecord.class}, version = 1, exportSchema = false)
public abstract class PainRecordDatabase extends RoomDatabase
{
    public static final ExecutorService databaseExecutor
            = Executors.newFixedThreadPool(4);
    // A method to create singleton database instance to save system resource.
    private static PainRecordDatabase INSTANCE;

    public static synchronized PainRecordDatabase getInstance(final Context context)
    {
        if (INSTANCE == null)
        {
            INSTANCE
                    = Room.databaseBuilder(context.getApplicationContext(),
                    PainRecordDatabase.class, "PainRecordDatabase")
                    .build();
        }
        return INSTANCE;
    }

    public abstract RecordDao getRecordDao();
}