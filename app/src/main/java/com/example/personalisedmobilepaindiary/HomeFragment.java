package com.example.personalisedmobilepaindiary;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
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

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment
{
    private HomeFragmentBinding binding;
    // Location server client
    private FusedLocationProviderClient fusedLocationClient;

    // Because the permission is checked during the initialization of main activity, skip the permission checking of location service
    @SuppressLint("MissingPermission")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        binding = HomeFragmentBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        // configure sign out button.
        binding.signoutButton.setOnClickListener(v ->
        {
            FirebaseAuth.getInstance().signOut();
            getActivity().recreate();
        });
        // Get instance of location service
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        // View model to store location longitude and latitude.
        LocationViewModel model = new
                ViewModelProvider(requireActivity()).get(LocationViewModel.class);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(requireActivity(), location ->
                {
                    // Get current location and store its details into view model
                    if (location != null)
                    {
                        model.setLocation(location.getLatitude() + "," + location.getLongitude());
                    }
                    // if location service unavailable, use a default value.
                    else
                    {
                        Log.e("location", "unable to connect location service, use default location (-37.9,145.126)");
                        model.setLocation("-37.9,145.126");
                    }
                });

        model.getLocation().observe(getViewLifecycleOwner(), v ->
        {
            WeatherApi weatherApi = WeatherClient.getWeatherService();
            // Asynchronously call weather api using previously acquired location.
            Call<WeatherModel> callAsync = weatherApi.getWeatherByLocation(v, "today", "json", "45aa79be0da34cef9dd182050210105");
            callAsync.enqueue(new Callback<WeatherModel>()
            {
                @Override
                public void onResponse(Call<WeatherModel> call, Response<WeatherModel> response)
                {
                    // Check if the fragment is added into the activity.
                    if (isAdded())
                    {
                        //If so, start updating the UI in main thread.
                        requireActivity().runOnUiThread(() ->
                        {
                            if (response.isSuccessful())
                            {
                                // Use geocode to decode coordinates into address name.
                                Geocoder geocoder = new Geocoder(requireActivity());
                                try
                                {
                                    // Get one matching address
                                    List<Address> addresses = geocoder.getFromLocation(Double.parseDouble(v.split(",")[0]),
                                            Double.parseDouble(v.split(",")[1]), 1);
                                    String addressValue = "";
                                    if (addresses.size() > 0)
                                    {
                                        Address address = addresses.get(0);
                                        // If address line is available, get the first address line, and show it in the UI.
                                        if (address.getMaxAddressLineIndex() != -1)
                                        {
                                            binding.location.setText("location: " + address.getAddressLine(0));
                                        }
                                        // If address line is not available, just show a rough address.
                                        else
                                        {
                                            binding.location.setText("location: " + address.getLocality() + ", " + address.getAdminArea() + ", " + address.getCountryName());
                                        }
                                    }
                                } catch (Exception E)
                                {
                                }
                                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+10:00"));
                                String date = "" + calendar.get(Calendar.DAY_OF_MONTH) + "/" + (calendar.get(Calendar.MONTH) + 1) +
                                        "/" + calendar.get(Calendar.YEAR);
                                binding.date.setText("date: " + date);
                                // Use data returned from weather api to get weather information and show them in UI, giving them unit indicator.
                                binding.temperature.setText("temperature: " + response.body().data.current_condition.get(0).temp_C + "℃");
                                binding.humidity.setText("humidity: " + response.body().data.current_condition.get(0).humidity + "%");
                                // For the pressure, format the value to a readable format, eg. 101.2kPa
                                String pressure = "";
                                for (int i = 0; i < String.valueOf(response.body().data.current_condition.get(0).pressure).length(); i++)
                                {
                                    pressure += String.valueOf(response.body().data.current_condition.get(0).pressure).charAt(i);
                                    if (i == 2)
                                        pressure += ".";
                                }
                                pressure += "kPa";
                                binding.pressure.setText("pressure: " + pressure);
                            }
                            // Store current weather data into shared preference for future use.
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
                public void onFailure(Call<WeatherModel> call, Throwable t)
                {
                    // A notification shown when unable to connect weather server.
                    requireActivity().runOnUiThread(() ->
                    {
                        binding.location.setText("Cannot connect to weather server!");
                    });
                }
            });
        });
        return view;
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        binding = null;
    }
}