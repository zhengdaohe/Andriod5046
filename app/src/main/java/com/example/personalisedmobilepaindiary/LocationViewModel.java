package com.example.personalisedmobilepaindiary;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;

public class LocationViewModel extends ViewModel {
    private MutableLiveData<String> location;

    public LocationViewModel(){
        location = new MutableLiveData<>();
    }
    public void setLocation(String location){
        this.location.setValue(location);
    }
    public LiveData<String> getLocation() {
        return location;
    }
}
