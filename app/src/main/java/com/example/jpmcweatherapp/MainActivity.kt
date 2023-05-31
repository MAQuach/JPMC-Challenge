package com.example.jpmcweatherapp

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.jpmcweatherapp.ui.theme.JPMCWeatherTheme
import com.example.jpmcweatherapp.utils.UIState
import com.example.jpmcweatherapp.view.DetailsList
import com.example.jpmcweatherapp.view.SearchScreen
import com.example.jpmcweatherapp.viewmodel.WeatherViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            JPMCWeatherTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val weatherViewModel = hiltViewModel() as WeatherViewModel

                    NavHost(navController, "main") {
                        composable("main") {
                            SearchScreen(
                                weatherViewModel,
                                navController
                            )
                        }
                        composable("details") {
                            weatherViewModel.getWeather()
                            WeatherDetails(weatherViewModel)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HandlePermissions(weatherViewModel: WeatherViewModel) {
    // Check if permissions are already granted
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { permsMap ->
        val areGranted = permsMap.values.reduce { acc, next -> acc && next }

        if (areGranted) {
            weatherViewModel.getLocation()
        }
    }

    CheckRequestPermissions(
        LocalContext.current,
        arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ),
        launcher,
        weatherViewModel
    )
}

@Composable
fun CheckRequestPermissions(
    context: Context,
    permissions: Array<String>,
    launcher: ManagedActivityResultLauncher<Array<String>, Map<String, Boolean>>,
    viewModel: WeatherViewModel
) {
    if (permissions.all {
            ContextCompat.checkSelfPermission(
                context,
                it
            ) == PackageManager.PERMISSION_GRANTED
        }
    ) {
        viewModel.getLocation()

    } else {
        SideEffect {
            launcher.launch(permissions)
        }
    }
}

@Composable
fun WeatherDetails(weatherViewModel: WeatherViewModel) {
    when (val state = weatherViewModel.weatherResponse.value) {
        is UIState.ERROR -> {}
        is UIState.LOADING -> {}
        is UIState.SUCCESS -> {
            DetailsList(state.data)
        }
    }
}
