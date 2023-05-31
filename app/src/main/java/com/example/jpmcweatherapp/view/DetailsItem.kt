package com.example.jpmcweatherapp.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.jpmcweatherapp.R
import com.example.jpmcweatherapp.model.WeatherResponse
import com.example.jpmcweatherapp.network.BASE_IMAGE

@Composable
fun DetailsList(weathers: WeatherResponse? = null) {
    LazyColumn {
        itemsIndexed(
            listOf(weathers)
        ) { _, sat ->
            sat?.let {
                DetailItem(it)
            } ?: Text(stringResource(R.string.no_location_info))
        }
    }
}

/**
 * Displays the weather data for the given WeatherResponse.
 */
@Composable
fun DetailItem(weatherResponse: WeatherResponse) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(String.format(
                stringResource(R.string.temperature),
                weatherResponse.main?.temp.toString()
            ))
        Text(String.format(
                stringResource(R.string.temperature_max),
                weatherResponse.main?.tempMax.toString()
            ))
        Text(String.format(
                stringResource(R.string.temperature_min),
                weatherResponse.main?.tempMin.toString()
            ))
        Text(String.format(
                stringResource(R.string.temperature_feels_like),
                weatherResponse.main?.feelsLike.toString()
            ))

        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(
                    BASE_IMAGE +
                            weatherResponse.weather?.get(0)?.icon +
                            "@2x.png"
                )
                .crossfade(true)
                .build(),

            placeholder = painterResource(R.drawable.broken_image),
            contentDescription = weatherResponse.weather?.get(0)?.description,
            contentScale = ContentScale.Crop,
            modifier = Modifier.clip(CircleShape)
        )
    }
}
