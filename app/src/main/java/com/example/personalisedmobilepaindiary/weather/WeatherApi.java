package com.example.personalisedmobilepaindiary.weather;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherApi {
    @GET("premium/v1/weather.ashx")
    Call<WeatherModel> getWeatherByLocation(@Query("q") String location, @Query("date") String date,
                                            @Query("format") String format, @Query("key") String key);
//    Call<WeatherModel> getWeatherByLocation();
}