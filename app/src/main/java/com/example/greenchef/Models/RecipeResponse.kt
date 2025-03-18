package com.example.greenchef.Objects

import com.google.gson.annotations.SerializedName

data class RecipeResponse(
    @SerializedName("candidates") val candidates: List<ResponseCandidate>
)

data class ResponseCandidate(
    @SerializedName("content") val content: ResponseContent
)

data class ResponseContent(
    @SerializedName("parts") val parts: List<ResponsePart>
)

data class ResponsePart(
    @SerializedName("text") val text: String
)