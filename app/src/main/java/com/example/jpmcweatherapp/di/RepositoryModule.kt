package com.example.jpmcweatherapp.di

import com.example.jpmcweatherapp.network.WeatherRepository
import com.example.jpmcweatherapp.network.WeatherRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun WeatherRepository(weatherRepositoryImpl: WeatherRepositoryImpl)
            : WeatherRepository
}
