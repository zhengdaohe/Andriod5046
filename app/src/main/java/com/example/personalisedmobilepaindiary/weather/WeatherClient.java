package com.example.personalisedmobilepaindiary.weather;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WeatherClient {
    private static Retrofit retrofit;
    private static final String BASE_URL="https://api.worldweatheronline.com/";
    public static WeatherApi getWeatherService(){
        retrofit=new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(WeatherApi.class);
    }
}
