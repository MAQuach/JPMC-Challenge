package com.example.jpmcweatherapp.model

import com.google.gson.annotations.SerializedName

data class Clouds(
    @SerializedName("all")
    val all: Int? = null
)
