package com.example.greenchef.Services

import com.example.greenchef.Models.OpenAiRequestBody
import com.example.greenchef.Models.RecipeResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface OpenAiService {

    @POST("v1/completions")
    suspend fun getRecipeSuggestions(
        @Body requestBody: OpenAiRequestBody
    ): Response<RecipeResponse>
}
