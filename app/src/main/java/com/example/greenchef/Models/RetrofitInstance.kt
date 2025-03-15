package com.example.greenchef

import com.example.greenchef.Services.OpenAiService
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {

    private const val BASE_URL = "https://api.openai.com/"
    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val originalRequest: Request = chain.request()
            val newRequest = originalRequest.newBuilder()
                .header("Authorization", "Bearer ${BuildConfig.OPENAI_API_KEY}") // API Key in header
                .build()
            chain.proceed(newRequest)
        }
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    val api: OpenAiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client) // Attach OkHttp client with header
            .addConverterFactory(GsonConverterFactory.create()) // Use Gson to parse JSON
            .build()
            .create(OpenAiService::class.java) // Create OpenAiService
    }
}
