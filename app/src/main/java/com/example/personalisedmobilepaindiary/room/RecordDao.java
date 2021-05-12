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

    @Query("SELECT * FROM PainRecord WHERE user_email = :email ORDER BY rid ASC")
    LiveData<List<PainRecord>> getAllRecords(String email);

    @Query("SELECT * FROM PainRecord WHERE user_email = :email ORDER BY rid ASC")
    List<PainRecord> getAllRecordsByListAndUser(String email);

    @Query("DELETE FROM PainRecord WHERE user_email = :email")
    void deleteAllRecords(String email);

    @Query("SELECT * FROM PainRecord WHERE date = :date AND user_email = :email LIMIT 1")
    PainRecord getRecordByDate(String date, String email);

    @Query("SELECT * FROM PainRecord WHERE date = :date")
    List<PainRecord> getPushRecordByDate(String date);

    @Query("SELECT pain_location as location,COUNT(*) as frequency FROM PainRecord WHERE user_email = :email GROUP BY pain_location")
    List<LocationFrequencyModel> getLocationFrequency(String email);
}