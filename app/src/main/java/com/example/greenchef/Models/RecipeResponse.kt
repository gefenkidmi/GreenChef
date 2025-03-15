package com.example.greenchef.Models

data class RecipeResponse(
    val choices: List<Choice>
)

data class Choice(
    val text: String
)
