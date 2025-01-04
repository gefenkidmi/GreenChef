package com.example.greenchef.DB

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.greenchef.Dao.RecipeDao
import com.example.greenchef.DataClass.Recipe

@Database(entities = [Recipe::class], version = 1)
@TypeConverters(Converters::class)
abstract class LocalDatabase : RoomDatabase(){
    abstract fun recipeDao(): RecipeDao
}