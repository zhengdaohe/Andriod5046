package com.example.personalisedmobilepaindiary.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.HashMap;
import java.util.List;

@Dao
public interface RecordDao {
    @Insert
    void insertRecord(PainRecord painRecord);

    @Delete
    void deleteRecord(PainRecord painRecord);

    @Update
    void updateRecord(PainRecord painRecord);

    @Query("SELECT * FROM PainRecord ORDER BY rid ASC")
    LiveData<List<PainRecord>> getAllRecords();

    @Query("SELECT * FROM PainRecord ORDER BY rid ASC")
    List<PainRecord> getAllRecordsByList();

    @Query("DELETE FROM PainRecord")
    void deleteAllRecords();

    @Query("SELECT * FROM PainRecord WHERE date = :date LIMIT 1")
    PainRecord getRecordByDate(String date);

    @Query("SELECT pain_location as location,COUNT(*) as frequency FROM PainRecord GROUP BY pain_location")
    List<LocationFrequencyModel> getLocationFrequency();
}