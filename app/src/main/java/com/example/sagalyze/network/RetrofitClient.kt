package com.example.sagalyze.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

object RetrofitClient {

    // ⚠️ Replace this with your system's actual IPv4 address
    // (run `ipconfig` on Windows or `ifconfig` on macOS/Linux)
    private const val BASE_URL = "http://192.168.83.93:8000"

    // Logging Interceptor for debugging requests/responses
    private val logger = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // OkHttp client with logging enabled
    private val client = OkHttpClient.Builder()
        .addInterceptor(logger)
        .build()

    // Retrofit instance configured with Gson converter
    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(ApiService::class.java)
    }
}
