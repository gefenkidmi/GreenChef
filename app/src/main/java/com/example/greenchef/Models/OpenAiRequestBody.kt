package com.example.greenchef.Models

data class OpenAiRequestBody(
    val model: String = "text-davinci-003", // You can change the model version if needed
    val prompt: String,
    val max_tokens: Int = 100
)
