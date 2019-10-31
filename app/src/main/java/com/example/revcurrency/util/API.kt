package com.example.revcurrency.util

import android.util.Log
import com.example.revcurrency.util.APIUtil.okHttpClient
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import kotlin.coroutines.resumeWithException

abstract class API<T> {
    companion object {
        private val TAG = API::class.java.simpleName
    }

    lateinit var url: String

    abstract fun parseResult(response: String): T

    suspend fun await(): T {
        val request = Request.Builder().url(url).build()
        val call = okHttpClient.newCall(request)

        return suspendCancellableCoroutine { continuation ->
            MyLog.d(TAG, "start ${this@API.javaClass.simpleName} $url")
            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    MyLog.d(
                        TAG,
                        "${this@API.javaClass.simpleName} onFailure: ${Log.getStackTraceString(e)}"
                    )
                    if (continuation.isCancelled) return

                    continuation.resumeWithException(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    continuation.resumeWith(runCatching {
                        val bodyStr = response.body?.string()
                        if (response.isSuccessful) {
                            bodyStr?.let {
                                MyLog.d(
                                    TAG,
                                    "${this@API.javaClass.simpleName} onResponse successful: $it"
                                )
                                parseResult(it)
                            } ?: run {
                                MyLog.d(
                                    TAG,
                                    "${this@API.javaClass.simpleName} onResponse fail: response body is null"
                                )
                                throw NullPointerException("Response body is null")
                            }
                        } else {
                            throw APIUtil.HttpException(
                                response.code,
                                response.message,
                                bodyStr
                            ).also {
                                MyLog.d(
                                    TAG,
                                    "${this@API.javaClass.simpleName} onResponse fail: $it"
                                )
                            }
                        }
                    })
                }
            })

            call.registerOnCompletion(continuation)
        }
    }

    private fun Call.registerOnCompletion(continuation: CancellableContinuation<*>) {
        continuation.invokeOnCancellation {
            MyLog.d(TAG, "${this@API.javaClass.simpleName} onCancellation")
            cancel()
        }
    }
}