package com.rh.heji.network

import com.rh.heji.AppCache
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class HejiNetwork {
    private val hejiServer = AppCache.getInstance().heJiServer;
    suspend fun login(username: String, password: String) = hejiServer.login(username, password).await()

    private suspend fun <T> Call<T>.await(): T {
        return suspendCoroutine { continuation ->
            enqueue(object : Callback<T> {
                override fun onFailure(call: Call<T>, t: Throwable) {
                    continuation.resumeWithException(t)
                }

                override fun onResponse(call: Call<T>, response: Response<T>) {
                    val body = response.body()
                    val errorBody = response.errorBody()
                    when {
                        body != null -> continuation.resume(body)
                        errorBody != null -> continuation.resumeWithException(RuntimeException(errorBody.string()))
                        else -> continuation.resumeWithException(RuntimeException("response body is null"))
                    }
                }
            })
        }
    }

    companion object {

        private var network: HejiNetwork? = null

        fun getInstance(): HejiNetwork {
            if (network == null) {
                synchronized(HejiNetwork::class.java) {
                    if (network == null) {
                        network = HejiNetwork()
                    }
                }
            }
            return network!!
        }

    }

}