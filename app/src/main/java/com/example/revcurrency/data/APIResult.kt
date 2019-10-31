package com.example.revcurrency.data

sealed class APIResult<T> {
    data class Success<T>(val data: T) : APIResult<T>()
    data class Failure<T>(val exception: Exception) : APIResult<T>()
}