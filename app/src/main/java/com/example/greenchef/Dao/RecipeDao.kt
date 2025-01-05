package com.example.greenchef.Dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.greenchef.DataClass.Recipe

@Dao
interface RecipeDao {
    @Query("SELECT * FROM recipes")
    fun getAll(): LiveData<List<Recipe>>

    @Query("SELECT * FROM recipes WHERE category = :category")
    fun getByCategory(category: String): LiveData<List<Recipe>>

    @Query("SELECT * FROM recipes WHERE ownerId = :ownerId")
    fun getByOwner(ownerId: String): LiveData<List<Recipe>>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(recipe: Recipe)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(recipe: Recipe)

    @Query("DELETE FROM recipes WHERE recipeId = :recipeId")
    fun deleteById(recipeId: String)
}