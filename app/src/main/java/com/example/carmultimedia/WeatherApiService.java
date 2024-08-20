package com.example.carmultimedia;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface WeatherApiService {

    @GET("weather")
    Call<WeatherResponse> getWeather(
        @Query("lat") double latitude,
        @Query("long") double longitude,
        @Query("format") String format,
        @Query("u") String unit,
        @Header("x-rapidapi-host") String host,
        @Header("x-rapidapi-key") String apiKey
    );
}
