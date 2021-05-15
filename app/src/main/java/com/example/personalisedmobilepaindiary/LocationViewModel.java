package com.example.personalisedmobilepaindiary;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;

/*
 * View model for accessing location details
 */
public class LocationViewModel extends ViewModel
{
    // Live data to store location coordination.
    private MutableLiveData<String> location;

    public LocationViewModel()
    {
        location = new MutableLiveData<>();
    }

    public LiveData<String> getLocation()
    {
        return location;
    }

    public void setLocation(String location)
    {
        this.location.setValue(location);
    }
}
