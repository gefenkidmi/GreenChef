package com.example.greenchef.ViewModels

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.greenchef.DataClass.User
import com.example.greenchef.Repositories.UserRepository
import java.util.*

class UserViewModel(private val userId: String) : ViewModel() {

    private val userRepository = UserRepository()
    private val _userLiveData: MutableLiveData<User> = MutableLiveData()
    val userLiveData: LiveData<User> get() = _userLiveData

    init {
        // Initialize user document on initialization
        userRepository.initializeUserDocument(userId)
        // Fetch user data on initialization
        fetchUser()
    }

    private fun fetchUser() {
        userRepository.fetchUser(userId,
            onSuccess = { user ->
                _userLiveData.postValue(user)
            },
            onFailure = {
                // Handle failure
            }
        )
    }

    fun updateUser(user: User) {
        userRepository.updateUser(user,
            onSuccess = {
                // After a successful update, fetch the user again to reflect changes
                fetchUser()
            },
            onFailure = {
                // Handle failure
            }
        )
    }

    fun updateUserName(newName: String) {
        userRepository.updateUserName(userId, newName,
            onSuccess = {
                // After a successful update, fetch the user again to reflect changes
                fetchUser()
            },
            onFailure = {
                // Handle failure
            }
        )
    }

    fun updateUserPhoto(imageUri: Uri) {
        userRepository.updateUserPhoto(userId, imageUri,
            onSuccess = { newPhotoUrl ->
                // After a successful update, fetch the user again to reflect changes
                fetchUser()
            },
            onFailure = {
                // Handle failure
            }
        )
    }

    fun updateUserRecipeIds(newRecipeIds: List<String>) {
        userRepository.updateUserRecipeIds(userId, newRecipeIds,
            onSuccess = {
                // After a successful update, fetch the user again to reflect changes
                fetchUser()
            },
            onFailure = {
                // Handle failure
            }
        )
    }

    fun updateUserFavoriteRecipeIds(newFavoriteRecipeIds: List<String>) {
        userRepository.updateUserFavoriteRecipeIds(userId, newFavoriteRecipeIds,
            onSuccess = {
                // After a successful update, fetch the user again to reflect changes
                fetchUser()
            },
            onFailure = {
                // Handle failure
            }
        )
    }

    fun updateUserRatedRecipes(newRatedRecipes: Map<String, Int>) {
        userRepository.updateUserRatedRecipes(userId, newRatedRecipes,
            onSuccess = {
                // After a successful update, fetch the user again to reflect changes
                fetchUser()
            },
            onFailure = {
                // Handle failure
            }
        )
    }
}