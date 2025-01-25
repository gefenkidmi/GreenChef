package com.example.greenchef.ViewModels

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.greenchef.DataClass.User
import com.example.greenchef.Objects.GlobalVariables
import com.example.greenchef.Repositories.UserRepository
import com.google.firebase.firestore.GeoPoint
import java.util.*

class UserViewModel(private val userId: String) : ViewModel() {

    private val userRepository = UserRepository()
    private val _userLiveData: MutableLiveData<User> = MutableLiveData()
    val userLiveData: LiveData<User> get() = _userLiveData

    init {
        fetchUser()
    }

    private fun fetchUser() {
        userRepository.fetchUser(userId,
            onSuccess = { user ->
                _userLiveData.postValue(user)
                GlobalVariables.currentUser = user
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

    fun removeUserRecipe(recipeId: String, onSuccess: () -> Unit) {
        userRepository.removeUserRecipe(userId, recipeId,
            onSuccess = {
                // After a successful update, fetch the user again to reflect changes
                fetchUser()
                onSuccess()
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

    fun updateUserRecipeIds(newRecipeIds: List<String>, onSuccess: () -> Unit, onFailure: () -> Unit) {
        userRepository.updateUserRecipeIds(userId, newRecipeIds,
            onSuccess = {
                // After a successful update, fetch the user again to reflect changes
                fetchUser()
                onSuccess()
            },
            onFailure = {
                onFailure()
                // Handle failure
            }
        )
    }

    fun updateUserFavoriteRecipeId(newFavoriteRecipeIds: String) {
        userRepository.updateUserFavoriteRecipeId(userId, newFavoriteRecipeIds,
            onSuccess = {
                // After a successful update, fetch the user again to reflect changes
                fetchUser()
            },
            onFailure = {
                // Handle failure
            }
        )
    }

    fun removeUserFavoriteRecipeId(favoriteRecipeId: String) {
        userRepository.removeUserFavoriteRecipeId(userId, favoriteRecipeId,
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

    fun removeUserRatedRecipe(recipeId: String) {
        userRepository.removeUserRatedRecipe(userId, recipeId,
            onSuccess = {
                // After a successful update, fetch the user again to reflect changes
                fetchUser()
            },
            onFailure = {
                // Handle failure
            }
        )
    }

    fun addUserRatedRecipe(recipeId: String, rating: Float) {
        userRepository.addUserRatedRecipe(userId, recipeId, rating,
            onSuccess = {
                // After a successful update, fetch the user again to reflect changes
                fetchUser()
            },
            onFailure = {
                // Handle failure
            }
        )
    }

    fun updateGeoLocation(lat: Double, lon: Double) {
        userRepository.updateGeoPoint(userId, GeoPoint(lat, lon),
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