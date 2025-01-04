package com.example.greenchef.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.greenchef.DataClass.Recipe
import com.example.greenchef.Repositories.FirestoreRepository

class RecipeViewModel : ViewModel() {

    private val repository = FirestoreRepository()
    private val _recipeData = MutableLiveData<Recipe>()
    val recipeData: LiveData<Recipe> get() = _recipeData

    fun fetchRecipeData(recipeId: String) {
        repository.getRecipeData(recipeId).observeForever {
            _recipeData.value = it
        }
    }

    fun updateRecipe(recipe: Recipe) {
        repository.updateRecipe(recipe)
    }

    fun getAllRecipes(): LiveData<List<Recipe>> {
        return repository.getAllRecipes()
    }

    fun getRecipesByCategory(category: String): LiveData<List<Recipe>> {
        return repository.getRecipesByCategory(category)
    }

    // ... other methods
}