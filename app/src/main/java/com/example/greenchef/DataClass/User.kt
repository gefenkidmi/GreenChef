package com.example.greenchef.DataClass

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val userId: String = "",
    val name: String = "",
    val photoUrl: String = "",
    val recipeIds: List<String> = emptyList(),
    val favoriteRecipeIds: List<String> = emptyList(),
    val ratedRecipes: Map<String, Float> = emptyMap()
) {
    constructor() : this("", "", "", emptyList(), emptyList(), emptyMap())
}
