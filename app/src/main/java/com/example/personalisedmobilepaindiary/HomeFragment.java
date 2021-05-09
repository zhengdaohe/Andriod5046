package com.example.personalisedmobilepaindiary;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.personalisedmobilepaindiary.databinding.HomeFragmentBinding;
import com.example.personalisedmobilepaindiary.weather.WeatherApi;
import com.example.personalisedmobilepaindiary.weather.WeatherClient;
import com.example.personalisedmobilepaindiary.weather.WeatherModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;

import android.location.Address;
import android.location.Geocoder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    private HomeFragmentBinding binding;
    private FusedLocationProviderClient fusedLocationClient;
    @SuppressLint("MissingPermission")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = HomeFragmentBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        binding.signoutButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            getActivity().recreate();
        });
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        LocationViewModel model = new
                ViewModelProvider(requireActivity()).get(LocationViewModel.class);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(requireActivity(), location -> {
                    if (location != null) {
//                        model.setLocation(String.valueOf(location.getLatitude()) + "," + String.valueOf(location.getLongitude()));
                        model.setLocation("-37.9,145.126");
                    }
                });

        model.getLocation().observe(getViewLifecycleOwner(), v -> {
              WeatherApi weatherApi = WeatherClient.getWeatherService();
              Call<WeatherModel> callAsync = weatherApi.getWeatherByLocation(v,"today", "json","45aa79be0da34cef9dd182050210105");
              callAsync.enqueue(new Callback<WeatherModel>() {

                  @Override
                  public void onResponse(Call<WeatherModel> call, Response<WeatherModel> response) {
                      if (isAdded()){
                          requireActivity().runOnUiThread(() -> {
                              if (response.isSuccessful()) {
                                  Geocoder geocoder = new Geocoder(requireActivity());
                                  try {
                                      List<Address> addresses = geocoder.getFromLocation(Double.parseDouble(v.split(",")[0]) ,
                                              Double.parseDouble(v.split(",")[1]), 1);
                                      String addressValue = "";
                                      if (addresses.size() > 0) {
                                          Address address = addresses.get(0);
                                          binding.location.setText("location: " + addresses.get(0).getAddressLine(0));
                                          for (int i = 0; i < address.getMaxAddressLineIndex(); i++){
                                              if (i < 2)
                                                  addressValue = addressValue + address.getAddressLine(i) + ", ";
                                          }
                                      }
                                  }
                                  catch (Exception E){

                                  }
                                  binding.temperature.setText("temperature: " + response.body().data.current_condition.get(0).temp_C + "℃");
                                  binding.humidity.setText("humidity: " + response.body().data.current_condition.get(0).humidity + "%");
                                  String pressure = "";
                                  for (int i = 0; i < String.valueOf(response.body().data.current_condition.get(0).pressure).length(); i++){
                                      pressure += String.valueOf(response.body().data.current_condition.get(0).pressure).charAt(i);
                                      if (i == 2)
                                          pressure += ".";
                                  }
                                  pressure += "kPa";
                                  binding.pressure.setText("pressure: " + pressure);
                              }
                              SharedPreferences weather = requireActivity().getSharedPreferences("WEATHER_PREFERENCE", Context.MODE_PRIVATE);
                              SharedPreferences.Editor wtEditor = weather.edit();
                              wtEditor.putInt("temperature", response.body().data.current_condition.get(0).temp_C);
                              wtEditor.putInt("humidity", response.body().data.current_condition.get(0).humidity);
                              wtEditor.putInt("pressure", response.body().data.current_condition.get(0).pressure);
                              wtEditor.apply();
                          });
                      }


                  }

                  @Override
                  public void onFailure(Call<WeatherModel> call, Throwable t) {
                      requireActivity().runOnUiThread(() -> {
                          binding.location.setText("Cannot connect to weather server!");
                  });}
              });
        });
        return view;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}