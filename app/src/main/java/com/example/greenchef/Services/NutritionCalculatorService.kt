package com.example.greenchef.Services

import android.util.Log
import com.example.greenchef.DataClass.Nutrition
import com.example.greenchef.DataClass.NutritonResponse
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
        "X-Api-Key:$apiKey"
    )
    @GET("nutrition")
    suspend fun fetchData(@Query("query") query:String):Response<NutritonResponse>
}

class NutritionCalculatorService {
    private val retrofit = Retrofit.Builder()
        .baseUrl(endpoint)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    suspend fun getNutritionalValues(ingredients: List<String>):Double{
        val query:String = ingredients.joinToString(", ")
        val apiService = retrofit.create(NutritionCalculatorApi::class.java)
        val response = apiService.fetchData(query)
        if (response.isSuccessful) {
            val data = response.body()?.items // Extract the list of Nutrition objects from the response body
            if (data != null) {
                return data.sumOf { it.calories }
            } else {
                return 0.0
            }
        } else {
            return 0.0
        }
    }
}