package com.reviews8.app
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .addInterceptor { chain ->
            val newRequest = chain.request().newBuilder()
                .header("User-Agent", "Mozilla/5.0 (Android) Reviews8 App")
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .build()
            chain.proceed(newRequest)
        }
        .build()

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(ApiService.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
