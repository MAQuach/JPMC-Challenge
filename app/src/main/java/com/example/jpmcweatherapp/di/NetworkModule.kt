package com.example.jpmcweatherapp.di

import android.content.Context
import android.content.SharedPreferences
import com.example.jpmcweatherapp.network.BASE_URL
import com.example.jpmcweatherapp.network.WeatherAPI
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    fun providesLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

    @Provides
    fun providesClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    fun providesGson(): Gson =
        Gson()

    @Provides
    fun providesIODispatcher(): CoroutineDispatcher =
        Dispatchers.IO

    @Provides
    fun providesSharedPreference(@ApplicationContext context: Context): SharedPreferences =
        context.getSharedPreferences("LOCATION_SHARED_PREFS", Context.MODE_PRIVATE)

    @Provides
    fun providesRetrofit(
        client: OkHttpClient,
        gson: Gson
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    fun weatherAPI(retrofit: Retrofit): WeatherAPI =
        retrofit.create(WeatherAPI::class.java)

    @Provides
    fun providesFusedLocation(@ApplicationContext context: Context): FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)
}
