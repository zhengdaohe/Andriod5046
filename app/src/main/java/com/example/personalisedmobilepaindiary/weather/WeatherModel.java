package com.example.personalisedmobilepaindiary.weather;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/*
 * A class to map the json data format returned from weather server.
 */
public class WeatherModel
{
    public data data;

    public static class Request
    {
        public String type;
        public String query;
    }

    public static class Current_condition
    {
        public int temp_C;
        public int humidity;
        public int pressure;
    }

    public static class data
    {
        public List<Request> request;
        public List<Current_condition> current_condition;

    }
}
