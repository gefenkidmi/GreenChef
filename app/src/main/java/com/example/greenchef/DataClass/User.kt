package com.example.greenchef.DataClass

data class User(
    val userId: String,
    var name: String,
    var photoUrl: String?,
    var recipeIds: MutableList<String>
)