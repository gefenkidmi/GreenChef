package com.example.greenchef.Models

import com.example.greenchef.Services.OpenAiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/"

    val geminiApi: OpenAiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenAiService::class.java)
    }
}