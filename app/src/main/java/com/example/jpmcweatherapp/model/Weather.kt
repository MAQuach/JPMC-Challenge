package com.example.jpmcweatherapp.model

import com.google.gson.annotations.SerializedName

/**
 * An object in the Weather API's WeatherResponse.
 */
data class Weather(

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("icon")
    val icon: String? = null,

    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("main")
    val main: String? = null
)
