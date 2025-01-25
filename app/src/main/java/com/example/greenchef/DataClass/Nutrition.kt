package com.example.greenchef.DataClass

data class Nutrition(
    var name: String,
    var calories: Double,
    var serving_size_g: Double,
    var fat_total_g: Double,
    var fat_saturated_g: Double,
    var protein_g: Double,
    var sodium_mg: Double,
    var potassium_mg: Double,
    var cholesterol_mg: Double,
    var carbohydrates_total_g: Double,
    var fiber_g: Double,
    var sugar_g: Double
)

data class NutritonResponse(
    val items: List<Nutrition>
)