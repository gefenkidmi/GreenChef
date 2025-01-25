package com.example.greenchef.DB

import android.content.Context
import androidx.room.Room

object RecipeDatabase {
    fun db(context: Context): LocalDatabase {
        return Room.databaseBuilder(context, LocalDatabase::class.java, "recipe-database").fallbackToDestructiveMigration().build()
    }
}