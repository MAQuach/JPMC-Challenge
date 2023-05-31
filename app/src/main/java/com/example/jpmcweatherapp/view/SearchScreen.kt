package com.example.jpmcweatherapp.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.jpmcweatherapp.HandlePermissions
import com.example.jpmcweatherapp.R
import com.example.jpmcweatherapp.viewmodel.WeatherViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    weatherViewModel: WeatherViewModel,
    navController: NavController? = null
) {
    val shouldGetLocation = remember { mutableStateOf(false) }
    val citySearch = remember {
        mutableStateOf(
            TextFieldValue(weatherViewModel.selectedCity ?: "")
        )
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        Arrangement.Top,
        Alignment.CenterHorizontally,
    ) {
        // EditText with the search icon at the end
        TextField(
            value = citySearch.value,
            onValueChange = { text ->
                weatherViewModel.isLocation.value?.let {
                    if (it) {
                        weatherViewModel.selectedCity?.let { city ->
                            citySearch.value = TextFieldValue(city)
                        } ?: let {
                            weatherViewModel.selectedCity = citySearch.value.text
                            citySearch.value = text
                        }
                    } else {
                        weatherViewModel.selectedCity = citySearch.value.text
                        citySearch.value = text
                    }
                } ?: let {
                    weatherViewModel.selectedCity = citySearch.value.text
                    citySearch.value = text
                }
            },
            trailingIcon = {
                IconButton(onClick = {
                    if (citySearch.value.text.isNotEmpty()) {
                        weatherViewModel.selectedCity = citySearch.value.text
                        navController?.navigate("details")
                    }
                }) {
                    Icon(Icons.Outlined.Search, null)
                }
            },
            placeholder = { Text(text = stringResource(R.string.enter_city_name)) },
            modifier = Modifier
                .padding(all = 16.dp)
                .fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                KeyboardCapitalization.None,
                true,
                KeyboardType.Text
            ),
            textStyle = TextStyle(
                color = Color.Black,
                fontFamily = FontFamily.SansSerif,
            ),
            maxLines = 1,
            singleLine = true,
        )

        // Button that looks up weather data at the current location
        Button(onClick = {
            shouldGetLocation.value = true
        }
        ) {
            Icon(
                Icons.Filled.LocationOn,
                stringResource(R.string.location),
                Modifier.size(ButtonDefaults.IconSize)
            )
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text(stringResource(R.string.check_your_current_location))
        }
    }

    if (shouldGetLocation.value) {
        HandlePermissions(weatherViewModel)
        navController?.navigate("details")
        shouldGetLocation.value = false
    }
}
