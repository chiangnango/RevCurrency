package com.example.revcurrency.util

import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

object APIUtil {
    const val DEFAULT_CURRENCY = "EUR"

    val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .build()

    data class HttpException(
        val code: Int,
        val msg: String,
        val body: String?
    ) : Exception("HTTP $code $msg")
}