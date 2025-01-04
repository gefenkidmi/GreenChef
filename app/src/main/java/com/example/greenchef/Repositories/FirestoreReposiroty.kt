package com.example.greenchef.Repositories

import androidx.lifecycle.LiveData
import com.example.greenchef.DataClass.Recipe
import com.example.greenchef.DataClass.User
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class FirestoreRepository {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    // Methods for User data
    fun getUserData(userId: String): LiveData<User> {
        val userDocRef = firestore.collection("users").document(userId)

        return FirestoreDocumentLiveData(userDocRef, User::class.java)
    }

    fun updateUser(user: User) {
        val userDocRef = firestore.collection("users").document(user.userId)
        userDocRef.set(user)
    }

    // Methods for Recipe data
    fun getRecipeData(recipeId: String): LiveData<Recipe> {
        val recipeDocRef = firestore.collection("recipes").document(recipeId)

        return FirestoreDocumentLiveData(recipeDocRef, Recipe::class.java)
    }

    fun updateRecipe(recipe: Recipe) {
        val recipeDocRef = firestore.collection("recipes").document(recipe.recipeId)
        recipeDocRef.set(recipe)
    }

    // Additional methods for retrieving lists of recipes
    fun getAllRecipes(): LiveData<List<Recipe>> {
        val recipesCollectionRef = firestore.collection("recipes")

        return FirestoreCollectionLiveData(recipesCollectionRef, Recipe::class.java)
    }

    fun getRecipesByCategory(category: String): LiveData<List<Recipe>> {
        val recipesCollectionRef = firestore.collection("recipes")
            .whereEqualTo("category", category)

        return FirestoreCollectionLiveData(recipesCollectionRef, Recipe::class.java)
    }

    // Additional methods for updating user's recipes and favorites
    fun addUserRecipe(userId: String, recipeId: String) {
        val userDocRef = firestore.collection("users").document(userId)
        userDocRef.update("recipeIds", FieldValue.arrayUnion(recipeId))
    }

    fun addUserFavoriteRecipe(userId: String, recipeId: String) {
        val userDocRef = firestore.collection("users").document(userId)
        userDocRef.update("favoriteRecipeIds", FieldValue.arrayUnion(recipeId))
    }
}
