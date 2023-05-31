package com.example.jpmcweatherapp

import android.content.SharedPreferences
import com.example.jpmcweatherapp.model.Clouds
import com.example.jpmcweatherapp.model.Coord
import com.example.jpmcweatherapp.model.Main
import com.example.jpmcweatherapp.model.Sys
import com.example.jpmcweatherapp.model.Weather
import com.example.jpmcweatherapp.model.WeatherResponse
import com.example.jpmcweatherapp.model.Wind
import com.example.jpmcweatherapp.network.WeatherRepository
import com.example.jpmcweatherapp.utils.UIState
import com.example.jpmcweatherapp.viewmodel.WeatherViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

@ExperimentalCoroutinesApi
class WeatherViewModelTests {

    @Mock
    private lateinit var repository: WeatherRepository

    @Mock
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    @Mock
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var viewModel: WeatherViewModel

    // Add a companion object to provide the TestCoroutineDispatcher
    companion object {
        @ObsoleteCoroutinesApi
        @JvmField
        val testDispatcher = TestCoroutineDispatcher()
    }

    @OptIn(ObsoleteCoroutinesApi::class)
    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        viewModel =
            WeatherViewModel(repository, testDispatcher, sharedPreferences, fusedLocationClient)
    }

    @Test
    fun `getWeather - success`() = runBlockingTest {
        // Given
        Dispatchers.setMain(TestCoroutineDispatcher()) // Mock the Main dispatcher
        val city = "London"
        val weatherModel = WeatherResponse(
            base = "",
            clouds = Clouds(all = 75),
            cod = 200,
            coord = Coord(lat = 51.5085, lon = -0.1257),
            dt = 1685036251,
            id = 2643743,
            main = Main(
                feelsLike = 289.21,
                humidity = 60,
                pressure = 1029,
                temp = 289.91,
                tempMax = 291.55,
                tempMin = 287.51
            ),
            name = "london",
            sys = Sys(
                country = "GB",
                id = 268730,
                sunrise = 1684986974,
                sunset = 1685044733,
                type = 2
            ),
            timezone = 3600,
            visibility = 10000,
            weather = listOf(
                Weather(
                    description = "broken clouds",
                    icon = "04d",
                    id = 803,
                    main = "Clouds"
                )
            ),
            wind = Wind(speed = 4.12, deg = 50)
        )
        Mockito
            .`when`(repository.getWeather(city))
            .thenReturn(flow { emit(UIState.SUCCESS(weatherModel)) })

        // When
        viewModel.getWeather()

        // Then
        assertEquals(UIState.SUCCESS(weatherModel), viewModel.weatherResponse.value)
    }

    @OptIn(ObsoleteCoroutinesApi::class)
    @Test
    fun `getWeather - IO exception`() = runBlockingTest {
        val cityName = "London"
        val expectedException = IOException("Network error")
        Mockito.`when`(repository.getWeather(cityName)).thenAnswer { throw expectedException }

        // Initialize the Main dispatcher
        Dispatchers.setMain(testDispatcher)

        // Act
        viewModel.getWeather()

        // Assert
        val result = viewModel.weatherResponse.value
        Assert.assertTrue(result is UIState.ERROR)
        Assert.assertEquals(expectedException, (result as UIState.ERROR).error)

        // Reset the Main dispatcher
        Dispatchers.resetMain()
    }

    @OptIn(ObsoleteCoroutinesApi::class)
    @Test
    fun `getWeather - HTTP exception`() = runBlockingTest {
        // Arrange
        val cityName = "London"
        val expectedException = HttpException(Response.error<Any>(404, "".toResponseBody()))
        Mockito.`when`(repository.getWeather(cityName)).thenThrow(expectedException)

        // Initialize the Main dispatcher
        Dispatchers.setMain(testDispatcher)

        // Act
        viewModel.getWeather()

        // Assert
        val result = viewModel.weatherResponse.value
        Assert.assertTrue(result is UIState.ERROR)
        Assert.assertEquals(expectedException, (result as UIState.ERROR).error)

        // Reset the Main dispatcher
        Dispatchers.resetMain()
    }

    @OptIn(ObsoleteCoroutinesApi::class)
    @Test
    fun `getWeather - unknown exception`() = runBlockingTest {
        // Arrange
        val cityName = "London"
        val expectedException = RuntimeException("Unknown error")
        Mockito.`when`(repository.getWeather(cityName)).thenThrow(expectedException)

        // Initialize the Main dispatcher
        Dispatchers.setMain(testDispatcher)

        // Act
        viewModel.getWeather()

        // Assert
        val result = viewModel.weatherResponse.value
        Assert.assertTrue(result is UIState.ERROR)
        Assert.assertEquals(expectedException, (result as UIState.ERROR).error)

        // Reset the Main dispatcher
        Dispatchers.resetMain()
    }
}
