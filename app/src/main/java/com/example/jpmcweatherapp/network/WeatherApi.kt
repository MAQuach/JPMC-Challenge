package com.example.jpmcweatherapp.network

import com.example.jpmcweatherapp.model.WeatherResponse
import com.example.jpmcweatherapp.model.geocoder.GeocoderResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

// https://api.openweathermap.org/data/2.5/weather?q={city name},{state code},{country code}&appid={API key}
private const val APP_ID = "604819d9723d05bfa5c04fa217f03737"

private const val PATH ="data/2.5/weather"
private const val GEOCODER = "geo/1.0/reverse"
const val BASE_URL = "https://api.openweathermap.org/"
const val BASE_IMAGE = "https://openweathermap.org/img/wn/"

interface WeatherAPI {

    @GET(PATH)
    suspend fun getWeatherByCity(
        @Query("q") city: String,
        @Query("appid") appid: String = APP_ID
    ): Response<WeatherResponse>

    @GET(GEOCODER)
    suspend fun getGeocodeName(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("limit") limit: Int = 1,
        @Query("appid") appid: String = APP_ID
    ): Response<GeocoderResponse>
}
