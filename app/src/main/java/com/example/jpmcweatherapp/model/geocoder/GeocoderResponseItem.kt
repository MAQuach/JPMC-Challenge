package com.example.jpmcweatherapp.model.geocoder

import com.google.gson.annotations.SerializedName

data class GeocoderResponseItem(

    @SerializedName("country")
    val country: String? = null,

    @SerializedName("lat")
    val lat: Double? = null,

    @SerializedName("local_names")
    val localNames: LocalNames? = null,

    @SerializedName("lon")
    val lon: Double? = null,

    @SerializedName("name")
    val name: String? = null
)
