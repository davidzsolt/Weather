package com.example.weather.repository

import com.example.weather.data.WeatherData
import retrofit2.Call

import retrofit2.http.GET
import retrofit2.http.Path

interface RetrofitApiService {

    @GET("api/location/804365/")
    fun getWeather(): Call<WeatherData>
}