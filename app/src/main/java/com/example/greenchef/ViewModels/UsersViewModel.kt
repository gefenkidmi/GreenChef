package com.example.greenchef.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.greenchef.DataClass.User
import com.example.greenchef.Repositories.UserRepository

class UsersViewModel : ViewModel() {

    private val userRepository = UserRepository()
    private val _usersLiveData: MutableLiveData<List<User>> = MutableLiveData()
    val usersLiveData: LiveData<List<User>> get() = _usersLiveData

    init {
        userRepository.getAllUsers(
            onSuccess = { users ->
                _usersLiveData.postValue(users)
            },
            onFailure = {
                // Handle failure
            }
        )
    }

    fun getUserById(userId: String, listener: (User) -> Unit) {
        userRepository.fetchUser(userId, onSuccess = { user ->
            listener(user)
        }, onFailure = {
            // Handle failure
        })
    }
}