package com.example.jpmcweatherapp.utils

sealed class UIState<out T> {
    object LOADING : UIState<Nothing>()
    data class SUCCESS<T>(val data: T) : UIState<T>()
    data class ERROR(val error: Exception) : UIState<Nothing>()
}
