package com.example.greenchef.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.greenchef.DataClass.User
import com.example.greenchef.Repositories.FirestoreRepository


class UserViewModel : ViewModel() {

    private val repository = FirestoreRepository()
    private val _userData = MutableLiveData<User>()
    val userData: LiveData<User> get() = _userData

    fun fetchUserData(userId: String) {
        repository.getUserData(userId).observeForever {
            _userData.value = it
        }
    }

    fun updateUser(user: User) {
        repository.updateUser(user)
    }

    fun addUserRecipe(userId: String, recipeId: String) {
        repository.addUserRecipe(userId, recipeId)
    }

    fun addUserFavoriteRecipe(userId: String, recipeId: String) {
        repository.addUserFavoriteRecipe(userId, recipeId)
    }

    // ... other methods
}