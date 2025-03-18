package com.example.greenchef.Services

import com.example.greenchef.Objects.OpenAiRequestBody
import com.example.greenchef.Objects.RecipeResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query


interface OpenAiService {
    @Headers("Content-Type: application/json")
    @POST("models/gemini-2.0-flash:generateContent")
    suspend fun generateContent(
        @Body requestBody: OpenAiRequestBody,
        @Query("key") apiKey: String
    ): Response<RecipeResponse>
}