package com.example.carmultimedia;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClientWeather {

    private static final String BASE_URL = "https://yahoo-weather5.p.rapidapi.com/";
    private static Retrofit retrofit;

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static WeatherApiService getWeatherApiService() {
        return getRetrofitInstance().create(WeatherApiService.class);
    }
}
