package com.example.greenchef.Models

import android.net.Uri
import android.util.Log
import com.example.greenchef.DataClass.Comment
import com.example.greenchef.DataClass.Recipe
import com.example.greenchef.DataClass.User
import com.example.greenchef.Objects.GlobalVariables
import com.example.greenchef.Objects.RecipeLocalTime
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

object FirestoreModel {
    fun getAllRecipes(
        since: Long,
        coroutineScope: CoroutineScope,
        listener: (List<Recipe>) -> Unit
    ) {
        coroutineScope.launch {
            val db = FirebaseFirestore.getInstance()
            try {
                val result = db.collection("recipes")
                    .whereGreaterThan(RecipeLocalTime.LAST_UPDATED, since)
                    .get()
                    .await()

                val recipes = mutableListOf<Recipe>()
                for (document in result) {
                    val recipe = document.toObject(Recipe::class.java)

                    // Fetch comments for each recipe
                    val commentsResult = db.collection("recipes")
                        .document(recipe.recipeId)
                        .collection("comments")
                        .get()
                        .await()

                    // Map comments to the respective recipe
                    val comments =
                        commentsResult.documents.map { it.toObject(Comment::class.java)!! }
                    recipe.comments = comments

                    // Query the users collection using ownerId
                    val ownerResult = db.collection("users").document(recipe.ownerId).get().await()
                    ownerResult?.let { documentSnapshot ->
                        val user = documentSnapshot.toObject(User::class.java)
                        if (user != null) {
                            recipe.owner = user
                        }
                    }

                    recipes.add(recipe)
                }
                listener(recipes)
            } catch (e: Exception) {
                Log.e("getAllRecipes", "Error fetching recipes", e)
                // Handle error
            }
        }
    }


    fun checkForDeletedRecipes(recipeIdsList: List<String>, listener: (List<String>) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val collectionReference = db.collection("recipes")

        val tasks: List<Task<DocumentSnapshot>> = recipeIdsList.map { recipeId ->
            collectionReference.document(recipeId).get()
        }

        // Use Tasks.whenAllSuccess to wait for all tasks to complete
        Tasks.whenAllSuccess<DocumentSnapshot>(tasks)
            .addOnCompleteListener { result ->
                if (result.isSuccessful) {
                    val deletedRecipeIds: MutableList<String> = mutableListOf()

                    for (i in 0 until result.result?.size!!) {
                        val documentSnapshot = result.result[i]
                        if (!documentSnapshot.exists()) {
                            deletedRecipeIds.add(recipeIdsList[i])
                        }
                    }
                    listener(deletedRecipeIds)
                }
            }
    }

    fun createRecipe(recipe: Recipe, listener: (Recipe) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val newRecipeRef = db.collection("recipes").document()
        val newRecipe = recipe.copy(recipeId = newRecipeRef.id)
        newRecipeRef.set(newRecipe)
            .addOnSuccessListener {
                listener(newRecipe)
            }
            .addOnFailureListener {
                Log.d("createRecipe", "failed: ${it.message}")
            }
    }

    fun deleteRecipe(recipeId: String, listener: () -> Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("recipes")
            .document(recipeId)
            .delete()
            .addOnSuccessListener {
                listener()
            }
            .addOnFailureListener {
                Log.d("deleteRecipe", "failed: ${it.message}")
            }
    }

    fun updateRecipe(recipe: Recipe, listener: () -> Unit) {
        val db = FirebaseFirestore.getInstance()
        Log.d("updateRecipe", "Document reference: recipes/${recipe.recipeId}")

        db.collection("recipes")
            .document(recipe.recipeId)
            .set(recipe, SetOptions.merge())
            .addOnSuccessListener {
                Log.d("updateRecipe", "Update successful for recipeId: ${recipe.recipeId}")
                listener()
            }
            .addOnFailureListener { Log.d("updateRecipe", "failed: ${it.message}") }
    }

    // Function to upload an image to Firestore Storage and get the URL
    fun uploadImage(imageUri: Uri, onSuccess: (String) -> Unit, onFailure: () -> Unit) {
        val storageRef: StorageReference = FirebaseStorage.getInstance().reference
        val imageFileName = UUID.randomUUID().toString() // Generate a unique filename for the image
        val imageRef: StorageReference = storageRef.child("user_images/$imageFileName")

        // Upload the image to Firebase Storage
        imageRef.putFile(imageUri)
            .addOnSuccessListener { taskSnapshot ->
                // Image uploaded successfully
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    // Get the download URL of the uploaded image
                    val imageUrl = uri.toString()
                    onSuccess(imageUrl)
                }
            }
            .addOnFailureListener { exception ->
                // Handle failure
                Log.d("uploadImage", "failed: ${exception.message}")
                onFailure()
            }
    }

    fun addCommentToRecipe(
        recipeId: String,
        commentText: String,
        listener: (Comment) -> Unit
    ) {
        val firestore = FirebaseFirestore.getInstance()

        // Generate a unique ID for the new comment
        val commentId =
            firestore.collection("recipes").document(recipeId).collection("comments").document().id

        // Create a Comment object
        val comment = Comment(
            commentId,
            commentText,
            System.currentTimeMillis(),
            GlobalVariables.currentUser!!.userId
        )

        // Add the comment document to the comments subcollection
        firestore.collection("recipes").document(recipeId).collection("comments")
            .document(commentId).set(comment).addOnSuccessListener {
                // Comment added successfully
                Log.d("Add comment to recipe", "Comment added successfully")
                listener(comment)
            }.addOnFailureListener { e ->
                // Handle any errors
                Log.e("Add comment to recipe", "Error adding comment", e)
            }
    }
}