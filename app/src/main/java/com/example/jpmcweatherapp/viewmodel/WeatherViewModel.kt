package com.example.jpmcweatherapp.viewmodel

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.location.Location
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.compose.rememberNavController
import com.example.jpmcweatherapp.model.WeatherResponse
import com.example.jpmcweatherapp.network.WeatherRepository
import com.example.jpmcweatherapp.utils.UIState
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val LOCATION_KEY = "LOCATION_KEY"

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val dispatcher: CoroutineDispatcher,
    private val sharedPreferences: SharedPreferences,
    private val fusedLocationProviderClient: FusedLocationProviderClient
) : ViewModel() {

    var selectedCity: String?
        get() = sharedPreferences.getString(LOCATION_KEY, null)
        set(value) {
            sharedPreferences.edit {
                putString(LOCATION_KEY, value)
                apply()
            }
        }

    private val _weatherResponse: MutableState<UIState<WeatherResponse>> = mutableStateOf(UIState.LOADING)
    val weatherResponse: State<UIState<WeatherResponse>>
        get() = _weatherResponse

    private val _isLocation: MutableState<Boolean?> = mutableStateOf(null)
    val isLocation: State<Boolean?>
        get() = _isLocation

    /**
     * Call the Weather API and post the response to _weatherResponse.
     */
    fun getWeather() {
        viewModelScope.launch(dispatcher) {
            selectedCity?.let {
                weatherRepository.getWeather(it)
                    .collect { state -> _weatherResponse.value = state }
            } ?: let {
                _weatherResponse.value = UIState.ERROR(Exception("Error: Please enter a location."))
            }
        }
    }

    /**
     * Fetch the name of the city that contains the given LatLng.
     */
    private fun getCityNameFromLocation(location: Location) {
        viewModelScope.launch {
            selectedCity = weatherRepository.getCityName(location.latitude, location.longitude)
            _isLocation.value = true
        }
    }

    /**
     * Fetch user's LatLng using Fused Location.
     */
    @SuppressLint("MissingPermission")
    fun getLocation() {
        val task = fusedLocationProviderClient.lastLocation

        task
            .addOnSuccessListener { loc ->
                if (loc != null) {
                    getCityNameFromLocation(loc)
                }
            }
            .addOnFailureListener {
                _isLocation.value = false
            }
    }
}
