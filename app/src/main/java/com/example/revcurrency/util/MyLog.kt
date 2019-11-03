package com.example.revcurrency.util

import android.util.Log
import com.example.revcurrency.BuildConfig

object MyLog {

    var isLoggable = BuildConfig.DEBUG

    fun d(tag: String, msg: String) {
        if (isLoggable) {
            Log.d(tag, msg)
        }
    }

    fun i(tag: String, msg: String) {
        if (isLoggable) {
            Log.i(tag, msg)
        }
    }

    fun e(tag: String, msg: String) {
        if (isLoggable) {
            Log.e(tag, msg)
        }
    }

    fun v(tag: String, msg: String) {
        if (isLoggable) {
            Log.v(tag, msg)
        }
    }

    fun w(tag: String, msg: String) {
        if (isLoggable) {
            Log.w(tag, msg)
        }
    }
}