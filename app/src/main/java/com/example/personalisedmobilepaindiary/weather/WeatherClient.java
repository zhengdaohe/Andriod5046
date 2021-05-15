package com.example.personalisedmobilepaindiary.weather;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/*
 * A class to create an instance of weather api.
 */
public class WeatherClient
{
    private static final String BASE_URL = "https://api.worldweatheronline.com/";
    private static Retrofit retrofit;

    public static WeatherApi getWeatherService()
    {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(WeatherApi.class);
    }
}
