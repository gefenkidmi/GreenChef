package com.example.greenchef.ViewModels

import android.content.Context
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.greenchef.DB.RecipeDatabase
import com.example.greenchef.Dao.RecipeDao
import com.example.greenchef.DataClass.Recipe
import com.example.greenchef.Models.FirestoreModel
import com.example.greenchef.Objects.RecipeLocalTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RecipeViewModel() : ViewModel() {
    private lateinit var context: Context
    private lateinit var recipeDao: RecipeDao
    private val _allRecipes: MutableLiveData<List<Recipe>> = MutableLiveData()
    private val allRecipes: LiveData<List<Recipe>> = _allRecipes

    fun setContextAndDB(context: Context) {
        this.context = context
        recipeDao = RecipeDatabase.db(context).recipeDao()
        recipeDao.getAll().observeForever { recipes ->
            _allRecipes.postValue(recipes)
        }
    }
    fun getAllRecipes(): LiveData<List<Recipe>> {
        val localLastUpdated = RecipeLocalTime.getLocalLastUpdated(context)
        FirestoreModel.getAllRecipes(localLastUpdated) { recipes ->
            var lastUpdated = 0L
            for (recipe in recipes) {
                CoroutineScope(Dispatchers.IO).launch {
                    recipeDao.insert(recipe)
                }
                if (lastUpdated < recipe.lastUpdated) {
                    lastUpdated = recipe.lastUpdated
                }
            }
            RecipeLocalTime.setLocalLastUpdated(context, lastUpdated)
        }
        return allRecipes
    }

    fun getByCategory(category: String): LiveData<List<Recipe>> {
        return recipeDao.getByCategory(category)
    }

    fun getByOwner(ownerId: String): LiveData<List<Recipe>> {
        return recipeDao.getByOwner(ownerId)
    }

    fun createRecipe(recipe: Recipe, listener: (Recipe) -> Unit) {
        val newRecipe = recipe.copy()
        if (newRecipe.imageUri == "null") {
            createRecipeWithoutUploadingImage(newRecipe, listener)
        } else {
            FirestoreModel.uploadImage(newRecipe.imageUri.toUri(), onSuccess = { imageUri ->
                newRecipe.imageUri = imageUri
                createRecipeWithoutUploadingImage(newRecipe, listener)
            }, onFailure = {
                // Handle failure
            })
        }
    }

    private fun createRecipeWithoutUploadingImage(recipe: Recipe, listener: (Recipe) -> Unit) {
        FirestoreModel.createRecipe(recipe){recipeWithId ->
            CoroutineScope(Dispatchers.IO).launch {
                recipeDao.insert(recipeWithId)
                listener(recipeWithId)
            }
        }
    }
}

