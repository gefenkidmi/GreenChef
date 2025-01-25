package com.example.greenchef.DataClass

import com.google.firebase.firestore.GeoPoint
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val userId: String = "",
    val name: String = "",
    val photoUrl: String = "",
    val recipeIds: List<String> = emptyList(),
    val favoriteRecipeIds: List<String> = emptyList(),
    val ratedRecipes: Map<String, Float> = emptyMap(),
    val GeoPoint: GeoPoint? = null
) {
    // Add a no-argument constructor
    constructor() : this("", "", "", emptyList(), emptyList(), emptyMap())
}
