package com.example.greenchef.Services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.example.greenchef.DataClass.Nutrition
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Headers
import retrofit2.http.Query

private const val endpoint = "https://api.calorieninjas.com/v1/"
private const val apiKey = "qfFW3Tl17BfTmoKngxMyhQ==SNAI6dfzsQo6xuZy"

interface NutritionCalculatorApi{
    @Headers(
        "Accept: application/json",
        "Content-Type: application/json",
        "X-Api-Key:" + apiKey,
        "Platform: android"
    )
    @GET("nutrition")
    fun fetchData(@Query("query") query:String): Response<List<Nutrition>>
}

class NutritionCalculatorService : Service() {
    private val retrofit = Retrofit.Builder()
        .baseUrl(endpoint)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    fun getNutritionalValues(ingredients: List<String>):Double{
        val query = ingredients.joinToString(", ")
        val apiService = retrofit.create(NutritionCalculatorApi::class.java)
        val response = apiService.fetchData(query)
        if (response.isSuccessful) {
            val data = response.body()
            return data.sumOf { it.calories}
        } else {
            return 0.0
        }
    }
}