package com.example.greenchef.Repositories

import android.net.Uri
import android.util.Log
import com.example.greenchef.DataClass.User
import com.example.greenchef.Models.FirestoreModel
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*

class UserRepository {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()

    // Function to initialize user document
    private fun initializeUserDocument(userId: String) {
        val initialUserData = hashMapOf(
            "userId" to userId,
            "name" to "",
            "photoUrl" to "",
            "recipeIds" to emptyList<String>(),
            "favoriteRecipeIds" to emptyList<String>(),
            "ratedRecipes" to emptyMap<String, Int>()
        )

        db.collection("users")
            .document(userId)
            .set(initialUserData, SetOptions.mergeFields("userId"))
            .addOnSuccessListener {
                // Document initialization successful
                Log.d("initializeUser", "success")
            }
            .addOnFailureListener { exception ->
                // Handle failure
                Log.d("initializeUser", "failed: ${exception.message}")
            }
    }

    // Function to fetch user data from Firestore
    fun fetchUser(userId: String, onSuccess: (User) -> Unit, onFailure: () -> Unit) {
        db.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                if(document.exists()) {
                    onSuccess(document.toObject(User::class.java)!!)
                } else {
                    initializeUserDocument(userId)
                    onSuccess(User(userId, "", "", emptyList(), emptyList(), emptyMap()))
                }
            }
            .addOnFailureListener { exception ->
                // Handle failure
                Log.d("fetchUser", "failed: ${exception.message}")
                onFailure()
            }
    }

    // Function to update user data in Firestore
    fun updateUser(user: User, onSuccess: () -> Unit, onFailure: () -> Unit) {
        db.collection("users")
            .document(user.userId)
            .set(user)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                // Handle failure
                Log.d("updateUser", "failed: ${exception.message}")
                onFailure()
            }
    }

    // Function to update user name in Firestore
    fun updateUserName(
        userId: String,
        newName: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        db.collection("users")
            .document(userId)
            .update("name", newName)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                // Handle failure
                Log.d("updateUserName", "failed: ${exception.message}")
                onFailure()
            }
    }

    // Function to update user recipe IDs in Firestore
    fun updateUserRecipeIds(
        userId: String,
        newRecipeIds: List<String>,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        val fieldUpdate = mapOf(
            "recipeIds" to FieldValue.arrayUnion(*newRecipeIds.toTypedArray())
        )

        db.collection("users")
            .document(userId)
            .update(fieldUpdate)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                // Handle failure
                Log.d("updateUserRecipeIds", "failed: ${exception.message}")
                onFailure()
            }
    }

    // Function to update user favorite recipe IDs in Firestore
    fun updateUserFavoriteRecipeId(
        userId: String,
        newFavoriteRecipeId: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        val fieldUpdate = mapOf(
            "favoriteRecipeIds" to FieldValue.arrayUnion(newFavoriteRecipeId)
        )

        db.collection("users")
            .document(userId)
            .update(fieldUpdate)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                // Handle failure
                Log.d("updateUserFavoriteRecipeIds", "failed: ${exception.message}")
                onFailure()
            }
    }

    fun removeUserFavoriteRecipeId(
        userId: String,
        recipeId: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        val fieldUpdate = mapOf(
            "favoriteRecipeIds" to FieldValue.arrayRemove(recipeId)
        )

        db.collection("users")
            .document(userId)
            .update(fieldUpdate)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                // Handle failure
                Log.d("removeUserFavoriteRecipeIds", "failed: ${exception.message}")
                onFailure()
            }
    }

    // Function to update user rated recipes in Firestore
    fun updateUserRatedRecipes(
        userId: String,
        newRatedRecipes: Map<String, Int>,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        db.collection("users")
            .document(userId)
            .update("ratedRecipes", newRatedRecipes)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                // Handle failure
                Log.d("updateUserRatedRecipes", "failed: ${exception.message}")
                onFailure()
            }
    }

    // Function to update user photo URL in Firestore
    fun updateUserPhoto(userId: String, imageUri: Uri, onSuccess: (String) -> Unit, onFailure: () -> Unit) {
        FirestoreModel.uploadImage(imageUri,
            onSuccess = { imageUrl ->
                // Update the user document with the new photoUrl
                db.collection("users")
                    .document(userId)
                    .update("photoUrl", imageUrl)
                    .addOnSuccessListener {
                        // Photo URL updated successfully
                        onSuccess(imageUrl)
                    }
                    .addOnFailureListener { exception ->
                        // Handle failure
                        Log.d("updateUserPhoto", "failed to update photoUrl: ${exception.message}")
                        onFailure()
                    }
            },
            onFailure = onFailure
        )
    }

    fun removeUserRatedRecipe(userId: String, recipeId: String, onSuccess: () -> Unit, onFailure: () -> Unit) {
        val fieldUpdate = mapOf(
            "ratedRecipes.$recipeId" to FieldValue.delete()
        )

        db.collection("users")
            .document(userId)
            .update(fieldUpdate)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                // Handle failure
                Log.d("removeUserRatedRecipe", "failed: ${exception.message}")
                onFailure()
            }
    }

    fun addUserRatedRecipe(userId: String, recipeId: String, rating: Float, onSuccess: () -> Unit, onFailure: () -> Unit) {
        val fieldUpdate = mapOf(
            "ratedRecipes.$recipeId" to rating
        )

        db.collection("users")
            .document(userId)
            .update(fieldUpdate)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                // Handle failure
                Log.d("addUserRatedRecipe", "failed: ${exception.message}")
                onFailure()
            }
    }
}