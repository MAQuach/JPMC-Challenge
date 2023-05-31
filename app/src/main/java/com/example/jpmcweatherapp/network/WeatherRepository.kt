package com.example.jpmcweatherapp.network

import com.example.jpmcweatherapp.model.WeatherResponse
import com.example.jpmcweatherapp.utils.UIState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject


interface WeatherRepository {
    /**
    https://api.openweathermap.org/data/2.5/weather?q={city name},{state code},{country code}&appid={API key}

    https://openweathermap.org/api/geocoding-api
    http://api.openweathermap.org/geo/1.0/reverse?lat={lat}&lon={lon}&limit={limit}&appid={API key}
    */
    fun getWeather(
        city: String,
        state: String? = null,
        country: String? = null
    ): Flow<UIState<WeatherResponse>>

    suspend fun getCityName(lat: Double, lon: Double): String
}

class WeatherRepositoryImpl @Inject constructor(
    private val weatherAPI: WeatherAPI,
    private val dispatcher: CoroutineDispatcher
) : WeatherRepository {

    /**
     * API call that fetches the weather data for a location.
     */
    override fun getWeather(
        city: String,
        state: String?,
        country: String?
    ): Flow<UIState<WeatherResponse>> = flow {
        emit(UIState.LOADING)

        try {
            val response = weatherAPI.getWeatherByCity(city)
            if (response.isSuccessful) {
                response.body()?.let {
                    emit(UIState.SUCCESS(it))
                } ?: throw Exception("Weather information not available")
            } else {
                throw Exception(response.errorBody()?.string())
            }
        } catch (e: Exception) {
            emit(UIState.ERROR(e))
        }
    }.flowOn(dispatcher)

    /**
     * API call that translates coords into a city name.
     */
    override suspend fun getCityName(lat: Double, lon: Double): String =
        weatherAPI.getGeocodeName(lat, lon).body()
            ?.firstOrNull()?.name ?: ""
}
