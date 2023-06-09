package com.example.jpmcweatherapp.model

import com.google.gson.annotations.SerializedName

data class Wind(

    @SerializedName("deg")
    val deg: Int? = null,

    @SerializedName("speed")
    val speed: Double? = null
)
