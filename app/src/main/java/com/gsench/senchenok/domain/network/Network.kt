package com.gsench.senchenok.domain.network

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import java.io.File
import java.util.concurrent.TimeUnit

class Network(context: Context) {

    var networkState = NetworkState.UNDEFINED
        private set

    init {
        networkStateFlow(context)
            .onEach { networkState = it }
            .launchIn(CoroutineScope(Dispatchers.IO))
    }

    private companion object HttpClientConstants {
        const val HTTP_CACHE_PATH = "http-cache"
        const val HTTP_CACHE_SIZE = 10L * 1024L * 1024L
    }

    private val cacheInterceptor = object: Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val response: Response = chain.proceed(chain.request())
            val cacheControl = CacheControl.Builder()
                .maxAge(1, TimeUnit.DAYS)
                .build()
            return response.newBuilder()
                .header("Cache-Control", cacheControl.toString())
                .build()
        }
    }

    private val forceCacheInterceptor = object: Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val builder: Request.Builder = chain.request().newBuilder()
            if (networkState != NetworkState.AVAILABLE) {
                builder.cacheControl(CacheControl.FORCE_CACHE)
            }
            return chain.proceed(builder.build())
        }
    }

    val httpClient = OkHttpClient
        .Builder()
        .cache(
            Cache(
                File(context.cacheDir, HTTP_CACHE_PATH),
                HTTP_CACHE_SIZE
            )
        )
        .addNetworkInterceptor(cacheInterceptor)
        .addInterceptor(forceCacheInterceptor)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        })
        .build()

}